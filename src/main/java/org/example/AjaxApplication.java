package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.apache.commons.lang3.SystemUtils;

public class AjaxApplication extends Application {
    private final Set<Class<?>> _classes;

    public AjaxApplication() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (SystemUtils.IS_OS_WINDOWS) {
            classes.add(WindowsFileView.class);
        } else {
            classes.add(UnixFileView.class);
        }
        _classes = Collections.unmodifiableSet(new HashSet<Class<?>>(classes));
    }

    @Override
    public Set<Class<?>> getClasses() {
        return _classes;
    }

}
