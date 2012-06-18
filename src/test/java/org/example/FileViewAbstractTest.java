package org.example;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.HttpServer;

public abstract class FileViewAbstractTest {
    private static final String BASE_URL =
            "http://localhost:8080/filechooser-example";
    /** HTTPサーバ */
    private static HttpServer _server = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        _server = HttpServer.start();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        _server.stop();
        _server = null;
    }

    @Test
    public void testRoots() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/ajax/filesystem/roots");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode(),
                is(HttpStatus.SC_OK));
        assertThat(EntityUtils.toString(response.getEntity()),
                is("[" + StringUtils.join(File.listRoots(), ',') + "]"));
    }

    @Test
    public void testSeparator() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/ajax/filesystem/separator");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode(),
                is(HttpStatus.SC_OK));
        assertThat(EntityUtils.toString(response.getEntity()),
                is(File.separator));
    }

    @Test
    public void testListString() throws Exception {
        Path dir = Files.createTempDirectory("test-");
        try {
            Files.createFile(Paths.get(dir.toString(), "test.dat"));
            Files.createDirectory(Paths.get(dir.toString(), "test.dir"));

            StringBuilder expect = new StringBuilder();
            expect.append('[')
                  .append("{\"name\":\"test.dat\",\"type\":\"f\"},")
                  .append("{\"name\":\"test.dir\",\"type\":\"d\"}")
                  .append(']');
            HttpGet request = new HttpGet(createListUrl(dir));
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);

            assertThat(response.getStatusLine().getStatusCode(),
                    is(HttpStatus.SC_OK));
            assertThat(EntityUtils.toString(response.getEntity()),
                    is(expect.toString()));
        } finally {
            FileUtils.deleteDirectory(dir.toFile());
        }
    }

    protected abstract String createListUrl(final Path dir);

    public static class UnixTest extends FileViewAbstractTest {
        protected String createListUrl(final Path dir) {
            StringBuilder url = new StringBuilder();
            url.append(BASE_URL)
               .append("/ajax/filesystem/list/")
               .append(dir.toString());
            return url.toString();
        }
    }

    public static class WindowsTest extends FileViewAbstractTest {
        protected String createListUrl(final Path dir) {
            StringBuilder url = new StringBuilder();
            url.append(BASE_URL)
               .append("/ajax/filesystem/list/")
               .append(dir.toString().replace(File.separatorChar, '/'));
            return url.toString();
        }
    }
}
