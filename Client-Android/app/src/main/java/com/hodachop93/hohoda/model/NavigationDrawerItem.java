package com.hodachop93.hohoda.model;

/**
 * Created by Hop on 04/03/2016.
 */
public class NavigationDrawerItem {
    private String title;
    private int iconResourceId;
    private int badge;
    private boolean showDivider;
    private int position;

    public NavigationDrawerItem(String title, int iconResourceId, boolean showDivider, int position) {
        this.title = title;
        this.iconResourceId = iconResourceId;
        this.showDivider = showDivider;
        this.position = position;
    }

    public boolean isShowDivider() {
        return showDivider;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getTitle() {
        return title;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public int getPosition() {
        return position;
    }
}
