package base;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.*;

public class TestBase {

    public static WebDriver driver;
    public Properties p;

    @Parameters({"browser"})
    @BeforeClass(groups = { "Sanity", "Regression", "Master" })
    public void setup(@Optional("chrome") String br) throws IOException {

        FileReader configFile = new FileReader("./src/test/resources/config.properties");
        p = new Properties();
        p.load(configFile);

        switch (br.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();

                // Disable only browser-level popups and password manager
                Map<String, Object> chromePrefs = new HashMap<>();
                chromePrefs.put("profile.password_manager_leak_detection", false);
                chromePrefs.put("credentials_enable_service", false);
                chromePrefs.put("profile.password_manager_enabled", false);
            //    chromePrefs.put("profile.default_content_setting_values.notifications", 2); // block notifications
            //    chromePrefs.put("profile.default_content_setting_values.popups", 2);        // block popups


                chromeOptions.setExperimentalOption("prefs", chromePrefs);
                chromeOptions.addArguments("--disable-popup-blocking");

                driver = new ChromeDriver(chromeOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();

                Map<String, Object> edgePrefs = new HashMap<>();
         //     edgePrefs.put("profile.default_content_setting_values.notifications", 2); // block notifications
        //      edgePrefs.put("profile.default_content_setting_values.popups", 2);        // block popups
                
                // Only disable system-level prompts
                edgePrefs.put("profile.password_manager_enabled", false);

                edgeOptions.setExperimentalOption("prefs", edgePrefs);
                edgeOptions.addArguments("--disable-popup-blocking");
      //        edgeOptions.addArguments("--disable-notifications");

                driver = new EdgeDriver(edgeOptions);
                break;

            case "firefox":
                FirefoxProfile profile = new FirefoxProfile();
                
     //         profile.setPreference("dom.webnotifications.enabled", false);
     //         profile.setPreference("dom.push.enabled", false); // disables desktop push
                
                // Allow web notifications from your app, but block system prompts
                profile.setPreference("signon.rememberSignons", false); // disable save password popup
                profile.setPreference("dom.disable_open_during_load", true); // block unwanted popups

                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setProfile(profile);

                driver = new FirefoxDriver(firefoxOptions);
                break;

            default:
                System.out.println("Invalid browser name");
                return;
        }

        driver.manage().window().maximize();
        driver.get(p.getProperty("AppURL"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterClass(groups = { "Sanity", "Regression", "Master" })
    public void close() {
        driver.quit();
    }
}
