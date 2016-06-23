package com.hodachop93.hohoda.eventbus;

import android.graphics.Bitmap;

import org.greenrobot.eventbus.EventBus;


public class PhotoIntentEventBus {

    private static EventBus instance;

    public static synchronized EventBus getInstance() {
        if (instance == null)
            instance = new EventBus();
        return instance;
    }

    public static class Event {
        private Bitmap photo;

        public Event(Bitmap photo) {
            this.photo = photo;
        }

        public Bitmap getPhoto() {
            return photo;
        }
    }
}
