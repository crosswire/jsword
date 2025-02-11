package org.crosswire.jsword.book.install;

import java.net.URI;

public class DownloadCancelledException extends InstallException {
    public DownloadCancelledException(URI uri) {
        super("Downloading " + uri + " was cancelled");
    }
}
