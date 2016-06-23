package com.hodachop93.hohoda.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hopho on 12/05/2016.
 */
public class LocationEventBus {
    private static EventBus instance;

    public static synchronized EventBus getInstance() {
        if (instance == null)
            instance = new EventBus();
        return instance;
    }

    public static class Event {
        public Event() {
        }
    }
}
