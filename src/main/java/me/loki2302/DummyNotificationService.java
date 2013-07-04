package me.loki2302;

import com.google.gson.Gson;

public class DummyNotificationService implements NotificationService {
    @Override
    public void notifyUser() {            
    }
    
    @Override
    public String toString() {
        Gson gson = new Gson();
        return String.format("DummyNotificationService(%s)", gson.toJson(this));
    }
}