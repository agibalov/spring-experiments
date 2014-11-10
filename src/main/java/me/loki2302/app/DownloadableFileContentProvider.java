package me.loki2302.app;

import java.io.OutputStream;

public interface DownloadableFileContentProvider {
    void writeContent(OutputStream outputStream);
}
