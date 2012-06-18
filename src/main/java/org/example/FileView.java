package org.example;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

@Path("/ajax/filesystem")
public abstract class FileView {

    @GET
    @Path("roots")
    public List<String> roots() {
        File[] roots = File.listRoots();
        List<String> paths = new ArrayList<String>();
        for (final File d : roots) {
            paths.add(d.getPath());
        }
        return paths;
    }

    @GET
    @Path("separator")
    public String separator() {
        return File.separator;
    }

    @GET
    @Path("list")
    public Collection<FileItem> list(@QueryParam("q") final String q) {
        return list("", q);
    }

    @GET
    @Path("list/{path:.*}")
    @Produces("application/json")
    public Collection<FileItem> list(
            @PathParam("path") final String path, @QueryParam("q") final String q) {
        List<FileItem> files = new LinkedList<FileItem>();

        File p = getPath(path);
        if (p.isDirectory()) {
            String pattern =
                StringUtils.defaultString(StringUtils.trim(q), "*");
            FileFilter filter = FileFilterUtils.or(
                    new WildcardFileFilter(StringUtils.split(pattern, ';')),
                    FileFilterUtils.directoryFileFilter());
            for (final File f : p.listFiles(filter)) {
                files.add(new FileItem(f));
            }
            Collections.sort(files);
        }

        return files;
    }

    protected abstract File getPath(final String path);

    static class FileItem implements Comparable<FileItem> {
        private final String _name;
        private final String _type;

        public FileItem(final File f) {
            _name = f.getName();
            _type = f.isDirectory() ? "d" : "f";
        }

        public String getName() {
            return _name;
        }

        public String getType() {
            return _type;
        }

        @Override
        public int compareTo(final FileItem o) {
            return _name.compareTo(o._name);
        }
    }
}
