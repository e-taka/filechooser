package org.example;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

    @Test
    public void testEmpty() throws Exception {
        _driver.get("http://localhost:8080/filechooser-example/index.html");
        List<WebElement> buttons = _driver.findElements(By.tagName("button"));
        buttons.get(0).click();
        TimeUnit.SECONDS.sleep(5);

        try {
            List<WebElement> filenames =
                    _driver.findElements(
                            By.cssSelector("#filechooser-contents li"));
            Assert.assertTrue(filenames.size() > 0);

            List<WebElement> dirs =
                    _driver.findElements(By.cssSelector("#dirs option"));
            Assert.assertTrue(dirs.size() > 0);
        } finally {
            WebElement cancel = _driver.findElement(By.id("cancel"));
            cancel.click();
        }
    }
}
