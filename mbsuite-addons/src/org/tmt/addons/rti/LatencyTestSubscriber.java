package org.tmt.addons.rti;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmt.addons.rti.latency.Latency;
import org.tmt.addons.rti.latency.LatencyDataReader;
import org.tmt.addons.rti.latency.LatencyDataWriter;
import org.tmt.addons.rti.latency.LatencySeq;
import org.tmt.addons.rti.latency.LatencyTypeSupport;
import org.tmt.addons.rti.latency.MAX_DATA_SEQUENCE_LENGTH;

import com.persistent.bcsuite.base.SubscriberBase;
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
 * Subscriber class that susbcribes to the publisher and receives messages.
 * Echoes back the messages that it receives.
 * 
 */
public class LatencyTestSubscriber extends SubscriberBase {
	private static final Logger logger = Logger
			.getLogger(LatencyTestSubscriber.class);
	public static final int RTI_DDS_OVERHEAD = 512;
	public static final int FINAL_SN = -1;
	public int cookie = 1;
	public boolean no_echo = false;
	public int domain_id = 0, transport = 1, multicast_ttl = 1;
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
	private boolean canRun = true;
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
					counter + 20);
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
		
		
		
		// Need to change default plugin property
//		if(factory==null){
//			
//		factory = DomainParticipantFactory.get_instance();
//		factory.get_qos(factory_qos);
//		factory_qos.entity_factory.autoenable_created_entities = false;
//		factory.set_qos(factory_qos);
//		}
//		if(participant == null ){
//		// --- Create participant --- //
//		factory.get_default_participant_qos(participant_qos);
//
//		participant_qos.wire_protocol.participant_id = cookie;
//		participant_qos.resource_limits.writer_user_data_max_length = 128;
//		participant_qos.receiver_pool.buffer_size = MAX_DATA_SEQUENCE_LENGTH.VALUE
//				+ 8 + RTI_DDS_OVERHEAD;
//		participant_qos.transport_builtin.mask = 1;
//		participant_qos.discovery_config.participant_liveliness_assert_period.sec = 61;
//		participant_qos.discovery_config.participant_liveliness_lease_duration.sec = 128;
//
//		participant = factory.create_participant(0, participant_qos, null, // listener
//				StatusKind.STATUS_MASK_NONE);
//
//		UDPv4Transport.Property_t property = new UDPv4Transport.Property_t();
//		TransportSupport.get_builtin_transport_property(participant, property);
//
//		property.message_size_max = participant_qos.receiver_pool.buffer_size;
//		property.send_socket_buffer_size = property.message_size_max;
//		property.recv_socket_buffer_size = 2 * property.send_socket_buffer_size;
//		property.multicast_ttl = multicast_ttl;
//
//		TransportSupport.set_builtin_transport_property(participant, property);
//
//		// --- Enable to start Discovery ---
//		participant.enable();
//		}
		subscriber = participant.create_subscriber(
				DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		publisher = participant.create_publisher(
				DomainParticipant.PUBLISHER_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		// --- Create topic --- //

		// Register type before creating topic
		String type_name = LatencyTypeSupport.get_type_name();
		LatencyTypeSupport.register_type(participant, type_name);

		// To customize topic QoS, use
		// participant.get_default_topic_qos() instead of default
		data_topic = participant.create_topic("DataTopic"+counter, type_name,
				DomainParticipant.TOPIC_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);
		echo_topic = participant.create_topic("EchoTopic"+counter, type_name,
				DomainParticipant.TOPIC_QOS_DEFAULT, null, // listener
				StatusKind.STATUS_MASK_NONE);

		// echo writer
		publisher.get_default_datawriter_qos(writer_qos);
		// Get the default reader QoS and configure to our requirements
		int max_gather_send_buffers = 16;
		RTIQosHelper.configure_data_writer_qos(writer_qos, false, max_gather_send_buffers, publisher, false, false, isReliable, counter);
//		writer_qos.history.kind = HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS;
//		writer_qos.history.depth = 1;
//		writer_qos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
//		writer_qos.reliability.max_blocking_time.sec = 1;
//		writer_qos.reliability.max_blocking_time.nanosec = 0;
//		writer_qos.resource_limits.initial_samples = writer_qos.resource_limits.max_samples = 3;
//		writer_qos.resource_limits.max_samples_per_instance = writer_qos.resource_limits.max_samples;
//		writer_qos.protocol.rtps_reliable_writer.fast_heartbeat_period.nanosec = 1 * 1000000; // 1
//																								// ms
//		writer_qos.protocol.rtps_reliable_writer.heartbeat_period.sec = 3600 * 24 * 7; // 1
//																						// week
//
//		// because the queue is 1,
//		// want to piggyback HB w/ every sample.
//		// This may be a duplicate HB,
//		// but since the send is low, it's OK
//		writer_qos.protocol.rtps_reliable_writer.heartbeats_per_max_samples = writer_qos.resource_limits.max_samples;
//		writer_qos.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;

		writer = (LatencyDataWriter) publisher.create_datawriter(echo_topic,
				writer_qos, null, // listener
				StatusKind.STATUS_MASK_NONE);
		// --- Create reader --- //
		listener = new LatencyListener(writer, instance_handle, cookie);

		subscriber.get_default_datareader_qos(reader_qos);

//		reader_qos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
//		reader_qos.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
//		reader_qos.protocol.rtps_reliable_reader.max_heartbeat_response_delay.sec = 0;
//		reader_qos.protocol.rtps_reliable_reader.max_heartbeat_response_delay.nanosec = 0;
		RTIQosHelper.configure_data_reader_qos(reader_qos, subscriber,
				isReliable, counter);
		reader = (LatencyDataReader) subscriber
				.create_datareader(
						data_topic,
						reader_qos,
						listener,
						(StatusKind.DATA_AVAILABLE_STATUS | StatusKind.REQUESTED_INCOMPATIBLE_QOS_STATUS));
		counter++;
	}

	/**
	 * Method that is called for reading the messages.
	 */
	@Override
	public void read() {
		while (!listener.has_received_sentinel() && !canRun) {
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
		logger.info("Recd shutdown from master...");
		counter--;
		if(counter == 0){
			logger.info("Counter is zero, destroying recieverParticipant");
		logger.info("Cleaning up Publisher...");
		canRun = false;
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
		}
		logger.info("Marking Subscriber as complete");
		markTaskComplete();
	}

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
			logger.debug("Incompatible QoS " + status.last_policy_id);
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
						Latency msg = (Latency) data_seq.get(i);
						byte cookie = msg.data.getByte(0);
						_sequence_number = msg.sequence_number;
						if (cookie == _cookie) {
							_writer.write(msg, _instance_handle);
							++_num_replies;
						}
						addToStatisticsPool(null, msg.data.size(), new Date());
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
