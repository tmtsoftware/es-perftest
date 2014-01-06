package org.tmt.csw.consumertwo;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.callback.EventCallback;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.callback.EventCallbackImplTwo;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;

/**
 * Standalone Consumer program
 * 
 * @author amit_harsola
 * 
 */
public class EventConsumerTwo {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		boolean flag = true;

		System.out
				.println("==================Starting ConsumerTwo=================");
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
									new EventCallbackImplTwo());
							System.out
									.println("Callback EventCallbackImplTwo Subscribed to topic :"
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
					System.out.println("UnSubscription Request");

					if (topic != null) {
						try {
							eventService.unSubscribe(topic,
									new EventCallbackImplTwo());
							System.out
									.println("Call EventCallbackImplTwo unsubscribed from topic:"
											+ topic);
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

					if (topic != null) {
						try {
							eventService.unSubscribeAll(topic);
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
							.println("==================Shutting Down ConsumerTwo=================");
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
		System.out.println("To Subscribe To Topic, Type: 0");
		System.out.println("To Subscribe To Topic, Type: 1");
		System.out.println("To unSubscribe From Topic, Type: 2");
		System.out.println("To unSubscribeAll From Topic, Type: 3");
		System.out.println("To Stop ConsumerTwo, Type: 4");
	}

	// Junit specific code
	public void subscribe(EventService eventService, String Topic,
			EventCallback callback) throws EventSubscriptionException {
		try {
			eventService.subscribe(Topic, callback);
			System.out.println(callback.getClass().getSimpleName()
					+ " Subscribed");
		} catch (EventSubscriptionException exception) {
			throw exception;
		}
	}

	public void unSubscribe(EventService eventService, String Topic,
			EventCallback callback) throws EventUnSubscriptionException {
		try {
			eventService.unSubscribe(Topic, callback);
			System.out.println(callback.getClass().getSimpleName()
					+ " UNSubscribed");
		} catch (EventUnSubscriptionException exception) {
			throw exception;
		}
	}

	public void unSubscribeAll(EventService eventService, String Topic)
			throws EventUnSubscriptionException {
		try {
			eventService.unSubscribeAll(Topic);
			System.out.println(Topic + " UNSubscribed");
		} catch (EventUnSubscriptionException exception) {
			throw exception;
		}

	}

}
