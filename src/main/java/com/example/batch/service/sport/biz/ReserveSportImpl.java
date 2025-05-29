package com.example.batch.service.sport.biz;

import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveSportImpl implements ReserveSportSVC {
    private final MattermostUtil mattermostUtil;

    private record ChromeRec(WebDriver webDriver, WebDriverWait webDriverWait) {
    }

    @NotNull
    private static ChromeRec chromeRec() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--single-process");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");
//        InternetExplorerOptions options = new InternetExplorerOptions();
//        options.setCapability("ignoreProtectedModeSettings", true);
        WebDriver webDriver = new ChromeDriver(options);
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        ChromeRec chromeRec = new ChromeRec(webDriver, webDriverWait);
        return chromeRec;
    }

    @Override
    public void test1() {
        ChromeRec chromeRec = chromeRec();

        try {
            login(chromeRec.webDriver(), chromeRec.webDriverWait());

            reserve(chromeRec.webDriver(), chromeRec.webDriverWait());
        } catch (Exception e) {
            log.error("error > {}", e);
        } finally {
            chromeRec.webDriver().quit();
        }
    }

    private void login(WebDriver driver, WebDriverWait wait) {
        driver.get("https://www.gwangjusportsinfo.org/space/space_view/1");
        driver.navigate().to("https://www.gwangjusportsinfo.org/login");

        WebElement idElement = driver.findElement(By.cssSelector("input[placeholder='아이디']"));
        idElement.sendKeys("kd2675");

        WebElement pwElement = driver.findElement(By.cssSelector("input[placeholder='비밀번호']"));
        pwElement.sendKeys("Whitered2@");

        // 로그인 버튼 클릭
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='submit'][name='btn_submit'][value='로그인']")
        ));
        loginButton.click();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void reserve(WebDriver driver, WebDriverWait wait) {
        driver.get("https://www.gwangjusportsinfo.org/reservation/reservation_view/1/4?agree=1");

        // XPath로 날짜 ID 내에 있는 li 중, b 텍스트가 원하는 시간과 일치하는 li 클릭
        String xpath = String.format("//td[@id='%s']//li[.//b[contains(normalize-space(), '%s')]]", "2025-04-28", "주간(13:00~15:00)");

        WebElement timeSlot = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        timeSlot.click();

        // 특정 요소가 나타날 때까지 대기 (예: 'useCnt' 필드가 로드될 때까지)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("useCnt")));
        // useCnt 입력 필드 찾아서 '4' 입력
        WebElement inputField = driver.findElement(By.name("useCnt"));
        inputField.sendKeys("4");

        // 2. 체크박스 클릭 및 팝업 열기
        WebElement checkbox = driver.findElement(By.id("userAgreement1"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checkbox);

        // 3. 팝업이 뜨면 '[닫기]' 버튼을 클릭하여 팝업 닫기
        closePopup(driver, wait);

        // 4. 다음 버튼 클릭 (팝업이 닫힌 후)
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.btn_next.btn_confirm")));
        nextButton.click();

        System.out.println("test");
    }

    private static void closePopup(WebDriver driver, WebDriverWait wait) {
        try {
            // 팝업이 새로운 창(Window)으로 열린 경우 처리
            String mainWindow = driver.getWindowHandle(); // 메인 윈도우 저장
            Set<String> allWindows = driver.getWindowHandles();

            for (String windowHandle : allWindows) {
                if (!windowHandle.equals(mainWindow)) {
                    driver.switchTo().window(windowHandle); // 팝업 윈도우로 전환

                    try {
                        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='[닫기]']")));
                        closeButton.click();
                        System.out.println("팝업 닫기 완료");
                    } catch (Exception e) {
                        System.out.println("팝업 닫기 버튼 클릭 실패");
                    }

                    driver.close(); // 팝업 창 닫기
                    driver.switchTo().window(mainWindow); // 다시 메인 윈도우로 전환
                }
            }
        } catch (Exception e) {
            System.out.println("팝업이 없거나 예외 발생: " + e.getMessage());
        }
    }
}
