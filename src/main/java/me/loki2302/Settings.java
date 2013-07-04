package me.loki2302;

import com.google.gson.Gson;

public class Settings {
    private String environmentName;
    
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
    
    public String getEnvironmentName() {
        return environmentName;
    }
    
    @Override
    public String toString() {
        Gson gson = new Gson();
        return String.format("Settings[%s]", gson.toJson(this));
    }
}