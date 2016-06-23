package com.hodachop93.hohoda.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Hop Dac Ho on 09/05/2016.
 */
public class UpdateCandidateEventBus {
    private static EventBus instance;

    private UpdateCandidateEventBus() {
    }

    public synchronized static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public static class Event {
        public Event() {
        }
    }
}
