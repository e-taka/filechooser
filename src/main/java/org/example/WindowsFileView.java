package org.example;

import java.io.File;

public class WindowsFileView extends FileView {

    @Override
    protected File getPath(final String path) {
        if (path.matches("[A-Za-z]:")) {
            // 'C:' -> 'C:\'
            return new File(path + File.separator);
        } else {
            // 'C:/path/to' -> 'C:/path/to'
            return new File(path);
        }
    }
}
