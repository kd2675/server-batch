package com.example.batch.common.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public record ChromeRecord(WebDriver webDriver, WebDriverWait webDriverWait) {
}