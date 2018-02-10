package me.loki2302.notifications;

import com.google.gson.Gson;

public class RealNotificationService implements NotificationService {
    private final String notifyAs;
    
    public RealNotificationService(String notifyAs) {
        this.notifyAs = notifyAs;
    }
    
    @Override
    public void notifyUser() {            
    }
    
    @Override
    public String toString() {
        Gson gson = new Gson();
        return String.format("RealNotificationService(%s)", gson.toJson(this));
    }
}