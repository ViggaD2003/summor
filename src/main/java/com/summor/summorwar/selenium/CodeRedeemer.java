//package com.summor.summorwar.selenium;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//
//@Component
//public class CodeRedeemer {
//
//    @Value("${sw.hiveId}")
//    private String hiveId;
//
//    @Value("${sw.server:ASIA}")
//    private String server;
//
//    private final ChromeOptions options;
//
//    public CodeRedeemer(ChromeOptions options) {
//        this.options = options;
//    }
//
//    public boolean redeem(String code) {
//
//        WebDriver driver = new ChromeDriver(options);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
//
//        try {
//            driver.get("https://rosa-midman.com/giftcode-summoners-war-sky-arena");
//
//            // ================== FILL FORM =====================
//
//            WebElement serverElement = wait.until(
//                    ExpectedConditions.visibilityOfElementLocated(By.id("EVTserver"))
//            );
//            new Select(serverElement).selectByValue(server);
//
//            WebElement idBox = wait.until(
//                    ExpectedConditions.visibilityOfElementLocated(By.id("EVTid"))
//            );
//            idBox.clear();
//            idBox.sendKeys(hiveId);
//
//            WebElement codeBox = wait.until(
//                    ExpectedConditions.visibilityOfElementLocated(By.id("EVTcode"))
//            );
//            codeBox.clear();
//            codeBox.sendKeys(code);
//
//            WebElement redeemBtn = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.id("SubmitCoupon"))
//            );
//            redeemBtn.click();
//
//            // ================== POPUP 1 =====================
//
//            WebElement popup1 = waitForPopup(wait);
//            if (popup1 == null) {
//                System.out.println("[WARN] No popup appeared after submit → FAIL");
//                return false;
//            }
//
//            String p1Text = popup1.getText().toUpperCase();
//
//            // Nếu popup đầu tiên đã FAILED → trả về luôn
//            if (containsFailKeyword(p1Text)) {
//                clickOkButton(popup1, wait);
//                System.out.println("[FAILED] " + code + " → Popup1 FAIL: " + p1Text);
//                return false;
//            }
//
//            // Nếu popup1 có nút Confirm → nhấn Confirm
//            clickConfirmButton(popup1, wait);
//            Thread.sleep(600);
//
//            // ================== POPUP 2 =====================
//            WebElement popup2 = waitForPopup(wait);
//            if (popup2 == null) {
//                System.out.println("[WARN] No popup2 after confirm → assume FAIL");
//                return false;
//            }
//
//            String p2Text = popup2.getText().toUpperCase();
//            System.out.println("[INFO] Popup2 = " + p2Text);
//
//            if (containsSuccessKeyword(p2Text)) {
//                clickOkButton(popup2, wait);
//                System.out.println("[SUCCESS] Code redeemed → " + code);
//                return true;
//            }
//
//            // Otherwise FAIL
//            clickOkButton(popup2, wait);
//            System.out.println("[FAILED] Final result FAIL → " + code);
//            return false;
//
//        } catch (Exception e) {
//            System.out.println("[EXCEPTION] Redeem error: " + code);
//            e.printStackTrace();
//            return false;
//        } finally {
//            try { driver.quit(); } catch (Exception ignored) {}
//        }
//    }
//
//    // ========================================================
//    // HELPER FUNCTIONS
//    // ========================================================
//
//    private WebElement waitForPopup(WebDriverWait wait) {
//        try {
//            return wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.cssSelector(".jconfirm-box")
//            ));
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private boolean containsFailKeyword(String text) {
//        return text.contains("FAIL")
//                || text.contains("INVALID")
//                || text.contains("USED")
//                || text.contains("ALREADY")
//                || text.contains("ERROR");
//    }
//
//    private boolean containsSuccessKeyword(String text) {
//        return text.contains("SUCCESS")
//                || text.contains("COMPLETED")
//                || text.contains("REDEEMED");
//    }
//
//    private void clickOkButton(WebElement popup, WebDriverWait wait) {
//        try {
//            WebElement okBtn = popup.findElement(By.xpath(
//                    ".//button[contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'OK')]"
//            ));
//            wait.until(ExpectedConditions.elementToBeClickable(okBtn)).click();
//        } catch (Exception e) {
//            // fallback
//            try {
//                WebElement btn = popup.findElement(By.cssSelector(".jconfirm-buttons button"));
//                wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
//            } catch (Exception ignored) {}
//        }
//    }
//
//    private void clickConfirmButton(WebElement popup, WebDriverWait wait) {
//        try {
//            WebElement confirmBtn = null;
//
//            // Try by CSS class
//            try { confirmBtn = popup.findElement(By.cssSelector("button.confirm")); }
//            catch (Exception ignored) {}
//
//            // Try by text
//            if (confirmBtn == null) {
//                confirmBtn = popup.findElement(By.xpath(
//                        ".//button[contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'CONFIRM')]"
//                ));
//            }
//
//            wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
//
//        } catch (Exception e) {
//            System.out.println("[ERROR] Failed to click Confirm");
//            e.printStackTrace();
//        }
//    }
//}
