package com.cht.easygrpc.ec;

/**
 * @author : chenhaitao934
 */
public interface EventCenter {

    void subscribe(EventSubscriber subscriber, String... topics);

    void unSubscribe(String topic, EventSubscriber subscriber);

    void publishSync(EventInfo eventInfo);

    void publishAsync(EventInfo eventInfo);
}
