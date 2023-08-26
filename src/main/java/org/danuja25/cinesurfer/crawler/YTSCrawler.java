package org.danuja25.cinesurfer.crawler;

import org.danuja25.cinesurfer.model.flix.Flix;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static org.danuja25.cinesurfer.Constants.MOVIE_TYPE;

@Service
public class YTSCrawler {
    private static final String BROWSE_PAGE = "https://yts.mx/browse-movies";

    @Autowired
    final ImdbCrawler imdbCrawler;

    public YTSCrawler(ImdbCrawler imdbCrawler) {
        this.imdbCrawler = imdbCrawler;
    }

    public static void main(String[] args) {
        WebDriver driver = CrawlerService.getWebDriver();
        Flix flix = new Flix();
        flix.setName("Bird Box");
        flix.setYear(2018);
//        fillYTSDetails(driver, List.of(flix));

    }

    public List<Flix> fillYTSDetails(WebDriver driver, List<Flix> movies) {
        List<String> unsuccessfulLinks = new ArrayList<>();
        List<Flix> unsuccessfulMovies = new ArrayList<>();
        AtomicInteger macthCount = new AtomicInteger();
        movies.forEach(movie -> updateYTSMovieDetails(driver, unsuccessfulLinks, macthCount, movie));
        unsuccessfulLinks.forEach(System.out::println);
        System.out.println("YTS matches : " + macthCount.get());
        return unsuccessfulMovies;
    }

    private void updateYTSMovieDetails(WebDriver driver, List<String> unsuccessfulLinks, AtomicInteger macthCount, Flix movie) {
        driver.get(BROWSE_PAGE);
        try {
            driver.findElement(By.xpath("//*[@id='main-search-fields']/input")).sendKeys(movie.getName());
            driver.findElement(By.xpath("//*[@id='main-search-btn']/input")).click();
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(d -> d.findElements(By.className("browse-movie-wrap")));
            Optional<WebElement> match = driver.findElements(By.className("browse-movie-wrap")).stream().filter(e -> (sanitiseName(e.findElement(By.className("browse-movie-title")).getText()).equalsIgnoreCase(movie.getName())) && (e.findElement(By.className("browse-movie-year")).getText().equals(String.valueOf(movie.getYear())))).findFirst();
            if (match.isPresent()) {
                match.get().findElement(By.className("browse-movie-link")).click();
                wait.until(d -> d.findElement(By.cssSelector("[title=\"IMDb Rating\"]")));
                String imdbLink = driver.findElement(By.cssSelector("[title=\"IMDb Rating\"]")).getAttribute("href");
                movie.setImdbId(extractImdbIdFromYTS(imdbLink));
                imdbCrawler.fillSecondaryDetails(driver, movie);
                System.out.println(movie.getName() + " " + imdbLink);
                macthCount.getAndIncrement();
            } else {
                imdbCrawler.fillImdbDetails(driver, unsuccessfulLinks, movie);
                unsuccessfulLinks.add(movie.getName() + " " + movie.getYear() + " No match");
            }
        } catch (Exception e) {
            unsuccessfulLinks.add(movie.getName() + " " + movie.getYear() + " Exception");
        }
    }

    public void getYTSMovies(WebDriver driver, List<Flix> movies) {
        List<String> unsuccessfulLinks = new ArrayList<>();
        AtomicInteger macthCount = new AtomicInteger();
        movies.stream().filter(item -> item.getName().contains("YTS.")).forEach(item -> {
            try {
                String movieName = item.getName().substring(0, item.getName().indexOf('(')).trim();
                String year = item.getName().substring(item.getName().indexOf('('), item.getName().indexOf(')')).replace("(", "").trim();
                item.setName(movieName);
                item.setYear(Integer.parseInt(year));
                item.setType(MOVIE_TYPE);
                updateYTSMovieDetails(driver, unsuccessfulLinks, macthCount, item);
            } catch (Exception e) {
                System.out.println(item.getName());
            }
        });
        unsuccessfulLinks.forEach(System.out::println);
        System.out.println("YTS matches : " + macthCount.get());
        System.out.println("YTS misses : " + unsuccessfulLinks.size());
    }

    public void getRarbgMovies(WebDriver driver, List<Flix> movies) {
        List<String> unsuccessfulLinks = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d{4}");
        movies.stream().filter(item -> item.getName().contains("RARBG")).forEach(item -> {
            Matcher matcher = pattern.matcher(item.getName());
            if (matcher.find()) {
                String name = item.getName().substring(0, matcher.start()).replace(".", " ").trim();
                String year = item.getName().substring(matcher.start(), matcher.end(0));
                if (!year.equals("1080")) {
                    item.setName(name);
                    item.setYear(Integer.parseInt(year));
                    imdbCrawler.fillImdbDetails(driver, unsuccessfulLinks, item);
                }
            }
        });
        unsuccessfulLinks.forEach(System.out::println);
        System.out.println("RARBG misses : " + unsuccessfulLinks.size());
    }

    public void getPSAMovies(WebDriver driver, List<Flix> movies) {
        List<String> unsuccessfulLinks = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d{4}");
        movies.stream().filter(item -> item.getName().contains("PSA")).forEach(item -> {
            Matcher matcher = pattern.matcher(item.getName());
            if (matcher.find()) {
                String name = item.getName().substring(0, matcher.start()).replace(".", " ").trim();
                String year = item.getName().substring(matcher.start(), matcher.end(0));
                if (!year.equals("1080")) {
                    item.setName(name);
                    item.setYear(Integer.parseInt(year));
                    imdbCrawler.fillImdbDetails(driver, unsuccessfulLinks, item);
                }
            }
        });
        unsuccessfulLinks.forEach(System.out::println);
        System.out.println("PSA misses : " + unsuccessfulLinks.size());
    }

    public void getMovies(WebDriver driver, List<Flix> movies) {
        List<String> unsuccessfulLinks = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d{4}");
        movies.stream().filter(item -> isNull(item.getImdbId()) && !(item.getName().contains("S0")) && !(item.getName().contains("RARBG")) && !(item.getName().contains("YTS"))).forEach(item -> {
            Matcher matcher = pattern.matcher(item.getName());
            if (matcher.find()) {
                String name = item.getName().substring(0, matcher.start()).replaceAll("[^A-Za-z0-9\\s]+", "").trim();
                String year = item.getName().substring(matcher.start(), matcher.end(0));
                if (!year.equals("1080")) {
                    item.setName(name);
                    item.setYear(Integer.parseInt(year));
                    imdbCrawler.fillImdbDetails(driver, unsuccessfulLinks, item);
                }
            }
        });
        unsuccessfulLinks.forEach(System.out::println);
        System.out.println("Other movies misses : " + unsuccessfulLinks.size());
    }

    private static String sanitiseName(String name) {
        if (name.startsWith("[")) {
            return name.replaceFirst("\\[(.*?)\\]", "").trim();
        }
        return name.trim();
    }

    private static String extractImdbIdFromYTS(final String link) {
        return link.replace("https://www.imdb.com/title/","").replace("/","");
    }
}
