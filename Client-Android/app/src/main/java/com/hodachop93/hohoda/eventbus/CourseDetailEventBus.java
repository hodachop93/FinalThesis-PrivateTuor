package com.hodachop93.hohoda.eventbus;

import com.hodachop93.hohoda.model.Course;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hopho on 28/04/2016.
 */
public class CourseDetailEventBus {
    private static EventBus instance;

    public static synchronized EventBus getInstance() {
        if (instance == null)
            instance = new EventBus();
        return instance;
    }

    public static class Event {
        private Course course;

        public Event(Course course) {
            this.course = course;
        }

        public Course getCourse() {
            return course;
        }
    }
}
