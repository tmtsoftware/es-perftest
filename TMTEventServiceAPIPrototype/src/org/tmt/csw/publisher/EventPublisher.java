package org.tmt.csw.publisher;

import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.event.EventHeader;
import org.tmt.csw.eventservice.exception.EventPublishException;

/**
 * Publisher to publish message to the topic 
 * 
 * @author amit_harsola 
 *
 */
public class EventPublisher {

	public static void main(String[] args)  {
		try {		
			System.out.println("============Starting Publisher=============");

			repeat();
			
			Scanner scanner=new Scanner(System.in);
			boolean flag = true;
			
			EventPublisher  eventPublisher = new EventPublisher();
			
			while (flag) {

				try {
					int number = scanner.nextInt();
					
					if (number == 1) {
						System.out.println("Request to Publish Message. Please provide topic name");
						
						String topic = scanner.next();
						
						System.out.println("Please enter Message Key");
						
						String key = scanner.next();
						System.out.println("Please enter Message Value");
						String value = scanner.next();
						eventPublisher.publish(topic, key, value);
						System.out.println("");
						repeat();
					} else if (number == 2) {
						System.out.println("============Shutting Down Publisher=============");
						flag = false;
					} else {
						repeat();
					}
					
				} catch (InputMismatchException exception) {
					scanner.reset();
					scanner=new Scanner(System.in);
					repeat();
				}
			} 
			
			scanner.close();
			
		} catch (EventPublishException eventPublishException) {
			eventPublishException.printStackTrace();
		}
	}
	
	/**
	 * Publish Message to the topic
	 * 
	 * @data
	 * @throws EventPublishException
	 */
	private void publish(String topic, String key, String value) throws  EventPublishException {
		EventService eventService = AbstractEventService.createEventService();
		Event event = new Event();
		event.addKeyValue(key, value);
		
		EventHeader headers = new EventHeader();
		headers.setCreateTimestamp(Calendar.getInstance().getTime());
		event.setHeaders(headers);
		eventService.post(topic, event);
	}
	
	private static void repeat() {
		System.out.println("Usage: ");
		System.out.println("To Publish Event, Type: 1");
		System.out.println("To Stop Publisher, Type: 2");
	}
}
