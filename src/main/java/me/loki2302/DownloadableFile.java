package me.loki2302;

public class DownloadableFile {
    private final String fileName;
    private final long fileSize;
    private final DownloadableFileContentProvider downloadableFileContentProvider;

    public DownloadableFile(
            String fileName,
            long fileSize,
            DownloadableFileContentProvider downloadableFileContentProvider) {

        this.fileName = fileName;
        this.fileSize = fileSize;
        this.downloadableFileContentProvider = downloadableFileContentProvider;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public DownloadableFileContentProvider getDownloadableFileContentProvider() {
        return downloadableFileContentProvider;
    }
}
