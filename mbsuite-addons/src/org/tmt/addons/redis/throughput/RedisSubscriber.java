package org.tmt.addons.redis.throughput;

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

public class RedisSubscriber extends SubscriberBase {

	private static final Logger logger = Logger.getLogger(RedisSubscriber.class
			.getName());
	private String serverHost;
	private String serverPort;
	private String channel;
	private RedisInner redisInner;
	private Jedis subscriberJedis;
	private static JedisPool jedisPool;
	private static JedisPoolConfig poolConfig;

	/**
	 * Initialize the Redis parameters for Subscriber.
	 */
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
			Date recdOn = new Date();
			addToStatisticsPool(null, msg.length(), recdOn);
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
		}
	}

}
