package com.summor.summorwar.service;

import com.summor.summorwar.models.GameAccount;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCodeAutomationService {

    private final ChromeOptions options;

    @Value("${redeem.sleep-between-accounts-ms:10000}")
    private long sleepBetweenAccountsMs;

    public void redeemForAccounts(List<GameAccount> accounts, String code, RedeemProgressListener listener) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

        try {
            for (int index = 0; index < accounts.size(); index++) {
                GameAccount acc = accounts.get(index);
                listener.onAccountStarted(acc, index, accounts.size());

                RedeemAttemptResult result = redeem(driver, wait, acc, code);
                listener.onAccountFinished(acc, index, accounts.size(), result.success(), result.error());

                log.info("{} -> success={} error={}", acc.getHiveId(), result.success(), result.error());

                if (index < accounts.size() - 1 && sleepBetweenAccountsMs > 0) {
                    Thread.sleep(sleepBetweenAccountsMs);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Redeem job interrupted while sleeping between accounts", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to redeem gift code for accounts", e);
        } finally {
            driver.quit();
        }
    }

    private RedeemAttemptResult redeem(
            WebDriver driver,
            WebDriverWait wait,
            GameAccount acc,
            String code
    ) {
        try {
            driver.get("https://rosa-midman.com/giftcode-summoners-war-sky-arena");

            WebElement serverElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EVTserver")));
            new Select(serverElement).selectByValue(safeValue(acc.getHiveServer()));

            WebElement idBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EVTid")));
            idBox.clear();
            idBox.sendKeys(safeValue(acc.getHiveId()));

            WebElement codeBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EVTcode")));
            codeBox.clear();
            codeBox.sendKeys(safeValue(code));

            WebElement redeemBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("SubmitCoupon")));
            redeemBtn.click();

            WebElement popup1 = waitForPopup(wait);
            if (popup1 == null) {
                return failedResult("No popup appeared after submit");
            }

            String popup1Text = normalizePopupText(popup1.getText());

            if (containsFailKeyword(popup1Text)) {
                clickOkButton(popup1, wait);
                return failedResult(popup1Text);
            }

            clickConfirmButton(popup1, wait);
            Thread.sleep(600);

            WebElement popup2 = waitForPopup(wait);
            if (popup2 == null) {
                return failedResult("No confirmation popup after confirm");
            }

            String popup2Text = normalizePopupText(popup2.getText());
            log.info("Popup2 = {}", popup2Text);

            if (containsSuccessKeyword(popup2Text)) {
                clickOkButton(popup2, wait);
                return successResult();
            }

            clickOkButton(popup2, wait);
            return failedResult(popup2Text);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Redeem interrupted for account {}", acc.getHiveId(), e);
            return failedResult("Redeem interrupted");
        } catch (Exception e) {
            log.warn("Redeem failed for account {}", acc.getHiveId(), e);
            return failedResult(e.getMessage());
        }
    }

    private WebElement waitForPopup(WebDriverWait wait) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".jconfirm-box")));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean containsFailKeyword(String text) {
        return text.contains("FAIL")
                || text.contains("INVALID")
                || text.contains("USED")
                || text.contains("ALREADY")
                || text.contains("ERROR");
    }

    private boolean containsSuccessKeyword(String text) {
        return text.contains("SUCCESS")
                || text.contains("COMPLETED")
                || text.contains("REDEEMED");
    }

    private void clickOkButton(WebElement popup, WebDriverWait wait) {
        try {
            WebElement okBtn = popup.findElement(By.xpath(
                    ".//button[contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OK')]"
            ));
            wait.until(ExpectedConditions.elementToBeClickable(okBtn)).click();
        } catch (Exception e) {
            try {
                WebElement btn = popup.findElement(By.cssSelector(".jconfirm-buttons button"));
                wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
            } catch (Exception ignored) {
                log.debug("Unable to click OK button on popup");
            }
        }
    }

    private void clickConfirmButton(WebElement popup, WebDriverWait wait) {
        try {
            WebElement confirmBtn = null;

            try {
                confirmBtn = popup.findElement(By.cssSelector("button.confirm"));
            } catch (Exception ignored) {
            }

            if (confirmBtn == null) {
                confirmBtn = popup.findElement(By.xpath(
                        ".//button[contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'CONFIRM')]"
                ));
            }

            wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();

        } catch (Exception e) {
            throw new RuntimeException("Failed to click Confirm", e);
        }
    }

    private String normalizePopupText(String text) {
        return safeValue(text).toUpperCase(Locale.ROOT);
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private RedeemAttemptResult successResult() {
        return new RedeemAttemptResult(true, null);
    }

    private RedeemAttemptResult failedResult(String error) {
        String normalizedError = error == null || error.isBlank() ? "Unknown redeem error" : error;
        return new RedeemAttemptResult(false, normalizedError);
    }

    public interface RedeemProgressListener {
        void onAccountStarted(GameAccount account, int index, int total);

        void onAccountFinished(GameAccount account, int index, int total, boolean success, String error);
    }

    private record RedeemAttemptResult(boolean success, String error) {
    }
}
