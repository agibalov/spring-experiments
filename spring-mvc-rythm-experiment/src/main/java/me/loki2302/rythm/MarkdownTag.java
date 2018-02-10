package me.loki2302.rythm;

import org.pegdown.PegDownProcessor;
import org.rythmengine.template.JavaTagBase;

public class MarkdownTag extends JavaTagBase {
    private final static PegDownProcessor pegDownProcessor = new PegDownProcessor();

    @Override
    public String __getName() {
        return "markdown";
    }
    
    @Override
    protected void call(__ParameterList params, __Body body) {
        p(pegDownProcessor.markdownToHtml(body.render()));            
    }        
}