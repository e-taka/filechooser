package org.example;

import java.io.File;

public class UnixFileView extends FileView {

    @Override
    protected File getPath(final String path) {
        // 'path/to' -> '/path/to'
        return new File(File.separator + path);
    }
}
