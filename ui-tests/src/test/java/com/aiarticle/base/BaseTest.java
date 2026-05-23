package com.aiarticle.base;

import com.aiarticle.utils.Login;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseTest {
  protected static WebDriver driver;
  protected static WebDriverWait wait;

  protected static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:81");
  protected static final String EMAIL = System.getProperty("ui.email", "parallel_builder_02@example.com");
  protected static final String PASSWORD = System.getProperty("ui.password", "Test100#");

  @BeforeAll
  static void setUp() {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    if (Boolean.parseBoolean(System.getProperty("ui.headless", "false"))) {
      options.addArguments("--headless=new");
      options.addArguments("--window-size=1920,1080");
    }
    options.addArguments("--disable-notifications", "--no-sandbox", "--disable-dev-shm-usage");

    driver = new ChromeDriver(options);
    wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    driver.manage().window().maximize();

    open("/home");
    driver.manage().deleteAllCookies();
    try {
      ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
    } catch (Exception ignored) {
    }
    open("/home");
    if (driver.findElements(By.xpath("//button[contains(normalize-space(), 'Logout')]")).isEmpty()) {
      Login.login(driver, wait, EMAIL, PASSWORD);
      new WebDriverWait(driver, Duration.ofSeconds(90))
        .until(d -> d.getCurrentUrl().startsWith(BASE_URL));
      open("/home");
      new WebDriverWait(driver, Duration.ofSeconds(90))
        .until(d -> !d.findElements(By.cssSelector("a.profile-info--link")).isEmpty()
          || !d.findElements(By.xpath("//button[contains(normalize-space(), 'Logout')]")).isEmpty());
    }
  }

  @AfterAll
  static void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  protected static void open(String path) {
    driver.get(BASE_URL + path);
  }
}

