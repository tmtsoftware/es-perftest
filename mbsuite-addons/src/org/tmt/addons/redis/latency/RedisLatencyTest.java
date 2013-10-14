package org.tmt.addons.redis.latency;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import com.persistent.bcsuite.base.PublisherBase;

/**
 * 
 * For this publisher program we use 'redis' java client which is 'Jedis'.
 * 
 */
public class RedisLatencyTest extends PublisherBase {

	private static final Logger logger = Logger
			.getLogger(RedisLatencyTest.class.getName());

	private static JedisPool jedisPoolSend;
	private static JedisPool jedisPoolRecv;
	private static JedisPoolConfig poolConfigSend;
	private static JedisPoolConfig poolConfigRecv;
	private Jedis publisherJedis;
	private Jedis subscriberJedis;
	private Pipeline pipeline;
	private RedisInner redisInner;
	private String latencyCaptureWindow;
	private String serverHost;
	private String serverPort;
	private String channel;
	private int sendCnt = 0;
	private int recvCnt = 0;
	private long sendTime = 0;
	private long recvTime = 0;
	private int mcounter = 0;
	private int throttlingFactor = 1000;

	/**
	 * Initialize the Redis parameters for Publisher.
	 */
	@Override
	public void init(Map<String, String> attributes) {
		logger.info("RedisPublisherForLAtency Initializing");
		super.init(attributes);

		/*
		 * 'serverHost' is the ip-address & 'serverPort' is the port and
		 * 'channel' is topic for redis Publisher to connect.
		 */
		serverHost = attributes.get("host-ip");
		serverPort = attributes.get("host-port");
		channel = attributes.get("topic");
		latencyCaptureWindow = attributes.get("latencyCaptureWindow");
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

		poolConfigSend = new JedisPoolConfig();

		// All of bellowing parameters are useful for Publisher performance
		// tuning.
		if (poolConfigSend == null) {
			poolConfigSend.setMaxActive(100);
			poolConfigSend.setMaxIdle(5);
			poolConfigSend.setMinIdle(1);
			poolConfigSend.setTestOnBorrow(true);
			poolConfigSend.setTestOnReturn(true);
			poolConfigSend.setTestWhileIdle(true);
			poolConfigSend.setNumTestsPerEvictionRun(10);
			poolConfigSend.setTimeBetweenEvictionRunsMillis(60000);
			poolConfigSend.setMaxWait(3000);
		}

		// create the jedis pool and connect to an port & ip-address which is
		// specified.
		jedisPoolSend = new JedisPool(poolConfigSend, serverHost,
				Integer.parseInt(serverPort), 0);

		logger.info("Publisher connecting to server at IP [" + serverHost
				+ "] , port [" + serverPort + "]" + "channel [" + channel + "]");

		publisherJedis = jedisPoolSend.getResource();
		logger.info("RedisPublisherForLatency inited");
		initSubscriber(attributes);

	}

	/**
	 * Initialize the Redis parameters for Subscriber.
	 */
	private void initSubscriber(Map<String, String> attributes) {
		logger.info("RedisSubscriberForLatency  Initializing");

		serverHost = attributes.get("host-ip");
		serverPort = attributes.get("host-port");
		channel = attributes.get("topic");
		logger.info("Subscriber Topic Name from suite [" + channel + "]");

		if (latencyCaptureWindow == null) {
			latencyCaptureWindow = String.valueOf(10);
		}

		poolConfigRecv = new JedisPoolConfig();

		// All of bellowing parameters are useful for Publisher performance
		// tuning.
		if (poolConfigRecv == null) {
			poolConfigRecv.setMaxActive(100);
			poolConfigRecv.setMaxIdle(5);
			poolConfigRecv.setMinIdle(1);
			poolConfigRecv.setTestOnBorrow(true);
			poolConfigRecv.setTestOnReturn(true);
			poolConfigRecv.setTestWhileIdle(true);
			poolConfigRecv.setNumTestsPerEvictionRun(10);
			poolConfigRecv.setTimeBetweenEvictionRunsMillis(60000);
			poolConfigRecv.setMaxWait(3000);
		}

		// create the jedis pool and connect to an port & ip-address which is
		// specified.
		jedisPoolRecv = new JedisPool(poolConfigRecv, serverHost,
				Integer.parseInt(serverPort), 0);

		logger.info("Subscriber connecting to server at IP [" + serverHost
				+ "] , port [" + serverPort + "]" + "chanel [" + channel + "]");
		logger.info("RedisSubscriber inited");

		subscriberJedis = jedisPoolRecv.getResource();
		redisInner = new RedisInner();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Subscribing to {" + channel
							+ "}. This thread will be blocked.");
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
		int cnt = 0;

		public void onMessage(String channel, String msg) {
			try {
				recvTime = System.nanoTime();
				mcounter++;
				if (mcounter > Integer.parseInt(latencyCaptureWindow)) {
					addLatencyStatistics(String.valueOf(mcounter), recvTime
							- sendTime);
					mcounter = 0;
				}
				recvCnt++;
			} catch (Exception e) {
				logger.error("Exception encountered in Echo-Send-Receive program");
				e.printStackTrace();
			}
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
	public void sendMessageForLatency(byte[] message) {
		pipeline = publisherJedis.pipelined();
		setStartTime(new Date());
		String mstr = new String(message);
		try {
			if (throttlingFactor > 0) {
				while (canContinue) {
					if (sendCnt == recvCnt) {
						long beforeSend = System.currentTimeMillis();
						for (int i = 0; i < throttlingFactor && canContinue(); i++) {
							sendTime = System.nanoTime();
							pipeline.publish(channel, mstr);
							sendCnt++;
							addToStatisticsPool(message.length);
						}
						long afterSend = System.currentTimeMillis();
						long timeLeftInMillis = 100 - (afterSend - beforeSend);

						if (timeLeftInMillis > 0) {
							TimeUnit.MILLISECONDS.sleep(timeLeftInMillis);
						}
					}
				}
			} else {
				while (canContinue) {
					if (sendCnt == recvCnt) {
						// Send the message. Now the message contains a unique
						// identifier which is used for calculating latency.
						sendTime = System.nanoTime();
						pipeline.publish(channel, mstr);
						sendCnt++;
						// add data to statistics pool. This will count the
						// number
						// of messages and the total bytes sent.
						addToStatisticsPool(message.length);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			setEndTime(new Date());
			markTaskComplete();
		}
	}

	@Override
	public boolean isReadyToRun() {
		return true;
	}

	@Override
	public void sendMessageForThroughput(byte[] message) {
	}

	/**
	 * Close pipeline,jedispool, & publisher
	 */
	@Override
	public void cleanup() {
		pipeline.sync();
		System.out.println("Sent [" + sendCnt + "], recd [" + recvCnt + "]");
		jedisPoolSend.returnResource(publisherJedis);
		if (publisherJedis.isConnected())
			publisherJedis.disconnect();
		logger.info("Shutdown called");
		redisInner.unsubscribe();
		jedisPoolRecv.returnResource(subscriberJedis);
		if (subscriberJedis.isConnected())
			subscriberJedis.disconnect();
		logger.info("Subscriber closed");
	}

}
