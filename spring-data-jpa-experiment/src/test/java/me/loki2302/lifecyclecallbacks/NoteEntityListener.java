package me.loki2302.lifecyclecallbacks;

import javax.persistence.*;

// I didn't manage to figure out how to let Spring manage the listener, that's why 'static'
// TODO: get rid of static
public class NoteEntityListener {
    private static String log = "";

    @PrePersist
    public void onPrePersist(Note note) {
        appendLog("onPrePersist,");
    }

    @PreRemove
    public void onPreRemove(Note note) {
        appendLog("onPreRemove,");
    }

    @PostPersist
    public void onPostPersist(Note note) {
        appendLog("onPostPersist,");
    }

    @PostRemove
    public void onPostRemove(Note note) {
        appendLog("onPostRemove,");
    }

    @PreUpdate
    public void onPreUpdate(Note note) {
        appendLog("onPreUpdate,");
    }

    @PostUpdate
    public void onPostUpdate(Note note) {
        appendLog("onPostUpdate,");
    }

    @PostLoad
    public void onPostLoad(Note note) {
        appendLog("onPostLoad,");
    }

    public static void resetLog() {
        log = "";
    }

    private static void appendLog(String s) {
        log += s;
    }

    public static String getLog() {
        return log;
    }
}
