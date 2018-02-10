package me.loki2302.rythm;

import java.text.SimpleDateFormat;

import org.rythmengine.template.JavaTagBase;

public class FancyTimeFormatTag extends JavaTagBase {
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    @Override
    public String __getName() {
        return "fancyTime";
    }
    
    @Override
    protected void call(__ParameterList params, __Body body) {
        p(timeFormat.format(params.getDefault()));
    }        
}