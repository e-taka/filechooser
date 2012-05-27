package org.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Application;

public class AjaxApplication extends Application {
    private final Set<Class<?>> _classes;

    public AjaxApplication() {
        List<Class<?>> classes = Arrays.asList(new Class<?>[] {
                FileView.class,
        });
        _classes = Collections.unmodifiableSet(new HashSet<Class<?>>(classes));
    }

    @Override
    public Set<Class<?>> getClasses() {
        System.out.println(_classes);
        return _classes;
    }

}
