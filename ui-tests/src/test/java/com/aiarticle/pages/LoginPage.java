package com.aiarticle.pages;

import com.aiarticle.base.BasePage;
import com.aiarticle.utils.helpers.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class LoginPage extends BasePage {
  private static final By SIGN_IN = By.xpath("//button[contains(normalize-space(), 'Sign in')]");
  private static final By PASSWORD = By.cssSelector("input[type='password'],input[name='password']");
  private static final By ERROR = By.cssSelector(".error-message");

  public LoginPage(WebDriver driver, WebDriverWait wait) {
    super(driver, wait);
  }

  public void openSignIn() {
    WaitUtils.clickWhenClickable(wait, SIGN_IN);
  }

  public void enterUsernameOrEmail(String value) {
    List<By> selectors = List.of(By.id("username"), By.name("username"), By.cssSelector("input[type='email']"));
    for (By by : selectors) {
      List<WebElement> elements = driver.findElements(by);
      if (!elements.isEmpty()) {
        WebElement el = elements.get(0);
        el.clear();
        el.sendKeys(value);
        return;
      }
    }
    throw new IllegalStateException("Username/email input not found");
  }

  public void enterPassword(String value) {
    WebElement password = WaitUtils.visible(wait, PASSWORD);
    password.clear();
    password.sendKeys(value);
  }

  public void submit() {
    List<By> loginButtons = List.of(
      By.id("kc-login"),
      By.cssSelector("button[type='submit']"),
      By.cssSelector("input[type='submit']")
    );
    for (By by : loginButtons) {
      List<WebElement> els = driver.findElements(by);
      if (!els.isEmpty()) {
        els.get(0).click();
        return;
      }
    }
    throw new IllegalStateException("Login submit button was not found");
  }

  public boolean hasLoginError() {
    return !driver.findElements(ERROR).isEmpty();
  }
}

