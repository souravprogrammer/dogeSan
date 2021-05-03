package com.sourav.dogesan.utils;

public class Alert {
    private String alertTitle;
    private String description;
    private String notice;

    public Alert() {
    }

    public Alert(String alertTitle, String description, String notice) {
        this.alertTitle = alertTitle;
        this.description = description;
        notice = notice;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getNotice() {
        return notice;
    }
}
