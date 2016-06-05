package me.loki2302.shared;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.io.PrintWriter;

public class YamlHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    private final Yaml yaml;

    public YamlHttpMessageConverter(Yaml yaml) {
        super(new MediaType("application", "yaml"));
        this.yaml = yaml;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return yaml.loadAs(inputMessage.getBody(), clazz);
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        String yamlString = yaml.dumpAs(o, Tag.MAP, DumperOptions.FlowStyle.BLOCK);

        try(PrintWriter printWriter = new PrintWriter(outputMessage.getBody())) {
            printWriter.print(yamlString);
        }
    }
}
