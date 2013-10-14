package org.tmt.addons.redis.reliability;

import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import com.persistent.bcsuite.base.SubscriberBase;

/**
 * 
 * For this subscriber program we use 'redis' java client which is 'Jedis'.
 * 
 */
public class RedisSubReliabilityTest extends SubscriberBase {

	private static final Logger logger = Logger
			.getLogger(RedisSubReliabilityTest.class.getName());

	private String serverHost;
	private String serverPort;
	private String channel;
	private String sleepTime;
	private Jedis subscriberJedis;
	private static JedisPool jedisPool;
	private static JedisPoolConfig poolConfig;
	private RedisInner redisInner;
	private boolean shouldStop = false;
	private Tracker t = null;
	private Date recdOn;

	@Override
	public void init(Map<String, String> attributes) {
		logger.info("RedisSubscriber Initializing");

		/*
		 * 'serverHost' is the ip-address & 'serverPort' is the port and
		 * 'channel' is topic for redis Subscriber to connect.
		 */
		serverHost = attributes.get("host-ip");
		serverPort = attributes.get("host-port");
		channel = attributes.get("topic");
		sleepTime = attributes.get("sleep-time");
		logger.info("Topic Name from suite [" + channel + "]");

		poolConfig = new JedisPoolConfig();

		// All of bellowing parameters are useful for Subscriber performance
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

		logger.info("Subscriber connecting to server at IP [" + serverHost
				+ "] , port [" + serverPort + "]" + "chanel [" + channel + "]");
		logger.info("RedisSubscriber inited");

		subscriberJedis = jedisPool.getResource();
		redisInner = new RedisInner();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Subscribing to {" + channel
							+ "}. This thread will be blocked.");
					t = new Tracker();
					t.start();
					subscriberJedis.subscribe(redisInner, channel);
					System.out.println("Subscription ended.");
				} catch (Exception e) {
					System.out.println("Subscribing failed." + e);
				}
			}
		}).start();
	}

	/**
	 * Receives messages from topic.
	 */
	class RedisInner extends JedisPubSub {

		public void onMessage(String channel, String msg) {
			recdOn = new Date();
			t.incrementCounter();
		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {
		}

		@Override
		public void onSubscribe(String channel, int subscribedChannels) {
		}

		@Override
		public void onUnsubscribe(String channel, int subscribedChannels) {
		}

		@Override
		public void onPUnsubscribe(String pattern, int subscribedChannels) {
		}

		@Override
		public void onPSubscribe(String pattern, int subscribedChannels) {
		}

	}

	@Override
	public void read() {

	}

	/**
	 * Close pipeline,jedispool, & publisher
	 */
	@Override
	public void shutdown() {
		try {
			addToStatisticsPool(null, 1, recdOn);
			logger.info("Shutdown called");
			redisInner.unsubscribe();
			jedisPool.returnResource(subscriberJedis);
			if (subscriberJedis.isConnected())
				subscriberJedis.disconnect();
			logger.info("Subscriber closed");
		} catch (Exception e) {
			logger.info("shutdown() Exception");
			e.printStackTrace();
		} finally {
			markTaskComplete();
			shouldStop = true;
		}
	}

	/*
	 * This reliability test dumps recv count in log file after every
	 * 'sleepTime' (sleepTime attribute is taken from 'subscriber-config.tmpl'
	 * file) after every 'sleepTime' the 'recvCounter' is reset to 0
	 */
	public class Tracker extends Thread {
		public boolean isRunning = false;
		private int recvCounter = 0;
		private int iteration = 1;

		@Override
		public void run() {
			logger.info("Tracker started");
			isRunning = true;
			while (!shouldStop) {
				{
					try {
						Thread.sleep(Long.parseLong(sleepTime));
						logger.info("recv," + iteration + "," + recvCounter);
						iteration++;
						resetCounter();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}

		public void incrementCounter() {
			recvCounter++;
		}

		public void resetCounter() {
			recvCounter = 0;
		}

	}

}