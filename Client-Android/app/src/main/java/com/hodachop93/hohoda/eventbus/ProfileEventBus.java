package com.hodachop93.hohoda.eventbus;

import android.graphics.Bitmap;

import com.hodachop93.hohoda.model.Profile;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Hop on 27/03/2016.
 */
public class ProfileEventBus {
    private static EventBus instance;

    private ProfileEventBus() {
    }

    public synchronized static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public static class UpdatePersonProfileEvent {
        private Profile profile;

        public UpdatePersonProfileEvent(Profile profile) {
            this.profile = profile;
        }


        public Profile getProfile() {
            return profile;
        }
    }

    public static class UpdateProfileEvent {
        private Profile profile;

        public UpdateProfileEvent(Profile profile) {
            this.profile = profile;
        }


        public Profile getProfile() {
            return profile;
        }
    }

    public static class EditProfileEvent {
        private boolean enable;
        private Bitmap avatar;

        public EditProfileEvent(boolean enable, Bitmap avatar) {
            this.enable = enable;
            this.avatar = avatar;
        }

        public boolean isEnable() {
            return enable;
        }

        public Bitmap getAvatar() {
            return avatar;
        }
    }

    public static class ReloadProfileEvent {

        public ReloadProfileEvent() {
        }

    }
}
