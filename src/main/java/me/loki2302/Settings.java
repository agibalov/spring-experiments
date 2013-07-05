package me.loki2302;

import com.google.gson.Gson;

public class Settings {
    private boolean useDummyNotificationService;
    private String notifyAs;
    
    public void setUseDummyNotificationService(boolean useDummyNotificationService) {
        this.useDummyNotificationService = useDummyNotificationService;
    }
    
    public boolean getUseDummyNotificationService() {
        return useDummyNotificationService;
    }
    
    public void setNotifyAs(String notifyAs) {
        this.notifyAs = notifyAs;
    }
    
    public String getNotifyAs() {
        return notifyAs;
    }
    
    @Override
    public String toString() {
        Gson gson = new Gson();
        return String.format("Settings[%s]", gson.toJson(this));
    }
}