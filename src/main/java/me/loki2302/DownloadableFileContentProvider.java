package me.loki2302;

import java.io.OutputStream;

public interface DownloadableFileContentProvider {
    void writeContent(OutputStream outputStream);
}
