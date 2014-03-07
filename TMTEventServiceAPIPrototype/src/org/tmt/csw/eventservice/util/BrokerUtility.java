package org.tmt.csw.eventservice.util;

import java.util.HashMap;
import java.util.Properties;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;

/**
 * Utility class to connect to the broker instance
 * 
 * @author amit_harsola
 *
 */
public class BrokerUtility {
		/**
	 * 
	 * @return
	 */
	public static ServerLocator connectToBroker(boolean producer) {
		Properties prop = ConfigPropertyLoader.getProperties();

		String brokerHost = prop.getProperty("brokerHost");
		String brokerPort = prop.getProperty("brokerPort");
		
		// All of bellowing parameters are useful for Publisher performance
		// tuning.
		String tcpBuffer = prop.getProperty("tcpBuffer");
		String tcpNoDelay = prop.getProperty("tcpNoDelay") ;
		String preAck = prop.getProperty("preAck") ;
		String useNio = prop.getProperty("useNio");
		String producerRate = prop.getProperty("producerRate");
		String producerWindowSize = prop.getProperty("producerWindowSize");
		String consumerWindowSize = prop.getProperty("consumerWindowSize") ;
		String confirmationWindowSize = prop.getProperty("confirmationWindowSize") ;
		String retryInterval = prop.getProperty("retryInterval");
		String retryIntervalMultiplier = prop.getProperty("retryIntervalMultiplier");
		String maxRetryInterval = prop.getProperty("maxRetryInterval");
		String reconnectAttempts = prop.getProperty("reconnectAttempts") ;
		String useGlobalPool = prop.getProperty("useGlobalPool") ;
		String threadPoolMaxSize = prop.getProperty("threadPoolMaxSize");
		String scheduledThreadPoolMaxSize = prop.getProperty("scheduledThreadPoolMaxSize");

		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.BROKER_HOST_PROPERTY, brokerHost);
		params.put(Constants.BROKER_HOST__PORT_PROPERTY, brokerPort);

		// After setting of all parameters they are added in Map "params".
		params.put(TransportConstants.TCP_NODELAY_PROPNAME, tcpNoDelay);
		params.put(TransportConstants.TCP_SENDBUFFER_SIZE_PROPNAME, tcpBuffer);
		params.put(TransportConstants.TCP_RECEIVEBUFFER_SIZE_PROPNAME, tcpBuffer);
		params.put(TransportConstants.USE_NIO_PROP_NAME, useNio);

	    ServerLocator serverLocator = HornetQClient.createServerLocatorWithoutHA(
	    		new TransportConfiguration(NettyConnectorFactory.class.getName(), params));

		serverLocator.setPreAcknowledge(Boolean.parseBoolean(preAck));			
		
		if (producer) {
		    serverLocator.setUseGlobalPools(Boolean.valueOf(useGlobalPool));
			serverLocator.setScheduledThreadPoolMaxSize(Integer.valueOf(scheduledThreadPoolMaxSize));
			serverLocator.setThreadPoolMaxSize(Integer.valueOf(threadPoolMaxSize));
			serverLocator.setProducerMaxRate(Integer.parseInt(producerRate));
			serverLocator.setProducerWindowSize(Integer.valueOf(producerWindowSize));
			serverLocator.setConfirmationWindowSize(Integer.valueOf(confirmationWindowSize));
			
		}else {
			serverLocator.setConsumerWindowSize(Integer.valueOf(consumerWindowSize));
		}
		
		serverLocator.setReconnectAttempts(Integer.valueOf(reconnectAttempts));
		serverLocator.setRetryInterval(Integer.valueOf(retryInterval));
		serverLocator.setRetryIntervalMultiplier(Integer.valueOf(retryIntervalMultiplier));
		serverLocator.setMaxRetryInterval(Integer.valueOf(maxRetryInterval));
	    
		return serverLocator;
	}
}
