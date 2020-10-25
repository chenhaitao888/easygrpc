package com.cht.easygrpc.ec;

import java.util.function.Consumer;

/**
 * @author : chenhaitao934
 */
public class EventSubscriber {

    public EventSubscriber(String id, Consumer<EventInfo> consumer) {
        this.id = id;
        this.consumer = consumer;
    }

    private String id;
    private Consumer<EventInfo> consumer;
    private ObServer observer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObServer getObserver() {
        return observer;
    }

    public void setObserver(ObServer observer) {
        this.observer = observer;
    }

    public Consumer<EventInfo> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<EventInfo> consumer) {
        this.consumer = consumer;
    }
}
