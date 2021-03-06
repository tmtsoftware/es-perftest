package org.tmt.addons.ospl.reliability;



import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.tmt.addons.ospl.DDSEntityManager;
import org.tmt.addons.ospl.ErrorHandler;
import org.tmt.addons.ospl.osplData.Msg;
import org.tmt.addons.ospl.osplData.MsgDataWriter;
import org.tmt.addons.ospl.osplData.MsgDataWriterHelper;
import org.tmt.addons.ospl.osplData.MsgTypeSupport;
import DDS.DataWriter;
import DDS.DomainParticipant;
import DDS.HANDLE_NIL;
import DDS.PublicationMatchedStatus;
import DDS.PublicationMatchedStatusHolder;
import com.persistent.bcsuite.base.PublisherBase;


public class OpenSplicePubReliability extends PublisherBase {

	private DDSEntityManager mgr;
	private DataWriter dwriter;
	private MsgDataWriter HelloWorldWriter;
	private int msgSent = 0;
	private String topicName = null;
	private DomainParticipant participant;
	private static int counter = 0;
	private int throttlingFactor = 1000;
	private int numSubscribers =1;
	private String throttleGap;
	private int tg;
	private String sleepTime;
	private Tracker t = null;

	
	
	/**
	 * Initialize the ospl parameters for Publisher.
	 */
	
	@Override
	public void init(java.util.Map<String, String> attributes) {
		super.init(attributes);
		String strTopicName = attributes.get("topic");
		if (strTopicName != null) {
			topicName = strTopicName.replaceAll("-", "_");
			System.out.println("Using Topic " + topicName);
		}
		String tf = attributes.get("throttlingFactor");
		try {
			throttlingFactor = Integer.parseInt(tf);
			System.out.println("Throttling factor " + throttlingFactor);
		} catch (Exception e) {
			System.out
					.println("Cannot parse throttling factor.. setting default as 1000");
			throttlingFactor = 1000;
		}
		sleepTime = attributes.get("sleep-time");
		 throttleGap=attributes.get("throttle-gap");
		 
		String ns = attributes.get("numSubscribers");

		try {
			numSubscribers = Integer.parseInt(ns);
		} catch (Exception e) {
			System.out
					.println("Cannot parse numSubscribers.. using default as 1");
	
		}

		System.out.println("Using numSubscribers[" + numSubscribers + "]");

		mgr = new DDSEntityManager();
		String partitionName = "HelloWorld example";
		// create Domain Participant

		mgr.createParticipant(partitionName);

		participant = mgr.getParticipant();

		// create Type
		MsgTypeSupport msgTS = new MsgTypeSupport();
		mgr.registerType(msgTS);

		// create Topic
		mgr.createTopic(topicName);

		// create Publisher
		mgr.createPublisher();

		// create DataWriter
		mgr.createWriter();

		// Publish Events

		dwriter = mgr.getWriter();
		System.out.println("Instance Handle " + dwriter.get_instance_handle());
		System.out.println("Topic : " + dwriter.get_topic().get_name());
		System.out.println("Publisher instance handle"
				+ dwriter.get_publisher().get_instance_handle());
		HelloWorldWriter = MsgDataWriterHelper.narrow(dwriter);
		counter++;
	}

	@Override
	public boolean isReadyToRun() {

		PublicationMatchedStatusHolder pubMatchStatusHldr = new PublicationMatchedStatusHolder();
		HelloWorldWriter.get_publication_matched_status(pubMatchStatusHldr);
		PublicationMatchedStatus pubMatchStat = pubMatchStatusHldr.value;
		System.out.println("Publication Matched Status Count "
				+ pubMatchStat.current_count + "Total Count "
				+ pubMatchStat.total_count);
		System.out.println("Publication Matched Status Count Change "
				+ pubMatchStat.current_count_change + "Total Count Change "
				+ pubMatchStat.total_count_change);
		if (pubMatchStat.current_count > 0) {
			System.out.println("Publication matched ");
			return true;
		}
		return false;

	}

	@Override
	public void sendMessageForThroughput(byte[] message) {
		System.out.println("Starting message sending ");
		setStartTime(new Date());
		int i = 0;
		Msg msgInstance = new Msg();
		msgInstance.userID = 1;
		msgInstance.message = new String(message);
		int len = message.length;
		tg=Integer.parseInt(throttleGap);
		setStartTime(new Date());
		t = new Tracker();
		t.start();
		if (throttlingFactor > 0) {
			float throttleRatio = ((float)throttlingFactor / 1000) * tg;
			while (canContinue()) {
				long beforeSend = System.currentTimeMillis();
				for (int j = 0; j < throttleRatio && canContinue(); j++) {
					HelloWorldWriter.register_instance(msgInstance);
					int status = HelloWorldWriter.write(msgInstance,
							HANDLE_NIL.value);
					// System.out.println("Status " +status );
					ErrorHandler.checkStatus(status, "MsgDataWriter.write");
					t.incrementCounter();
					i++;
					msgSent++;
			
				}
				long afterSend = System.currentTimeMillis();
				long timeLeftInMillis = tg - (afterSend - beforeSend);

				if (timeLeftInMillis > 0) {
					try {
						TimeUnit.MILLISECONDS.sleep(timeLeftInMillis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		else {
			while (canContinue()) {
				HelloWorldWriter.register_instance(msgInstance);
				int status = HelloWorldWriter.write(msgInstance,
						HANDLE_NIL.value);
				ErrorHandler.checkStatus(status, "MsgDataWriter.write");
				t.incrementCounter();
				i++;
				msgSent++;
			}
		}

		setEndTime(new Date());
		System.out.println("Publisher Sleeping for 10 seconds");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		addToStatisticsPool(len);
		markTaskComplete();

	}

	@Override
	public void sendMessageForLatency(byte[] message) {
		// TODO Auto-generated method stub

	}

	/**
	 * delete writer,publisher,topic & participant 
	 */
	@Override
	public void cleanup() {
		counter--;
		System.out.println("############### Total messages sent " + msgSent);
		System.out.println("Tracker ended");
		if (counter == 0) {
			// clean up
			mgr.getPublisher().delete_datawriter(HelloWorldWriter);
			mgr.deletePublisher();
			mgr.deleteTopic();

			mgr.deleteParticipant();
		}

	}
	
	
	public class Tracker extends Thread {
		public boolean isRunning = false;
		private int sentCounter = 0;
		private int iteration = 1;

		@Override
		public void run() {
			System.out.println("############ Tracker started");
			isRunning = true;
			while (canContinue()) {
				try {
					Thread.sleep(Long.parseLong(sleepTime));
					System.out.println("################ sent," + iteration + "," + sentCounter);
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
