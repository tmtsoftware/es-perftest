package org.tmt.csw.consumerone;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.callback.EventCallback;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;

/**
 * Standalone Consumer program
 * 
 * @author amit_harsola
 * 
 */
public class EventConsumerOne {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		boolean flag = true;

		System.out
				.println("==================Starting ConsumerOne=================");
		System.out.println(" ");

		repeat();

		EventService eventService = AbstractEventService.createEventService();
		String topic = null;

		while (flag) {
			try {

				int number = scanner.nextInt();

				if (number == 0) {
					System.out.println("Please provide topic name");
					topic = scanner.next();
					repeat();
				} else if (number == 1) {
					System.out.println("Subscription Request");

					if (topic != null) {

						try {
							eventService.subscribe(topic,
									new EventCallbackImplOne());

							System.out
									.println("Callback EventCallbackImplOne Subscribed to topic :"
											+ topic);
						} catch (EventSubscriptionException exception) {
							exception.printStackTrace();
						}
					} else {
						System.out
								.println("Please provide FQN topic name and then perform subscription");
					}

					System.out.println("");
					repeat();

					scanner.reset();

				} else if (number == 2) {
					System.out.println("Please provide FQN topic name for unsubscribing");
					topic = scanner.next();

					if (topic != null) {
						try {
							eventService.unSubscribe(topic,
									new EventCallbackImplOne());
							System.out
									.println("###### Callback EventCallbackImplOne unsubscribed from topic:"
											+ topic +" ######");
						} catch (EventUnSubscriptionException exception) {
							exception.printStackTrace();
						}
					} else {
						System.out
								.println("Please provide FQN topic name and then perform unsubscription");
					}

					System.out.println("");
					repeat();
					scanner.reset();

				} else if (number == 3) {
					System.out.println("Please provide FQN topic name for unsubscribing");
					topic = scanner.next();
					if (topic != null) {
						try {
							eventService.unSubscribeAll(topic);
							System.out
							.println("###### All callbacks unsubscribed from topic:"
									+ topic+" ######");
						} catch (EventUnSubscriptionException exception) {
							exception.printStackTrace();
						}
					} else {
						System.out
								.println("Please provide FQN topic name and then perform unsubscriptionall");
					}

					System.out.println("");
					repeat();

				} else if (number == 4) {
					System.out
							.println("==================Shutting Down ConsumerOne=================");
					flag = false;

				} else {
					repeat();
				}
			} catch (InputMismatchException exception) {
				scanner.reset();
				scanner = new Scanner(System.in);
				repeat();
			}
		}

		scanner.close();
	}

	private static void repeat() {
		System.out.println("Usage: ");
		System.out.println("To Provide Topic for Subscription, Type: 0");
		System.out.println("To Subscribe To Topic, Type: 1");
		System.out.println("To unSubscribe From Topic, Type: 2");
		System.out.println("To unSubscribeAll From Topic, Type: 3");
		System.out.println("To Stop ConsumerOne, Type: 4");
	}

	// Junit specific code
	public void subscribe(EventService eventService, String Topic,
			EventCallback callback) throws EventSubscriptionException {
		eventService.subscribe(Topic, callback);
		System.out.println(callback.getClass().getSimpleName() + " Subscribed");
	}

	public void unSubscribe(EventService eventService, String Topic,
			EventCallback callback) throws EventUnSubscriptionException {
		eventService.unSubscribe(Topic, callback);
		System.out.println(callback.getClass().getSimpleName()
				+ " UNSubscribed");
	}

	public void unSubscribeAll(EventService eventService, String Topic)
			throws EventUnSubscriptionException {
		eventService.unSubscribeAll(Topic);
		System.out.println(Topic + " UNSubscribed");

	}

}
