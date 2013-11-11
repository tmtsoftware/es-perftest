package org.tmt.addons.ospl.latency;

import java.util.Date;
import java.util.Map;
import org.tmt.addons.ospl.DDSEntityManager;
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
import DDS.SampleInfoSeqHolder;
import com.persistent.bcsuite.base.SubscriberBase;

public class OpenSpliceLatencySub extends SubscriberBase{

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
	private boolean terminate=false;
	
	

	
	/**
	 * Initialize the ospl parameters for Subscriberr.
	 */
	
	@Override
	public void init(Map<String, String> attributes) {

		String strTopicName = attributes.get("topic");
		if (strTopicName != null) {
			topicName = strTopicName.replaceAll("-", "_");
			System.out.println("Using Topic " + topicName);
		}
		sendTopicName = topicName + "_pqr";
		recvTopicName = topicName + "_abc";

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
		System.out.println("Send Topic : " + dwriter.get_topic().get_name());
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
		// create DataReader
		mgr.createReader();
		dreader = mgr.getReader();
		System.out.println("Receive Topic : " + dreader.get_topicdescription().get_name());
		HelloWorldReader = MsgDataReaderHelper.narrow(dreader);
		System.out.println("=== [Subscriber] Ready ...");
	}
	
	@Override
	public void read() {
		MsgSeqHolder msgSeq = new MsgSeqHolder();
		SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();
		System.out.println("In read ");
		Msg msgInstance = new Msg();
		msgInstance.userID = 1;
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
						//write the message back to publisher
						
						msgInstance.message = new String(msgSeq.value[i].message);
						HelloWorldWriter.register_instance(msgInstance);
						status = HelloWorldWriter.write(msgInstance, HANDLE_NIL.value);
						
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
			// clean up
			mgr.getPublisher().delete_datawriter(HelloWorldWriter);
			mgr.deletePublisher();
			// mgr.deleteTopic();
			mgr.deleteParticipant();
		}

	
		
	}

}
