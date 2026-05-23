package com.aiarticle.utils.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {
  public static void clickWhenClickable(WebDriverWait wait, By locator) {
    wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
  }

  public static WebElement visible(WebDriverWait wait, By locator) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  public static void urlContains(WebDriverWait wait, String value) {
    wait.until(ExpectedConditions.urlContains(value));
  }
}

