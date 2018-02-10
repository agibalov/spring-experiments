package me.loki2302.app;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;

public class DownloadableFileMessageConverter extends AbstractHttpMessageConverter<DownloadableFile> {
    public DownloadableFileMessageConverter() {
        super(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return aClass.equals(DownloadableFile.class);
    }

    @Override
    protected DownloadableFile readInternal(
            Class<? extends DownloadableFile> aClass,
            HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {

        return null;
    }

    @Override
    protected void writeInternal(
            DownloadableFile downloadableFile,
            HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        HttpHeaders httpHeaders = httpOutputMessage.getHeaders();
        httpHeaders.setContentLength(downloadableFile.getFileSize());
        httpHeaders.set("Content-Disposition", String.format("attachment; filename=\"%s\"", downloadableFile.getFileName()));

        OutputStream outputStream = null;
        try {
            outputStream = httpOutputMessage.getBody();
            DownloadableFileContentProvider outputStreamWriter = downloadableFile.getDownloadableFileContentProvider();
            outputStreamWriter.writeContent(outputStream);
        } finally {
            if(outputStream != null) {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }
}
