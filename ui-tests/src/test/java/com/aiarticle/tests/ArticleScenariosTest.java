package com.aiarticle.tests;

import com.aiarticle.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ArticleScenariosTest extends BaseTest {
  private static String createdArticleId;
  private static String createdTitle;
  private static String editedTitle;

  @Test
  @Order(1)
  void shouldOpenRecommendationsPage() {
    open("/home");
    wait.until(ExpectedConditions.or(
      ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(), 'Recommended Articles')]")),
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".empty-state")),
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-article-preview-card"))
    ));
  }

  @Test
  @Order(2)
  void shouldOpenAllArticlesPage() {
    open("/articles");
    wait.until(ExpectedConditions.or(
      ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(), 'All Articles')]")),
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".empty-state")),
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-article-preview-card"))
    ));
  }

  @Test
  @Order(3)
  void shouldCreateArticle() {
    createdTitle = "UI E2E " + Instant.now().toEpochMilli();
    String markdown = "Created by Selenium " + UUID.randomUUID();

    open("/edit-article");
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input.title-input")));

    driver.findElement(By.cssSelector("input.title-input")).sendKeys(createdTitle);
    driver.findElement(By.cssSelector("textarea.editor-textarea")).sendKeys(markdown);

    driver.findElement(By.xpath("//button[contains(normalize-space(), 'Set cover')]")).click();
    WebElement upload = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='file']")));
    upload.sendKeys(Path.of("src", "test", "resources", "cover.png").toAbsolutePath().toString());
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(normalize-space(), 'Close')]"))).click();

    WebElement publish = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(normalize-space(), 'Publish article')]")));
    publish.click();

    wait.until(d -> d.getCurrentUrl().contains("/articles/"));
    createdArticleId = articleIdFromCurrentUrl();
    assertNotNull(createdArticleId);
    assertFalse(createdArticleId.isBlank());
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".article-shell")));
  }

  @Test
  @Order(4)
  void shouldGetArticleDetailPage() {
    assertNotNull(createdArticleId, "Article should be created before detail test");
    open("/articles/" + createdArticleId);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".article-shell")));
    assertTrue(driver.getCurrentUrl().contains("/articles/" + createdArticleId));
  }

  @Test
  @Order(5)
  void shouldEditArticle() {
    assertNotNull(createdArticleId, "Article should be created before edit test");
    editedTitle = createdTitle + " Edited";
    String editedMarkdown = "Edited by Selenium " + UUID.randomUUID();

    open("/edit-article/" + createdArticleId);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input.title-input")));

    WebElement titleInput = driver.findElement(By.cssSelector("input.title-input"));
    titleInput.clear();
    titleInput.sendKeys(editedTitle);

    WebElement contentInput = driver.findElement(By.cssSelector("textarea.editor-textarea"));
    contentInput.clear();
    contentInput.sendKeys(editedMarkdown);

    driver.findElement(By.xpath("//button[contains(normalize-space(), 'Save article')]")).click();
    wait.until(d -> d.getCurrentUrl().contains("/articles/" + createdArticleId));

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".article-shell")));
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(normalize-space(), 'Edit')]"))).click();
    wait.until(d -> d.getCurrentUrl().contains("/edit-article/" + createdArticleId));
  }

  @Test
  @Order(6)
  void shouldLikeAndUnlikeArticle() {
    String likeableId = findLikeableArticleId();
    assertNotNull(likeableId, "No likeable article found. Ensure there is at least one article from another author.");

    open("/articles/" + likeableId);
    WebElement likeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".article-like-button")));
    String before = likeButton.getText();

    likeButton.click();
    wait.until(d -> {
      String now = d.findElement(By.cssSelector(".article-like-button")).getText();
      return !now.equals(before) || now.contains("Unlike article");
    });

    WebElement unlikeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".article-like-button")));
    unlikeButton.click();
    wait.until(d -> d.findElement(By.cssSelector(".article-like-button")).getText().contains("Like article"));
  }

  @Test
  @Order(7)
  void shouldFollowAndUnfollowAuthor() {
    String likeableId = findLikeableArticleId();
    assertNotNull(likeableId, "No article from another author found for follow/unfollow.");

    open("/articles/" + likeableId);
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".article-author-link"))).click();

    WebElement followButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".profile-follow-button")));
    boolean isFollowing = followButton.getText().contains("Unfollow");
    followButton.click();

    if (isFollowing) {
      wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".profile-follow-button"), "Follow"));
      driver.findElement(By.cssSelector(".profile-follow-button")).click();
      wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".profile-follow-button"), "Unfollow"));
    } else {
      wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".profile-follow-button"), "Unfollow"));
      driver.findElement(By.cssSelector(".profile-follow-button")).click();
      wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".profile-follow-button"), "Follow"));
    }
  }

  @Test
  @Order(8)
  void shouldDeleteArticle() {
    assertNotNull(createdArticleId, "Article should be created before delete test");
    open("/articles/" + createdArticleId);
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(normalize-space(), 'Delete')]"))).click();
    wait.until(ExpectedConditions.alertIsPresent());
    driver.switchTo().alert().accept();
    wait.until(ExpectedConditions.or(
      ExpectedConditions.urlContains("/profile"),
      ExpectedConditions.urlContains("/articles"),
      ExpectedConditions.urlContains("/home")
    ));

    open("/articles/" + createdArticleId);
    boolean deletedStateReached = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
      .until(d -> {
        boolean atNotFound = d.getCurrentUrl().contains("/not-found");
        boolean hasDeleteButton = !d.findElements(By.xpath("//button[contains(normalize-space(), 'Delete')]")).isEmpty();
        return atNotFound || !hasDeleteButton;
      });
    assertTrue(deletedStateReached, "Article still has Delete action after deletion attempt.");
  }

  private String findLikeableArticleId() {
    open("/articles");
    wait.until(ExpectedConditions.or(
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-article-preview-card")),
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".empty-state"))
    ));
    List<WebElement> links = driver.findElements(By.cssSelector("app-article-preview-card .card-title a"));
    List<String> hrefs = new ArrayList<>();
    for (WebElement link : links) {
      String href = link.getAttribute("href");
      if (href != null && !href.isBlank()) {
        hrefs.add(href);
      }
    }

    for (String href : hrefs) {
      String id = href.substring(href.lastIndexOf('/') + 1);
      open("/articles/" + id);
      if (!driver.findElements(By.cssSelector(".article-like-button")).isEmpty()) {
        return id;
      }
    }
    return null;
  }

  private String articleIdFromCurrentUrl() {
    try {
      String path = URI.create(driver.getCurrentUrl()).getPath();
      if (path == null || !path.contains("/articles/")) {
        return null;
      }
      return path.substring(path.lastIndexOf('/') + 1);
    } catch (IllegalArgumentException | NoSuchElementException ex) {
      return null;
    }
  }
}
