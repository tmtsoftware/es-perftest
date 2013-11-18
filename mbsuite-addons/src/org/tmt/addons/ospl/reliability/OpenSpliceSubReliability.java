package org.tmt.addons.ospl.reliability;

import java.util.Date;
import java.util.Map;
import org.tmt.addons.ospl.DDSEntityManager;
import org.tmt.addons.ospl.ErrorHandler;
import org.tmt.addons.ospl.osplData.MsgDataReader;
import org.tmt.addons.ospl.osplData.MsgDataReaderHelper;
import org.tmt.addons.ospl.osplData.MsgSeqHolder;
import org.tmt.addons.ospl.osplData.MsgTypeSupport;
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
public class OpenSpliceSubReliability extends SubscriberBase {

	private DDSEntityManager mgr;
	private DataReader dreader;
	private MsgDataReader HelloWorldReader;
	private MsgTypeSupport msgTS;
	private static int msgRecvd = 0;
	private Subscriber subscriber;
	private DataReader reader;
	private Topic topic;
	private String topicName;
	private MsgSeqHolder msgSeq = new MsgSeqHolder();
	SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();
	boolean terminate = false;
	private DomainParticipant participant;
	private String sleepTime;
	private boolean shouldStop = false;
	private Tracker t = null;
	private Date dateRecvd;

	
	/**
	 * Initialize the ospl parameters for Subscriber.
	 */
	
	@Override
	public void init(Map<String, String> attributes) {
		System.out.println("New Version");
		mgr = new DDSEntityManager();
		String strTopicName = attributes.get("topic");
		if (strTopicName != null) {
			topicName = strTopicName.replaceAll("-", "_");
			System.out.println("Using Topic " + topicName);
		}
		sleepTime = attributes.get("sleep-time");
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

	@Override
	public void read() {
		System.out.println("In read ");
		t = new Tracker();
		t.start();
		int count = 0;
		try {
			while (!terminate) {// HelloWorldReader.get_status_changes() >0){
				try {

					int status = HelloWorldReader.take(msgSeq, infoSeq,
							LENGTH_UNLIMITED.value, ANY_SAMPLE_STATE.value,
							ANY_VIEW_STATE.value, ANY_INSTANCE_STATE.value);

					dateRecvd = new Date();
					for (int i = 0; i < msgSeq.value.length; i++) {
						msgRecvd++;
						t.incrementCounter();
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
		addToStatisticsPool(null, 1, dateRecvd);
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

	public class Tracker extends Thread {
		public boolean isRunning = false;
		private int recvCounter = 0;
		private int iteration = 1;

		@Override
		public void run() {
			System.out.println("############# Tracker started");
			isRunning = true;
			while (!shouldStop) {
				{
					try {
						Thread.sleep(Long.parseLong(sleepTime));
						System.out.println("############### recv," + iteration
								+ "," + recvCounter);
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
