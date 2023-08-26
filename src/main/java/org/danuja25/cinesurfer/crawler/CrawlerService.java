package org.danuja25.cinesurfer.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CrawlerService {

    public static final String CAST_CLASS_NAME = "sc-bfec09a1-1 fUguci";
    public static final String TAGS_CLASS_NAME = "ipc-metadata-list-summary-item__t";

    public static void main(String[] args) {
        CrawlerService service = new CrawlerService();
//        service.loadKeywordPage();

        WebDriver driver = getWebDriver();
        driver.get("https://www.imdb.com/title/tt0093870/keywords/?ref_=tt_stry_kw");
        WebElement allbutton = driver.findElement(By.xpath("//*[@id='__next']/main/div/section/div/section/div/div[1]/section[1]/div/ul/div/span[2]/button"));
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", allbutton);
        driver.getTitle();
        driver.get("https://www.imdb.com/title/tt0093870/?ref_=ttkw_ov_i");
    }

    public static WebDriver getWebDriver() {
        System.setProperty("webdriver.chrome.driver","C:\\Users\\DJ\\Documents\\CineSurfer\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
//        options.addExtensions(new File("C:\\Users\\danuj\\Documents\\CineSurfer\\Extensions\\2.0.9_0.crx"));
        options.addExtensions(new File("C:\\Users\\DJ\\Documents\\CineSurfer\\Extensions\\3.17.1_0.crx"));
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

    public void loadTitlePage() {
        Document document = null;
        try {
            document = Jsoup.connect("https://www.imdb.com/title/tt0093870/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.select("p").forEach(System.out::println);
    }

    public void loadKeywordPage() {
        Document document = null;
        try {
            document = Jsoup.connect("https://www.imdb.com/title/tt0100502/keywords/?ref_=tt_stry_kw").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.select("p").forEach(System.out::println);
    }

    private List<String> loadTextElements(final Document document) {
        return document.getElementsByClass(CAST_CLASS_NAME).stream().map(e -> ((TextNode) e.firstChild()).text()).collect(Collectors.toList());
//        driver.findElements(By.className("sc-bfec09a1-1")).stream().map(e -> e.getText()).collect(Collectors.toList())
    }
}
