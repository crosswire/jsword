package org.crosswire.jsword.book.install;

import java.net.URI;

public class DownloadException extends InstallException {
    public int statusCode;
    public URI uri;

    public DownloadException(URI uri, int statusCode) {
        super("Downloading " + uri + " failed with status code " + statusCode);
        this.statusCode = statusCode;
        this.uri = uri;
    }

    public DownloadException(URI uri, int statusCode, Throwable cause) {
        super("Downloading " + uri + " failed with status code " + statusCode, cause);
        this.statusCode = statusCode;
        this.uri = uri;
    }
}
