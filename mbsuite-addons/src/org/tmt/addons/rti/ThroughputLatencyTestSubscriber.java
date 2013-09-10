package org.tmt.addons.rti;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.tmt.addons.rti.latency.Latency;
import org.tmt.addons.rti.latency.LatencyDataReader;
import org.tmt.addons.rti.latency.LatencyDataWriter;
import org.tmt.addons.rti.latency.LatencySeq;
import org.tmt.addons.rti.latency.LatencyTypeSupport;

import com.persistent.bcsuite.base.SubscriberBase;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;
import com.rti.ndds.transport.UDPv4Transport;

/**
 * Subscriber class that subscribes to the publisher and receives messages.
 * Echoes back the messages that it receives.
 * 
 */
public class ThroughputLatencyTestSubscriber extends SubscriberBase {
	private static final Logger logger = Logger
			.getLogger(ThroughputLatencyTestSubscriber.class);
	public static final int RTI_DDS_OVERHEAD = 512;
	public static final int FINAL_SN = -1;
	public int cookie = 1;
	public boolean no_echo = false;
	public int domain_id = 0, transport = 1, multicast_ttl = 1;
	DomainParticipantFactoryQos factory_qos = new DomainParticipantFactoryQos();
	private static DomainParticipantFactory factory = null;
	DomainParticipantQos reciever_participant_qos = new DomainParticipantQos();
	DomainParticipantQos sender_participant_qos = new DomainParticipantQos();
	private static DomainParticipant recieverParticipant = null;
	private static DomainParticipant senderParticipant = null;
	Subscriber subscriber = null;
	Publisher publisher = null;
	Topic data_topic = null, echo_topic = null;
	LatencyListener listener = null;
	DataReaderQos reader_qos = new DataReaderQos();
	LatencyDataReader reader = null;
	DataWriterQos writer_qos = new DataWriterQos();
	LatencyDataWriter writer = null;
	InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;
	private int maxObjectsPerThread;
	private String topicName;
	private static int counter = 0;
	private boolean canRun = true;
	private int numOfDataReaders = 1;
	boolean isReliable = false;

	/**
	 * Initialize all the datareaders, datawriters, subscribers, publishers and
	 * participants.
	 * 
	 */
	@Override
	public void init(Map<String, String> attributes) {
		String mo = attributes.get("maxObjectsPerThread");

		try {
			maxObjectsPerThread = Integer.parseInt(mo);
		} catch (Exception e) {
			logger.error("Cannot parse max-objects-per-thread.. using default as 1024");
			maxObjectsPerThread = 1024;
		}

		String strReliable = attributes.get("isReliable");
		if (strReliable != null && !strReliable.isEmpty()) {
			isReliable = Boolean.parseBoolean(strReliable);
			logger.info("Setting Reliability " + isReliable);
		}

		String nr = attributes.get("numOfDataReaders");

		try {
			numOfDataReaders = Integer.parseInt(nr);
		} catch (Exception e) {
			logger.info("Cannot parse numOfDataReaders.. using default as 1");
			numOfDataReaders = 1;
			;
		}

		logger.info("Using maxObjectsPerThread as [" + maxObjectsPerThread
				+ "]");

		topicName = attributes.get("topic");

		logger.info("Using topic as [" + topicName + "]");
		if (factory == null) { // Once initialized no need to do it again for
			// other threads.
			factory = DomainParticipantFactory.get_instance();
			RTIQosHelper.configure_factory_qos(factory_qos, factory,
					maxObjectsPerThread);
		}
		// Need to change default plugin property

		UDPv4Transport.Property_t property = new UDPv4Transport.Property_t();
		if (recieverParticipant == null) {
			// --- Create participant --- //
			factory.get_default_participant_qos(reciever_participant_qos);

			RTIQosHelper.configure_participant_qos(reciever_participant_qos,
					factory, counter + 40);
			recieverParticipant = factory.create_participant(0,
					reciever_participant_qos, null, // listener
					StatusKind.STATUS_MASK_NONE);
			UDPv4Transport.Property_t udpv4TransportProperty = new UDPv4Transport.Property_t();
			RTIQosHelper.configure_participant_transport(null,
					udpv4TransportProperty, recieverParticipant);
			// // --- Enable to start Discovery ---
			recieverParticipant.enable();
		}

		if (senderParticipant == null) {
			factory.get_default_participant_qos(sender_participant_qos);
			RTIQosHelper.configure_participant_qos(sender_participant_qos,
					factory, counter + 80);
			// Now we can create the 'disabled' participant.
			senderParticipant = factory.create_participant(80,
					sender_participant_qos, null, // listener
					StatusKind.STATUS_MASK_NONE);
			UDPv4Transport.Property_t udpv4TransportProperty = new UDPv4Transport.Property_t();
			RTIQosHelper.configure_participant_transport(null,
					udpv4TransportProperty, senderParticipant);

			senderParticipant.enable();
		}

		subscriber = recieverParticipant.create_subscriber(
				DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		publisher = senderParticipant.create_publisher(
				DomainParticipant.PUBLISHER_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		// --- Create topic --- //

		// Register type before creating topic
		String type_name = LatencyTypeSupport.get_type_name();
		LatencyTypeSupport.register_type(recieverParticipant, type_name);
		LatencyTypeSupport.register_type(senderParticipant, type_name);
		// To customize topic QoS, use
		// participant.get_default_topic_qos() instead of default
		data_topic = recieverParticipant.create_topic(topicName + "-DataTopic",
				type_name, DomainParticipant.TOPIC_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		echo_topic = senderParticipant.create_topic(topicName + "-EchoTopic",
				type_name, DomainParticipant.TOPIC_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		// echo writer
		publisher.get_default_datawriter_qos(writer_qos);

		int max_gather_send_buffers = 16;

		RTIQosHelper.configure_data_writer_qos(writer_qos, false,
				max_gather_send_buffers, publisher, false, false, isReliable,
				counter);

		writer = (LatencyDataWriter) publisher.create_datawriter(echo_topic,
				writer_qos, null, // listener
				StatusKind.STATUS_MASK_NONE);
		// --- Create reader --- //
		listener = new LatencyListener(writer, instance_handle, cookie);

		subscriber.get_default_datareader_qos(reader_qos);

		RTIQosHelper.configure_data_reader_qos(reader_qos, subscriber,
				isReliable, counter);

		if (numOfDataReaders == 1) {
			reader = (LatencyDataReader) subscriber
					.create_datareader(
							data_topic,
							reader_qos,
							listener,
							(StatusKind.DATA_AVAILABLE_STATUS | StatusKind.REQUESTED_INCOMPATIBLE_QOS_STATUS));
		} else {
			for (int i = 0; i < numOfDataReaders; i++) {
				createDataReaders();
			}
		}

		counter++;
	
		// while (true) {
		// PublicationMatchedStatus pubMatch = new PublicationMatchedStatus();
		// writer.get_publication_matched_status(pubMatch);
		// if (pubMatch.current_count > 0) {
		// logger.info("Echo readers matched !!");
		// break;
		// } else {
		// logger.info("Echo readers NOT Matched ");
		//
		// }
		// }
	}

	private DataReader createDataReaders() {
		DataReader newReader = (LatencyDataReader) subscriber
				.create_datareader(
						data_topic,
						reader_qos,
						new LatencyListener(writer, instance_handle, cookie),
						(StatusKind.DATA_AVAILABLE_STATUS | StatusKind.REQUESTED_INCOMPATIBLE_QOS_STATUS));
		return newReader;
	}

	/**
	 * Method that is called for reading the messages.
	 */
	@Override
	public void read() {

		while (!listener.has_received_sentinel() && canRun) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // sleep for 1 sec
		}

	}

	/**
	 * Cleans up all the factory, publisher, subscriber objects.
	 */
	@Override
	public void shutdown() {
		logger.info("Messages recevied = [" + msgReceived + "]");
		counter--;
		canRun = false;
		if (counter == 0) {
			logger.info("Counter is zero, destroying recieverParticipant");
			if (recieverParticipant != null) {
				recieverParticipant.delete_contained_entities();
				DomainParticipantFactory.TheParticipantFactory
						.delete_participant(recieverParticipant);
			}

			logger.info("Counter is zero, destroying senderParticipant");
			if (senderParticipant != null) {
				senderParticipant.delete_contained_entities();
				DomainParticipantFactory.TheParticipantFactory
						.delete_participant(senderParticipant);
			}

			// RTI DDS provides finalize_instance() method for people
			// who want to release memory used by the participant
			// factory singleton.
			// Uncomment the following block of code for clean destruction of
			// the participant factory singleton.
			logger.info("Counter is zero, finalizing instance");
			DomainParticipantFactory.finalize_instance();
		} else {
			logger.info("Counter is non zero, cannot destroy participants");
		}

		markTaskComplete();
	}

	private static long msgReceived = 0;

	/**
	 * 
	 * Class that implements the DataReaderAdapter class of RTI. Required to be
	 * able to listen and receive messages.
	 * 
	 */
	private class LatencyListener extends DataReaderAdapter {
		private LatencyDataWriter _writer;
		private InstanceHandle_t _instance_handle;
		private int _sequence_number = 0, _cookie, _num_messages = 0,
				_num_replies = 0;

		LatencySeq data_seq = new LatencySeq();
		SampleInfoSeq info_seq = new SampleInfoSeq();

		public LatencyListener(LatencyDataWriter writer,
				InstanceHandle_t instance_handle, int cookie) {
			_writer = writer;
			_instance_handle = instance_handle;
			_cookie = cookie;
		}

		/**
		 * Check if final ending message that signals the end of send and
		 * receive is received.
		 */
		public boolean has_received_sentinel() {
			return (_sequence_number == FINAL_SN);
		}

		/**
		 * Logs a message if the QOS are incompatible
		 */
		public void on_requested_incompatible_qos(DataReader reader,
				RequestedIncompatibleQosStatus status) {
			logger.info("Incompatible QoS " + status.last_policy_id);
		}

		/**
		 * This method is called whenever there is data available for reading
		 * for this datareader.
		 */
		public void on_data_available(DataReader reader) {
			LatencyDataReader LatencyReader = (LatencyDataReader) reader;
			try {
				LatencyReader.take(data_seq, info_seq,
						ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
						SampleStateKind.ANY_SAMPLE_STATE,
						ViewStateKind.ANY_VIEW_STATE,
						InstanceStateKind.ANY_INSTANCE_STATE);

				for (int i = 0; i < data_seq.size(); ++i) {
					SampleInfo info = (SampleInfo) info_seq.get(i);
					if (info.valid_data) {
						msgReceived++;
						Latency msg = (Latency) data_seq.get(i);
						byte cookie = msg.data.getByte(0);
						_sequence_number = msg.sequence_number;
						addToStatisticsPool(null, msg.data.size(), new Date());
						if (cookie == _cookie) {
							 _writer.write(msg, _instance_handle);
							++_num_replies;
						}
						if (msg.sequence_number == FINAL_SN) {
							logger.info("Test end signaled after "
									+ _num_messages + " messages and "
									+ _num_replies + " replies");
						}
					}
					++_num_messages;
				}
			} catch (RETCODE_NO_DATA no_data) { // No data to process
			} finally {
				LatencyReader.return_loan(data_seq, info_seq);
			}
		}
	}

}
