package com.youxiang.shiro.event.support;

import com.youxiang.shiro.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Author: RiversLau
 * Date: 2018/1/5 15:47
 */
public class DefaultEventBus implements EventBus {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventBus.class);
    private static final String EVENT_LISTENER_ERROR_MSG = "Event listener processing failed. Listeners should " +
            "generally handle exception directly and not propagate to the event bus.";

    private static final EventListenerComparator EVENT_LISTENER_COMPARATOR = new EventListenerComparator();

    private EventListenerResolver eventListenerResolver;

    private final Map<Object, Subscription> registry;
    private final Lock registryReadLock;
    private final Lock registryWriteLock;

    public DefaultEventBus() {
        this.registry = new LinkedHashMap<Object, Subscription>();
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        this.registryReadLock = rwl.readLock();
        this.registryWriteLock = rwl.writeLock();
        this.eventListenerResolver = new AnnotationEventListenerResolver();
    }

    public EventListenerResolver getEventListenerResolver() {
         return this.eventListenerResolver;
    }

    public void setEventListenerResolver(EventListenerResolver eventListenerResolver) {
        this.eventListenerResolver = eventListenerResolver;
    }

    public void publish(Object event) {
        if (event == null) {
            log.info("Received null event for publishing. Ignoring and returning.");
            return;
        }

        registryReadLock.lock();
        try {
            for (Subscription subscription : this.registry.values()) {
                subscription.onEvent(event);
            }
        } finally {
            registryReadLock.unlock();
        }
    }

    public void register(Object instance) {
        if (instance == null) {
            log.info("Received null instance for event listener registration. Ignoring registration request.");
            return;
        }
        unregister(instance);

        List<EventListener> listeners = getEventListenerResolver().getEventListeners(instance);
        if (listeners == null || listeners.isEmpty()) {
            log.warn("Unable to resolve event listeners for subscriber instance [{}]. Ignoring registration request.",
                    instance);
            return;
        }
        Subscription subscription = new Subscription(listeners);
        this.registryReadLock.lock();
        try {
            this.registry.put(instance, subscription);
        } finally {
            this.registryReadLock.unlock();
        }
    }

    public void unregister(Object instance) {
        if (instance == null) {
            return;
        }
        this.registryReadLock.lock();
        try {
            this.registry.remove(instance);
        } finally {
            this.registryReadLock.unlock();
        }
    }


    private class Subscription {

        private final List<EventListener> listeners;

        public Subscription(List<EventListener> listeners) {
            List<EventListener> toSort = new ArrayList<EventListener>(listeners);
            Collections.sort(toSort, EVENT_LISTENER_COMPARATOR);
            this.listeners = listeners;
        }

        public void onEvent(Object event) {
            Set<Object> delivered = new HashSet<Object>();
            for (EventListener listener : this.listeners) {
                Object target = listener;
                if (listener instanceof SingleArgumentMethodEventListener) {
                    SingleArgumentMethodEventListener singleArgListener = (SingleArgumentMethodEventListener) listener;
                    target = singleArgListener.getTarget();
                }
                if (listener.accepts(event) && !delivered.contains(target)) {
                    try {
                        listener.onEvent(event);
                    } catch (Throwable t) {
                        log.warn(EVENT_LISTENER_ERROR_MSG, t);
                    }
                    delivered.add(target);
                }
            }
        }
    }
}
