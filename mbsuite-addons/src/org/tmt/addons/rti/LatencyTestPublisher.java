package org.tmt.addons.rti;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmt.addons.rti.latency.Latency;
import org.tmt.addons.rti.latency.LatencyDataReader;
import org.tmt.addons.rti.latency.LatencyDataWriter;
import org.tmt.addons.rti.latency.LatencySeq;
import org.tmt.addons.rti.latency.LatencyTypeSupport;
import org.tmt.addons.rti.latency.MAX_DATA_SEQUENCE_LENGTH;

import com.persistent.bcsuite.base.PublisherBase;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.HistoryQosPolicyKind;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ReliabilityQosPolicyKind;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.PublicationMatchedStatus;
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
import com.rti.ndds.transport.TransportSupport;
import com.rti.ndds.transport.UDPv4Transport;

/**
 * Publisher class that publishes to the subscriber and receives messages that
 * the subscriber echoes back.
 * 
 */
public class LatencyTestPublisher extends PublisherBase {
	private static final Logger logger = Logger
			.getLogger(LatencyTestPublisher.class);
	public static final int RTI_DDS_OVERHEAD = 512;
	public static final int FINAL_SN = -1;
	public int num_iterations = 500000, min_size = 16, maxSize = 8192;
	// Create data sample for writing
	Latency instance = new Latency();

	DomainParticipantFactoryQos factory_qos = new DomainParticipantFactoryQos();
	DomainParticipantFactory factory = null;
	DomainParticipantQos participant_qos = new DomainParticipantQos();
	private static DomainParticipant participant = null;
	Subscriber subscriber = null;
	Publisher publisher = null;
	Topic data_topic = null, echo_topic = null;
	LatencyListener listener = null;
	DataReaderQos reader_qos = new DataReaderQos();
	LatencyDataReader reader = null;
	DataWriterQos writer_qos = new DataWriterQos();
	LatencyDataWriter writer = null;
	InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;
	public static byte num_subscribers = 1;
	private static int latencyCaptureWindow = 0;
	private static int counter = 0;
	boolean isReliable = false;
	boolean isAsynch = false;
	String topicName = null;
	private int maxObjectsPerThread = 1024;
	private int numSubscribers = 0;

	/**
	 * Initialize all the datareaders, datawriters, subscribers, publishers and
	 * participants.
	 * 
	 */
	@Override
	public void init(Map<String, String> attributes) {
		super.init(attributes);

		String lc = attributes.get("latencyCaptureWindow");

		try {
			latencyCaptureWindow = Integer.parseInt(lc);
		} catch (Exception e) {
			logger.error("Cannot parse latencyCaptureWindow.. using default as 100");
			latencyCaptureWindow = 100;
		}

		logger.info("Using latencyCaptureWindow as [" + latencyCaptureWindow
				+ "]");

		//{New QOS
		String strReliable = attributes.get("isReliable");
		if (strReliable != null && !strReliable.isEmpty()) {
			isReliable = Boolean.parseBoolean(strReliable);
			logger.info("Setting Reliability " + isReliable);
		}
		String ns = attributes.get("numSubscribers");

		try {
			numSubscribers = Integer.parseInt(ns);
		} catch (Exception e) {
			logger.error("Cannot parse numSubscribers.. using default as 1");
			numSubscribers = 1;
		}

		logger.info("Using numSubscribers[" + numSubscribers + "]");

		String mo = attributes.get("maxObjectsPerThread");

		try {
			maxObjectsPerThread = Integer.parseInt(mo);
		} catch (Exception e) {
			logger.error("Cannot parse max-objects-per-thread.. using default as 1024");
			maxObjectsPerThread = 1024;
		}

		logger.info("Using maxObjectsPerThread as [" + maxObjectsPerThread
				+ "]");
		if(factory == null){
			factory = DomainParticipantFactory.get_instance();
			RTIQosHelper.configure_factory_qos(factory_qos, factory,
					maxObjectsPerThread);
		}
		UDPv4Transport.Property_t udpv4TransportProperty = new UDPv4Transport.Property_t();
		String locator = null;
		if (participant == null) {
			/** Participant Setting Starts **/
			RTIQosHelper.configure_participant_qos(participant_qos, factory,
					counter + 1);
			// Now we can create the 'disabled' participant.
			participant = factory.create_participant(0, participant_qos, null,
					StatusKind.STATUS_MASK_NONE); // 88 some
													// arbitrary
													// //
													// here.
			RTIQosHelper.configure_participant_transport(locator,
					udpv4TransportProperty, participant);
			// Now enable the participant
			participant.enable();
			/** Participant Setting Ends **/
		} else {
			logger.info("Reusing participant");
		}
		//New QOS }
//		if (factory == null) {
//			// Need to change default plugin property
//			factory = DomainParticipantFactory.get_instance();
//			factory.get_qos(factory_qos);
//			factory_qos.entity_factory.autoenable_created_entities = false;
//			factory.set_qos(factory_qos);
//		}
//		if (participant == null) {
//			// --- Create participant --- //
//			factory.get_default_participant_qos(participant_qos);
//
//			participant_qos.wire_protocol.participant_id = 0;
//			participant_qos.resource_limits.writer_user_data_max_length = 128;
//			participant_qos.receiver_pool.buffer_size = MAX_DATA_SEQUENCE_LENGTH.VALUE
//					+ 8 + RTI_DDS_OVERHEAD;
//			participant_qos.transport_builtin.mask = 1;
//			participant_qos.discovery_config.participant_liveliness_assert_period.sec = 61;
//			participant_qos.discovery_config.participant_liveliness_lease_duration.sec = 128;
//
//			participant = factory.create_participant(0, participant_qos, null, // listener
//					StatusKind.STATUS_MASK_NONE);
//			UDPv4Transport.Property_t property = new UDPv4Transport.Property_t();
//			TransportSupport.get_builtin_transport_property(participant,
//					property);
//
//			property.message_size_max = participant_qos.receiver_pool.buffer_size;
//			property.send_socket_buffer_size = property.message_size_max;
//			property.recv_socket_buffer_size = 2 * property.send_socket_buffer_size;
//			property.multicast_ttl = 1;
//
//			TransportSupport.set_builtin_transport_property(participant,
//					property);
//
//			// Enable to start Discovery
//			participant.enable();
//		}
		subscriber = participant.create_subscriber(
				DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		publisher = participant.create_publisher(
				DomainParticipant.PUBLISHER_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		// --- Create topics --- //
		// Register type before creating topic
		String typeName = LatencyTypeSupport.get_type_name();
		LatencyTypeSupport.register_type(participant, typeName);
		// To customize topic QoS, use
		// participant.get_default_topic_qos() instead
		data_topic = participant.create_topic("DataTopic"+counter, typeName,
				DomainParticipant.TOPIC_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);
		echo_topic = participant.create_topic("EchoTopic"+counter, typeName,
				DomainParticipant.TOPIC_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		// data writer
		publisher.get_default_datawriter_qos(writer_qos);


//
//		writer_qos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
//		writer_qos.reliability.max_blocking_time.sec = 2;
//		writer_qos.reliability.max_blocking_time.nanosec = 0;
//		writer_qos.resource_limits.initial_samples = writer_qos.resource_limits.max_samples = writer_qos.resource_limits.max_samples_per_instance = 3;
//
//		writer_qos.protocol.rtps_reliable_writer.fast_heartbeat_period.sec = 0;
//		writer_qos.protocol.rtps_reliable_writer.fast_heartbeat_period.nanosec = 1 * 1000000; // 1
//																								// ms
//		writer_qos.protocol.rtps_reliable_writer.heartbeat_period.sec = 3600 * 24 * 7; // 1
//																						// week
//
//		// Because the queue is 1, want to piggyback HB
//		// w/ every sample.
//		// This may be a duplicate HB,
//		// but since the send is low, it's OK
//		writer_qos.protocol.rtps_reliable_writer.heartbeats_per_max_samples = (writer_qos.resource_limits.max_samples > 1) ? writer_qos.resource_limits.max_samples
//				: 100000000;
		int max_gather_send_buffers = 16;
		RTIQosHelper.configure_data_writer_qos(writer_qos, false, max_gather_send_buffers, publisher, false, false, isReliable, counter);
//		writer_qos.history.kind = HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS;
//		writer_qos.history.depth = 1;
		writer = (LatencyDataWriter) publisher.create_datawriter(data_topic,
				writer_qos, null, // listener
				StatusKind.STATUS_MASK_NONE);
		// --- Create reader --- //
		listener = new LatencyListener(num_iterations);

		subscriber.get_default_datareader_qos(reader_qos);

//		reader_qos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
//		reader_qos.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
//		reader_qos.protocol.rtps_reliable_reader.max_heartbeat_response_delay.sec = 0;
//		reader_qos.protocol.rtps_reliable_reader.max_heartbeat_response_delay.nanosec = 0;
		RTIQosHelper.configure_data_reader_qos(reader_qos, subscriber,
				isReliable, counter);
		reader = (LatencyDataReader) subscriber
				.create_datareader(
						echo_topic,
						reader_qos,
						listener,
						(StatusKind.DATA_AVAILABLE_STATUS | StatusKind.REQUESTED_INCOMPATIBLE_QOS_STATUS));
		counter++;
		// For data type that has key, if the same instance is going to be
		// written multiple times, initialize the key here
		// and register the keyed instance prior to writing
		// instance_handle_reg = writer.register_instance(instance);
		// InstanceHandle_t instance_handle_reg = InstanceHandle_t.HANDLE_NIL;

		// wait for the requisite number of readers to appear

	}

	/**
	 * Checks whether all the subscribers for the publisher are matched. The
	 * publisher starts writing data after all the subscribers have joined.
	 */
	@Override
	public boolean isReadyToRun() {
		PublicationMatchedStatus matched_status = new PublicationMatchedStatus();
		writer.get_publication_matched_status(matched_status);
		if (matched_status.current_count > 0) {
			logger.info("Matched !!");
			return true; // found everybody, can move on
		}

		logger.info("Not matched");
		return false;

	}

	@Override
	public void sendMessageForThroughput(byte[] message) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sends the message to the subscriber.
	 */
	@Override
	public void sendMessageForLatency(byte[] message) {
		logger.info("in sendMessageForLatency");
		try {
			instance.data.clear();
			instance.data.setMaximum(getMessageLength());
			instance.data.addAllByte(message);
			// set the subscribers know who should echo (the last one)
			instance.data.setByte(0, num_subscribers);

			listener.start_one_round(getMessageLength());
			setStartTime(new Date());
			for (instance.sequence_number = 1; instance.sequence_number <= num_iterations
					&& canContinue;) {
				writer.write(instance, instance_handle); // -- Write --
				addToStatisticsPool(getMessageLength());
				listener.start_one_message(instance.sequence_number);
				// block up to 1 s for reply
				if (listener.wait_for_reply(500)) {
					++instance.sequence_number;
				} else { // warn, sleep, and retry
					logger.info("Did not get reply at sn "
							+ instance.sequence_number);
//					Thread.sleep(10);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		setEndTime(new Date());
		logger.info("Sleeping for 5 seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		instance.sequence_number = FINAL_SN;
//		instance.data.setMaximum(20);
//		listener.start_one_message(instance.sequence_number);
//		writer.write(instance, instance_handle);

		markTaskComplete();
	}

	/**
	 * Cleans up all the factory, publisher, subscriber objects.
	 */
	@Override
	public void cleanup() {
		counter--;

		if (counter == 0) {
			logger.info("Counter is zero, destroying recieverParticipant");
			if (participant != null) {
				participant.delete_contained_entities();
				logger.info("Contained Entities Deleted...");
				DomainParticipantFactory.TheParticipantFactory
				.delete_participant(participant);
				logger.info("Participant Deleted...");
			}
			// NDDS provides finalize_instance() method for people who want to
			// release memory used by the participant factory singleton.
			// Uncomment the following block of code for clean destruction of
			// the participant factory singleton.
			DomainParticipantFactory.finalize_instance();
			logger.info("Factory Finalized");
			logger.info("Cleaning up Publisher...");
		}

	}

	/**
	 * 
	 * Class that implements the DataReaderAdapter class of RTI. Required to be
	 * able to listen and receive messages.
	 * 
	 */
	private class LatencyListener extends DataReaderAdapter {
		private int _num_iterations, _expected_sn, _received_sn, _message_size;
		private long _time_sent, _clock_overhead;
		public Logger logger;
		public ArrayList<Long> roundtrip_time_array;

		LatencySeq data_seq = new LatencySeq();
		SampleInfoSeq info_seq = new SampleInfoSeq();

		private int msgCounter = 0;

		public LatencyListener(int num_iterations) {
			_num_iterations = num_iterations;
			roundtrip_time_array = new ArrayList<Long>();
			roundtrip_time_array.ensureCapacity(num_iterations);

			long start_time, finish_time = 0;
			start_time = System.nanoTime();
			for (int i = 0; i < 16; ++i) {
				finish_time = System.nanoTime();
			}
			_clock_overhead = (finish_time - start_time) / 16;

			logger = Logger.getLogger(LatencyListener.class);
		}

		/**
		 * Start the round for the message.
		 * 
		 * @param message_size
		 */
		void start_one_round(int message_size) {
			_message_size = message_size;
			roundtrip_time_array.clear();
		}

		/**
		 * Send the first message.
		 * 
		 * @param expected_sn
		 */
		void start_one_message(int expected_sn) {
			_expected_sn = expected_sn;
			_received_sn = 0;
			_time_sent = System.nanoTime();
		}

		/**
		 * Logs a message if the QOS are incompatible
		 */
		public void on_requested_incompatible_qos(DataReader reader,
				RequestedIncompatibleQosStatus status) {
			logger.debug("Incompatible QoS " + status.last_policy_id);
		}

		/**
		 * Waits for a reply after sending the first message.
		 * 
		 * @param wait_time_ms
		 *            - max time to wait for reply
		 * @return
		 * @throws Exception
		 */
		public synchronized boolean wait_for_reply(long wait_time_ms)
				throws Exception {
			if (_received_sn == _expected_sn) { // no need to wait
				return true;
			}
			long start_time = System.currentTimeMillis();
			wait(wait_time_ms);
			long elapsed_time = System.currentTimeMillis() - start_time;
			return (elapsed_time < (wait_time_ms - 50 /* safety margin */));
		}

		/**
		 * This method is called whenever there is data available for reading
		 * for this datareader.
		 */
		public synchronized void on_data_available(DataReader reader) {
			LatencyDataReader latency_reader = (LatencyDataReader) reader;
			try {
				latency_reader.take(data_seq, info_seq,
						ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
						SampleStateKind.ANY_SAMPLE_STATE,
						ViewStateKind.ANY_VIEW_STATE,
						InstanceStateKind.ANY_INSTANCE_STATE);

				long time_received = System.nanoTime();
				for (int i = 0; i < data_seq.size(); ++i) {
					msgCounter++;
					SampleInfo info = (SampleInfo) info_seq.get(i);
					if (info.valid_data) {
						Latency msg = (Latency) data_seq.get(i);
						_received_sn = msg.sequence_number;
						if (_received_sn == _expected_sn) {
							long roundtrip_time = time_received - _time_sent
									- _clock_overhead;
							if (msgCounter > latencyCaptureWindow) {
								addLatencyStatistics(
										String.valueOf(msgCounter),
										roundtrip_time);
								msgCounter = 0;
							}
							notify(); // let the main thread send another packet
						} else {
							logger.error("**********ERROR: " + "Received SN "
									+ msg.sequence_number + "!= expected "
									+ _expected_sn);
						}
//						if (msg.sequence_number > _num_iterations) {
//							logger.error("**********ERROR: "
//									+ "Received SN > expected "
//									+ _num_iterations);
//						}
					}
				}
			} catch (RETCODE_NO_DATA no_data) { // No data to process
			} finally {
				latency_reader.return_loan(data_seq, info_seq);
			}
		}

	}
}
