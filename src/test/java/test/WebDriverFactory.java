package test;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebDriverFactory {
    public static WebDriver create() {
        String driver = System.getProperty("test.browser");
        if (StringUtils.isNotBlank(driver)) {
            ;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            driver = "ie";
        } else {
            driver = "firefox";
        }

        if ("firefox".equals(driver)) {
            return new FirefoxDriver();
        } else if ("ie".equals(driver)) {
            DesiredCapabilities capability =
                    DesiredCapabilities.internetExplorer();
            capability.setCapability(
                    InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                    true);
            return new InternetExplorerDriver(capability);
        }

        throw new IllegalStateException("Unknown driver: " + driver);
    }
}
