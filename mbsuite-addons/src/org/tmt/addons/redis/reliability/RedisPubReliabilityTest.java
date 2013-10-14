package org.tmt.addons.redis.reliability;

import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import com.persistent.bcsuite.base.PublisherBase;
import java.util.concurrent.TimeUnit;

/**
 * 
 * For this publisher program we use 'redis' java client which is 'Jedis'.
 * 
 */
public class RedisPubReliabilityTest extends PublisherBase {

	private static final Logger logger = Logger
			.getLogger(RedisPubReliabilityTest.class.getName());
	private String serverHost;
	private String serverPort;
	private String channel;
	private Jedis publisherJedis;
	private static JedisPool jedisPool;
	private static JedisPoolConfig poolConfig;
	private Pipeline pipeline;
	private int throttlingFactor = 1000;
	private String sleepTime;
	private Tracker t = null;

	/**
	 * Initialize the Redis parameters for Publisher.
	 */
	@Override
	public void init(Map<String, String> attributes) {
		logger.info("RedisPublisher Initializing");
		super.init(attributes);

		/*
		 * 'serverHost' is the ip-address & 'serverPort' is the port and
		 * 'channel' is topic for redis Publisher to connect.
		 */
		serverHost = attributes.get("host-ip");
		serverPort = attributes.get("host-port");
		channel = attributes.get("topic");
		sleepTime = attributes.get("sleep-time");
		String tf = attributes.get("throttlingFactor");
		logger.info("Topic Name from suite [" + channel
				+ "] & throttlingFactor [" + throttlingFactor + "]");
		try {
			throttlingFactor = Integer.parseInt(tf);
			logger.debug("Throttling factor " + throttlingFactor);
		} catch (Exception e) {
			logger.error("Cannot parse throttling factor.. setting default as 1000");
			throttlingFactor = 1000;
		}

		initializeMessagePlatform();

		logger.info("Sample Publisher initialization complete...");
	}

	private void initializeMessagePlatform() {

		poolConfig = new JedisPoolConfig();

		// All of bellowing parameters are useful for Publisher performance
		// tuning.
		if (poolConfig == null) {
			poolConfig.setMaxActive(100);
			poolConfig.setMaxIdle(5);
			poolConfig.setMinIdle(1);
			poolConfig.setTestOnBorrow(true);
			poolConfig.setTestOnReturn(true);
			poolConfig.setTestWhileIdle(true);
			poolConfig.setNumTestsPerEvictionRun(10);
			poolConfig.setTimeBetweenEvictionRunsMillis(60000);
			poolConfig.setMaxWait(3000);
		}

		// create the jedis pool and connect to an port & ip-address which is
		// specified.
		jedisPool = new JedisPool(poolConfig, serverHost,
				Integer.parseInt(serverPort), 0);

		logger.info("Publisher connecting to server at IP [" + serverHost
				+ "] , port [" + serverPort + "]" + "channel [" + channel + "]");

		publisherJedis = jedisPool.getResource();

		logger.info("RedisPublisher inited");

	}

	@Override
	public boolean isReadyToRun() {
		return true;
	}

	/**
	 * Sends messages to an topic.
	 */
	@Override
	public void sendMessageForThroughput(byte[] message) {

		// create the pipeline for performance perspective for publishing
		// messages.
		pipeline = publisherJedis.pipelined();

		logger.info("sendMessageForThroughput started.");

		setStartTime(new Date());
		String mStr = new String(message);
		try {
			// If we require throttled throughput
			t = new Tracker();
			t.start();
			if (throttlingFactor > 0) {
				while (canContinue) {
					long beforeSend = System.currentTimeMillis();
					for (int i = 0; i < throttlingFactor && canContinue(); i++) {
						pipeline.publish(channel, mStr);
						t.incrementCounter();
					}
					long afterSend = System.currentTimeMillis();
					long timeLeftInMillis = 100 - (afterSend - beforeSend);

					if (timeLeftInMillis > 0) {
						TimeUnit.MILLISECONDS.sleep(timeLeftInMillis);
					}
				}
			} else {
				// If we require throttling disabled
				while (canContinue) {
					pipeline.publish(channel, mStr);
					t.incrementCounter();
					if (getThinkTimeInMillis() > 0)
						Thread.sleep(getThinkTimeInMillis());
					if (t.getCounter() % 15000 == 0) {
						pipeline.sync();
						pipeline = publisherJedis.pipelined();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception encountered in Publisher program");
			e.printStackTrace();
		} finally {
			addToStatisticsPool(message.length);
			setEndTime(new Date()); // IMPORTANT !!
			markTaskComplete(); // IMPORTANT !!
			logger.info("sendMessageForThroughput completed.");
		}
	}

	@Override
	public void sendMessageForLatency(byte[] message) {
	}

	/**
	 * Close pipeline,jedispool, & publisher
	 */
	@Override
	public void cleanup() {
		try {
			logger.info("Tracker ended");
			pipeline.sync();
			jedisPool.returnResource(publisherJedis);
			if (publisherJedis.isConnected())
				publisherJedis.disconnect();
			logger.info("Publisher closed");
		} catch (Exception e) {
			logger.info("cleanup() Exception");
			e.printStackTrace();
		}
	}

	/*
	 * This reliability test dumps sent count in log file after every
	 * 'sleepTime' (sleepTime attribute is taken from 'publisher-config.tmpl'
	 * file) after every 'sleepTime' the 'sentCounter' is reset to 0
	 */

	public class Tracker extends Thread {
		public boolean isRunning = false;
		private int sentCounter = 0;
		private int iteration = 1;

		@Override
		public void run() {
			logger.info("Tracker started");
			isRunning = true;
			while (canContinue()) {
				try {
					Thread.sleep(Long.parseLong(sleepTime));
					logger.info("sent," + iteration + "," + sentCounter);
					iteration++;
					resetCounter();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public int getCounter() {
			return sentCounter;
		}

		public void incrementCounter() {
			sentCounter++;
		}

		public void resetCounter() {
			sentCounter = 0;
		}

	}

}