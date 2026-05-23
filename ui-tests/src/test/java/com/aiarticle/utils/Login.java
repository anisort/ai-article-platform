package com.aiarticle.utils;

import com.aiarticle.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Login {
  public static void login(WebDriver driver, WebDriverWait wait, String email, String password) {
    LoginPage loginPage = new LoginPage(driver, wait);
    loginPage.openSignIn();
    loginPage.enterUsernameOrEmail(email);
    loginPage.enterPassword(password);
    loginPage.submit();
  }
}

