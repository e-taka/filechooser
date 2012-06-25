package org.example;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import test.HttpServer;
import test.WebDriverFactory;

public class FileChooserTest {
    private static HttpServer _server = null;
    @BeforeClass
    public static void startServer() throws Exception {
        _server = HttpServer.start();
    }
    @AfterClass
    public static void stopServer() throws Exception {
        if (_server != null) {
            _server.stop();
        }
    }

    private static WebDriver _driver = null;
    @BeforeClass
    public static void openBrowser() {
        _driver = WebDriverFactory.create();
    }
    @AfterClass
    public static void closeBrowser() {
        if (_driver != null) {
            _driver.quit();
        }
    }

    private void waitLoad() {
        new WebDriverWait(_driver, 5).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(final WebDriver driver) {
                List<WebElement> filenames =
                        driver.findElements(
                                By.cssSelector("#filechooser-contents li"));
                return filenames.size() > 1;
            }
        });
    }

    @Test
    public void testEmptyAndCancel() throws Exception {
        _driver.get("http://localhost:8080/filechooser-example/index.html");

        List<WebElement> buttons = _driver.findElements(By.tagName("button"));
        buttons.get(0).click();
        waitLoad();

        try {
            WebElement title =
                    _driver.findElement(
                            By.id("ui-dialog-title-filechooser-dialog"));
            assertThat(title.getText(), is("File Chooser"));

            List<WebElement> dirs =
                    _driver.findElements(By.cssSelector("#dirs option"));
            assertThat(dirs.size(), is(1));
            assertThat(dirs.get(0).getText(),
                    is(System.getProperty("user.dir")));

            List<WebElement> filenames =
                    _driver.findElements(
                            By.cssSelector("#filechooser-contents li"));
            assertThat(filenames.size(), anyOf(is(7), is(8)));
            assertThat(filenames.get(0).getText(), is("README.md"));
            assertThat(filenames.get(0).getAttribute("class"), is("f"));
        } finally {
            WebElement cancel = _driver.findElement(By.id("cancel"));
            cancel.click();
        }

        WebElement text =
                _driver.findElement(By.cssSelector("#execute-file input"));
        assertThat(text.getAttribute("value"), is(""));
    }

    @Test
    public void testEmptyAndOk() throws Exception {
        List<WebElement> buttons = _driver.findElements(By.tagName("button"));
        buttons.get(0).click();
        waitLoad();

        List<WebElement> filenames =
                _driver.findElements(
                        By.cssSelector("#filechooser-contents li"));
        filenames.get(2).click();

        WebElement ok = _driver.findElement(By.id("ok"));
        ok.click();

        WebElement text =
                _driver.findElement(By.cssSelector("#execute-file input"));
        File expect = new File(System.getProperty("user.dir"), "build.xml");
        assertThat(text.getAttribute("value"), is(expect.getPath()));
    }

    @Test
    public void testDeepFilepath() throws Exception {
        String base = System.getProperty("user.dir");
        Path[] dirs = {
                Paths.get(base),
                Paths.get(base, "src"),
                Paths.get(base, "src", "main"),
                Paths.get(base, "src", "main", "webapp"),
        };
        WebElement text =
                _driver.findElement(By.cssSelector("#execute-file input"));
        text.clear();
        text.sendKeys(dirs[dirs.length - 1].resolve("index.html").toString());

        List<WebElement> buttons = _driver.findElements(By.tagName("button"));
        buttons.get(0).click();
        waitLoad();

        try {
            List<WebElement> actual =
                    _driver.findElements(By.cssSelector("#dirs option"));
            assertThat(actual.size(), is(dirs.length));
            assertThat(actual.get(0).getText(), is(dirs[0].toString()));
            assertThat(actual.get(1).getText(), is(dirs[1].toString()));
            assertThat(actual.get(2).getText(), is(dirs[2].toString()));
            assertThat(actual.get(3).getText(), is(dirs[3].toString()));
        } finally {
            WebElement cancel = _driver.findElement(By.id("cancel"));
            cancel.click();
        }
    }
}
