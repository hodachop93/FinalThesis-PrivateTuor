package com.hodachop93.hohoda.eventbus;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by Hop Dac Ho on 08/05/2016.
 */
public class SearchEventBus {
    private static EventBus instance;

    private SearchEventBus() {
    }

    public synchronized static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public static class Event{
        private ArrayList<String> hashtags;

        public Event(ArrayList<String> hashtags) {
            this.hashtags = hashtags;
        }

        public ArrayList<String> getHashtags() {
            return hashtags;
        }
    }
}
