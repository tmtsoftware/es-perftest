package org.tmt.addons.ospl.throughput;

import java.util.Date;
import java.util.Map;
import org.tmt.addons.ospl.DDSEntityManager;
import org.tmt.addons.ospl.ErrorHandler;
import osplData.MsgDataReader;
import osplData.MsgDataReaderHelper;
import osplData.MsgSeqHolder;
import osplData.MsgTypeSupport;
import DDS.ANY_INSTANCE_STATE;
import DDS.ANY_SAMPLE_STATE;
import DDS.ANY_VIEW_STATE;
import DDS.DataReader;
import DDS.DataReaderQosHolder;
import DDS.DomainParticipant;
import DDS.LENGTH_UNLIMITED;
import DDS.SampleInfoSeqHolder;
import DDS.Subscriber;
import DDS.Topic;
import com.persistent.bcsuite.base.SubscriberBase;

public class OpenSpliceDataSubscriber extends SubscriberBase {

	private DDSEntityManager mgr;
	private MsgDataReader HelloWorldReader;
	private MsgTypeSupport msgTS;
	private static int msgRecvd = 0;
	private Subscriber subscriber;
	private DataReader reader;
	private Topic topic;
	private String topicName;
	private MsgSeqHolder msgSeq = new MsgSeqHolder();
	private SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();
	private boolean terminate = false;
	private DomainParticipant participant;

	
	/**
	 * Initialize the ospl parameters for Subscriber.
	 */

	@Override
	public void init(Map<String, String> attributes) {
		System.out.println("New Version");
		
		/*
		 * Read topic from config file 
		 */
		
		mgr = new DDSEntityManager();
		String strTopicName = attributes.get("topic");
		if (strTopicName != null) {
			topicName = strTopicName.replaceAll("-", "_");
			System.out.println("Using Topic " + topicName);
		}
		System.out.println("Starting Subscriber");
		String partitionName = "HelloWorld example";

		// create Domain Participant
		mgr.createParticipant(partitionName);
		participant = mgr.getParticipant();

		// create Type
		msgTS = new MsgTypeSupport();
		mgr.registerType(msgTS);

		// create Topic
		mgr.createTopic(topicName);

		// create Subscriber
		mgr.createSubscriber();
		subscriber = mgr.getSubscriber();

		topic = mgr.getTopic();
		System.out.println("created topic ");
		// create DataReader
		mgr.createReader();
		DataReaderQosHolder RQosH = new DataReaderQosHolder();
		subscriber.get_default_datareader_qos(RQosH);
		reader = mgr.getReader();
		System.out.println("Attached listener");
		HelloWorldReader = MsgDataReaderHelper.narrow(reader);
		System.out.println("=== [Subscriber] Ready ...");

	}

	
	/**
	 * Receives messages from topic.
	 */
	
	@Override
	public void read() {
		System.out.println("In read ");

		int count = 0;
		try {
			while (!terminate) {// HelloWorldReader.get_status_changes() >0){
				try {

					int status = HelloWorldReader.take(msgSeq, infoSeq,
							LENGTH_UNLIMITED.value, ANY_SAMPLE_STATE.value,
							ANY_VIEW_STATE.value, ANY_INSTANCE_STATE.value);

					Date dateRecvd = new Date();
					for (int i = 0; i < msgSeq.value.length; i++) {
						msgRecvd++;
						addToStatisticsPool(null,
								msgSeq.value[i].message.length(), dateRecvd);
					}
				} catch (Exception e) {
					System.out.println("Exception while retrieving data ");
					continue;
				}
				++count;

			}
		} catch (Exception e) {
			System.out.println("Exception Occurred");
		}

		HelloWorldReader.return_loan(msgSeq, infoSeq);

	}

	
	/**
	 * delete writer,publisher,topic & participant 
	 */
	@Override
	public void shutdown() {
		terminate = true;
		System.out.println("Messages received " + msgRecvd);
		if (terminate) {
			markTaskComplete();
			// clean up
			mgr.getSubscriber().delete_datareader(HelloWorldReader);
			mgr.deleteSubscriber();
			// mgr.deleteTopic();

			mgr.deleteParticipant();
		}

	}

}
