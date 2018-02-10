package me.loki2302.rythm;

import java.text.SimpleDateFormat;

import org.rythmengine.template.JavaTagBase;

public class FancyDateFormatTag extends JavaTagBase {
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    
    @Override
    public String __getName() {
        return "fancyDate";
    }
    
    @Override
    protected void call(__ParameterList params, __Body body) {
        p(dateFormat.format(params.getDefault()));
    }        
}