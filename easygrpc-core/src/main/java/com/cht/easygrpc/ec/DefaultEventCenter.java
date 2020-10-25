package com.cht.easygrpc.ec;

import com.cht.easygrpc.logger.Logger;
import com.cht.easygrpc.logger.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : chenhaitao934
 */
public class DefaultEventCenter implements EventCenter {


    private final ConcurrentHashMap<String, Set<EventSubscriber>> ecMap =
            new ConcurrentHashMap<String, Set<EventSubscriber>>();

    private final ExecutorService executor = Executors.newFixedThreadPool(4 * 2, new NamedThreadFactory("easygrpc" +
            "-InjvmEventCenter-Executor", true));
    public void subscribe(EventSubscriber subscriber, String... topics) {

        for (String topic : topics) {
            Set<EventSubscriber> subscribers = ecMap.get(topic);
            if (subscribers == null) {
                subscribers = new ConcurrentHashSet<>();
                Set<EventSubscriber> oldSubscribers = ecMap.putIfAbsent(topic, subscribers);
                if (oldSubscribers != null) {
                    subscribers = oldSubscribers;
                }
            }
            subscribers.add(subscriber);
        }
    }

    public void unSubscribe(String topic, EventSubscriber subscriber) {
        Set<EventSubscriber> subscribers = ecMap.get(topic);
        if (subscribers != null) {
            for (EventSubscriber eventSubscriber : subscribers) {
                if (eventSubscriber.getId().equals(subscriber.getId())) {
                    subscribers.remove(eventSubscriber);
                }
            }
        }
    }

    public void publishSync(EventInfo eventInfo) {
        Set<EventSubscriber> subscribers = ecMap.get(eventInfo.getTopic());
        if (subscribers != null) {
            for (EventSubscriber subscriber : subscribers) {
                eventInfo.setTopic(eventInfo.getTopic());
                try {
                    subscriber.getConsumer().accept(eventInfo);
                } catch (Throwable e) {
                }
            }
        }
    }

    public void publishAsync(final EventInfo eventInfo) {
        executor.submit(() -> {
            String topic = eventInfo.getTopic();

            Set<EventSubscriber> subscribers = ecMap.get(topic);
            if (subscribers != null) {
                for (EventSubscriber subscriber : subscribers) {
                    try {
                        eventInfo.setTopic(topic);
                        subscriber.getObserver().onObserved(eventInfo);
                    } catch (Throwable e) {
                    }
                }
            }
        });
    }
}
