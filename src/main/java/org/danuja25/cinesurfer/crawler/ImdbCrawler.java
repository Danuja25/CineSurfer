package org.danuja25.cinesurfer.crawler;

import org.danuja25.cinesurfer.model.castMember.CastMember;
import org.danuja25.cinesurfer.model.castMember.CastMemberRepository;
import org.danuja25.cinesurfer.model.flix.Flix;
import org.danuja25.cinesurfer.model.tag.Tag;
import org.danuja25.cinesurfer.model.tag.TagRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class ImdbCrawler {
    private static final String SEARCH_PAGE = "https://www.imdb.com/search/title/?ref_=fn_asr_tt";
    private static final String FLIX_PAGE = "https://www.imdb.com/title/";
    @Autowired
    private final CastMemberRepository castMemberRepository;
    @Autowired
    private final TagRepository tagRepository;

    public ImdbCrawler(CastMemberRepository castMemberRepository, TagRepository tagRepository) {
        this.castMemberRepository = castMemberRepository;
        this.tagRepository = tagRepository;
    }

    public static void main(String[] args) {
        WebDriver driver = CrawlerService.getWebDriver();
        Flix flix = new Flix();
        flix.setName("Through My Window Across The Sea");
        flix.setYear(2023);
//        searchMovies(driver, List.of(flix));
//        flix.setImdbId("tt21245882");
//        fillCastDetails(driver, flix);
    }

    public void searchMovies(WebDriver driver, List<Flix> movies) {
        List<String> unsuccessfulLinks = new ArrayList<>();
        movies.forEach(movie -> fillImdbDetails(driver, unsuccessfulLinks, movie));
        unsuccessfulLinks.forEach(System.out::println);
    }

    public void fillImdbDetails(WebDriver driver, List<String> unsuccessfulLinks, Flix movie) {
        driver.get(SEARCH_PAGE);
        try {
            driver.findElement(By.xpath("//*[@id=\"main\"]/div[1]/div[2]/input")).sendKeys(movie.getName());
            driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[2]/input[1]")).sendKeys(String.valueOf(movie.getYear()));
            driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[2]/input[2]")).sendKeys(String.valueOf(movie.getYear()));
            driver.findElement(By.xpath("//*[@id=\"main\"]/div[20]/div[2]/input[1]")).sendKeys("60");
            driver.findElement(By.xpath("//*[@id=\"main\"]/p[3]/button")).click();
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(d -> d.findElements(By.className("lister-item-header")));
            Optional<WebElement> match = driver.findElements(By.className("lister-item-header")).stream().filter(item -> {
                String title = item.findElement(By.tagName("a")).getText()
                        .replace(":", "").replace("-", "");
                String year = item.findElement(By.className("lister-item-year")).getText().replace("(", "").replace(")", "");
                return title.equalsIgnoreCase(movie.getName()) &&
                        year.equals(String.valueOf(movie.getYear()));
            }).findFirst();
            if (match.isPresent()) {
                String imdbLink = match.get().findElement(By.tagName("a")).getAttribute("href");
                movie.setImdbId(extractImdbId(imdbLink));
                fillSecondaryDetails(driver, movie);
                System.out.println(movie.getName() + " " + imdbLink);
            } else {
                unsuccessfulLinks.add(movie.getName() + " " + movie.getYear() + " No match");
            }
        } catch (Exception e) {
            unsuccessfulLinks.add(movie.getName() + " " + movie.getYear() + " Exception");
        }
    }

    public void fillSecondaryDetails(WebDriver driver, Flix movie) {
        fillCastDetails(driver, movie);
        fillTagDetails(driver, movie);
    }

    public void fillCastDetails(WebDriver driver, Flix movie) {
        driver.get(FLIX_PAGE.concat(movie.getImdbId()));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(d -> d.findElements(By.className("lister-item-header")));
        try {
            String title = driver.findElements(By.cssSelector("[data-testid='hero__pageTitle']")).get(0).getText();
            movie.setTitle(title);
            driver.findElements(By.className("title-cast-item__actor"));
            driver.findElements(By.cssSelector("[data-testid='title-cast-item__actor']")).forEach(webElement -> {
                CastMember castMember = new CastMember();
                castMember.setName(webElement.getText());
                castMember.setImdbId(extractCastId(webElement.getAttribute("href")));
                movie.getCast().add(castMember);
                castMemberRepository.save(castMember);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillTagDetails(WebDriver driver, Flix movie) {
        driver.get(FLIX_PAGE.concat(movie.getImdbId()).concat("/keywords"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(d -> d.findElements(By.className("ipc-metadata-list-summary-item__t")));
        try {
            wait.until(d -> ExpectedConditions.elementToBeClickable(By.className("ipc-see-more__button")));
            WebElement seeMoreButton = driver.findElement(By.className("ipc-see-more__button"));
            if(nonNull(seeMoreButton)) {
                seeMoreButton.click();
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
            }

        } catch (WebDriverException e) {
        }
        try {
            driver.findElements(By.className("ipc-metadata-list-summary-item__t")).forEach(webElement -> {
                Tag tag = new Tag();
                tag.setName(webElement.getText());
                tag.setImdbCode(extractTagCode(webElement.getAttribute("href")));
                tagRepository.save(tag);
                movie.getTags().add(tag);
            });
        } catch (Exception e) {
        }
    }

    private static String extractImdbId(final String link) {
        return link.replace("https://www.imdb.com/title/", "").replace("/?ref_=adv_li_tt", "");
    }

    private static String extractCastId(final String link) {
        String filteredLink = link.replace("https://www.imdb.com/name/", "");
        return filteredLink.substring(0,filteredLink.indexOf('/'));
    }

    private static String extractTagCode(final String link) {
        String filtered = link.replace("/search/keyword/?keywords=", "");
        return filtered.substring(0, filtered.indexOf('&'));
    }
}
