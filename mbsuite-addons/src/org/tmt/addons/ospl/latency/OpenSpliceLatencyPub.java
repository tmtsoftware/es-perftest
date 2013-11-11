package org.tmt.addons.ospl.latency;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.tmt.addons.ospl.DDSEntityManager;
import org.tmt.addons.ospl.ErrorHandler;
import osplData.Msg;
import osplData.MsgDataReader;
import osplData.MsgDataReaderHelper;
import osplData.MsgDataWriter;
import osplData.MsgDataWriterHelper;
import osplData.MsgSeqHolder;
import osplData.MsgTypeSupport;
import DDS.ANY_INSTANCE_STATE;
import DDS.ANY_SAMPLE_STATE;
import DDS.ANY_VIEW_STATE;
import DDS.DataReader;
import DDS.DataWriter;
import DDS.DomainParticipant;
import DDS.HANDLE_NIL;
import DDS.LENGTH_UNLIMITED;
import DDS.PublicationMatchedStatus;
import DDS.PublicationMatchedStatusHolder;
import DDS.SampleInfoSeqHolder;
import com.persistent.bcsuite.base.PublisherBase;


public class OpenSpliceLatencyPub extends PublisherBase {

	private DDSEntityManager mgr;
	private DataWriter dwriter;
	private DataReader dreader;
	private MsgDataWriter HelloWorldWriter;
	private MsgDataReader HelloWorldReader;
	private int msgSent = 0;
	private String topicName = null;
	private String sendTopicName = null;
	private String recvTopicName = null;
	private DomainParticipant participant;
	private static int counter = 0;
	private static int msgRecvd = 0;
	private String latencyCaptureWindow;

	
	
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
		sendTopicName = topicName + "_abc";
		recvTopicName = topicName + "_pqr";

		latencyCaptureWindow = attributes.get("latencyCaptureWindow");
		
		mgr = new DDSEntityManager();
		
		String partitionName = "HelloWorld example";
		// create Domain Participant
		mgr.createParticipant(partitionName);
		participant = mgr.getParticipant();
		// create Type
		MsgTypeSupport msgTS = new MsgTypeSupport();
		mgr.registerType(msgTS);
		initPub();
		initSub();
		counter++;

	}

	private void initPub() {
		
		mgr.createTopic(sendTopicName);
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
		System.out.println("=== [Publisher] Ready ...");
	}

	private void initSub() {
		// create Topic
		mgr.createTopic(recvTopicName);
		// create Subscriber
		mgr.createSubscriber();
		System.out.println("created topic ");
		// create DataReader
		mgr.createReader();
		dreader = mgr.getReader();
		System.out.println("Attached listener");
		HelloWorldReader = MsgDataReaderHelper.narrow(dreader);
		System.out.println("=== [Subscriber] Ready ...");
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
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessageForLatency(byte[] message) {

		System.out.println("Starting message sending ");
		setStartTime(new Date());
		int i = 0;
		Msg msgInstance = new Msg();
		msgInstance.userID = 1;
		msgInstance.message = new String(message);
		
		int len = message.length;
		MsgSeqHolder msgSeq = new MsgSeqHolder();
		SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();
		long sendTime = 0;
		long recvTime = 0;
		long totalTime = 0;
		//int latencyCaptureWindow = 10;
		int latencyCounter = 0;
		boolean bMsgRecvd = false;
		int throttlingFactor=1000; 
		
		while (canContinue()) {
			//Throttle the sending rate
			long beforeSend = System.currentTimeMillis();
			bMsgRecvd = false;
//			for (int t = 0; t < throttlingFactor && canContinue(); t++) {
			
				
			HelloWorldWriter.register_instance(msgInstance);
			sendTime = System.nanoTime();
			int status = HelloWorldWriter.write(msgInstance, HANDLE_NIL.value);
			ErrorHandler.checkStatus(status, "MsgDataWriter.write");
			i++;
			msgSent++;
			addToStatisticsPool(len);
			// Receive the sent message
			while(!bMsgRecvd){
			try {

				int recvStatus = HelloWorldReader.take(msgSeq, infoSeq,
						LENGTH_UNLIMITED.value, ANY_SAMPLE_STATE.value,
						ANY_VIEW_STATE.value, ANY_INSTANCE_STATE.value);
				recvTime = System.nanoTime();
				totalTime = recvTime - sendTime;
				for (int k = 0; k < msgSeq.value.length; k++) {
					msgRecvd++;
					latencyCounter++;
					bMsgRecvd = true;
						int latency = (int) (recvTime - sendTime);
						if (latencyCounter > Integer.parseInt(latencyCaptureWindow)) {
							addLatencyStatistics(Long.toString(msgSent), latency);
//						System.out.println("Latency " +totalTime);
							latencyCounter = 0;
						}
				}
			} catch (Exception e) {
				System.out.println("Exception while retrieving data ");
				continue;
			}
	 }
		}

		setEndTime(new Date());
		
		markTaskComplete();

	}
	
	/**
	 * delete writer,publisher,topic & participant 
	 */

	@Override
	public void cleanup() {

		counter--;
		System.out.println("Total messages sent " + msgSent);
		System.out.println("Total messages received back " +msgRecvd);
		if (counter == 0) {
			// clean up
			mgr.getPublisher().delete_datawriter(HelloWorldWriter);
			mgr.deletePublisher();
			// mgr.deleteTopic();
			mgr.getSubscriber().delete_datareader(HelloWorldReader);
			mgr.deleteSubscriber();
			// mgr.deleteTopic();
			mgr.deleteParticipant();
		}

	}

}