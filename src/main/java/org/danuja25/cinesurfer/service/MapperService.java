package org.danuja25.cinesurfer.service;

import org.apache.commons.lang3.StringUtils;
import org.danuja25.cinesurfer.crawler.CrawlerService;
import org.danuja25.cinesurfer.crawler.YTSCrawler;
import org.danuja25.cinesurfer.model.SurferProps;
import org.danuja25.cinesurfer.model.flix.Flix;
import org.danuja25.cinesurfer.model.flix.FlixRepository;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Service
public class MapperService {

    @Autowired
    private final FileService fileService;
    @Autowired
    private final FlixRepository flixRepository;
    @Autowired
    private final YTSCrawler ytsCrawler;
    @Autowired
    private final SurferProps props;
    final boolean loadFromDisk;

    public MapperService(FileService fileService, FlixRepository flixRepository, YTSCrawler ytsCrawler, SurferProps props) {
        this.fileService = fileService;
        this.flixRepository = flixRepository;
        this.ytsCrawler = ytsCrawler;
        this.props = props;
        loadFromDisk = true;
    }

    public void map() {
        WebDriver driver = CrawlerService.getWebDriver();
        AtomicReference<List<Flix>> diskFlixes = new AtomicReference<>(new ArrayList<>());
        loadMovies(diskFlixes, loadFromDisk);
        List<Flix> repositoryFlixes = flixRepository.findAll();
        List<Flix> newFlixes = fileService.refreshFromDisk(flixRepository, diskFlixes.get(), repositoryFlixes);
        getMovieDetails(driver, newFlixes);
        deDuplicate(repositoryFlixes, newFlixes);
        repositoryFlixes.addAll(newFlixes);
        flixRepository.saveAll(repositoryFlixes);
    }

    private void deDuplicate(List<Flix> repositoryFlixes, List<Flix> newFlixes) {
        HashMap<String, Flix> repoFlixMap = new HashMap<>();
        repositoryFlixes.stream().filter(flix -> StringUtils.isNotBlank(flix.getImdbId())).forEach(flix -> repoFlixMap.put(flix.getImdbId(), flix));
        newFlixes.stream().filter(flix -> StringUtils.isNotBlank(flix.getImdbId())).filter(flix -> repoFlixMap.containsKey(flix.getImdbId())).forEach(flix -> {
            System.out.println("New : " + flix.getPath() + " || Existing: " + repoFlixMap.get(flix.getImdbId()).getPath());
            newFlixes.remove(flix);
        });
    }

    public void getMovieDetails(WebDriver driver, List<Flix> flixes) {
        ytsCrawler.getYTSMovies(driver, flixes);
        ytsCrawler.getRarbgMovies(driver, flixes);
        ytsCrawler.getPSAMovies(driver, flixes);
        ytsCrawler.getMovies(driver, flixes);
    }

    private void loadMovies(AtomicReference<List<Flix>> flixes, final boolean loadFromDisk) {
        if (loadFromDisk) {
            IntStream.rangeClosed(props.getStartDrive().charAt(0), props.getEndDrive().charAt(0)).forEach(c -> {
                try {
                    flixes.get().addAll(fileService.listFiles((char) c + ":/"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            List<String> otherLocations = Arrays.stream(props.getOtherLocations().split(",")).map(String::trim).toList();
            otherLocations.forEach(location -> {
                try {
                    flixes.get().addAll(fileService.listFiles(location));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            flixRepository.findByImdbIdIsNull();
        }

    }
}
