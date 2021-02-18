package org.crosswire.jsword.book.install;

public class DownloadException extends InstallException {
    int statusCode;

    public DownloadException(int statusCode) {
        super("Download failed with status code" + statusCode);
        this.statusCode = statusCode;
    }

    public DownloadException(int statusCode, Throwable cause) {
        super("Download failed with status code" + statusCode, cause);
        this.statusCode = statusCode;
    }

    public DownloadException(String msg) {
        super(msg);
    }

    public DownloadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
