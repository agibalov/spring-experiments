package me.loki2302;

import com.google.gson.Gson;

public class Settings {
    private boolean useStubNotificationService;
    private String notifyAs;
    
    public void setUseStubNotificationService(boolean useStubNotificationService) {
        this.useStubNotificationService = useStubNotificationService;
    }
    
    public boolean getUseStubNotificationService() {
        return useStubNotificationService;
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