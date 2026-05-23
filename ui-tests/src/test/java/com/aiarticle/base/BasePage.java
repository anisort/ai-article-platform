package com.aiarticle.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
  protected final WebDriver driver;
  protected final WebDriverWait wait;

  public BasePage(WebDriver driver, WebDriverWait wait) {
    this.driver = driver;
    this.wait = wait;
  }
}

