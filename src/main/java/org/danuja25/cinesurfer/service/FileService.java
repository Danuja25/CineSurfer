package org.danuja25.cinesurfer.service;

import org.apache.commons.io.FileUtils;
import org.danuja25.cinesurfer.model.flix.Flix;
import org.danuja25.cinesurfer.model.flix.FlixRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class FileService {

    public List<Flix> listFiles(String dir) throws IOException {
        System.out.println(dir);
        return Files.list(Path.of(dir)).map(this::createFlix).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Flix> refreshFromDisk(final FlixRepository flixRepository, final List<Flix> flixFromDisk, final List<Flix> flixFromRepo) {
        final Set<String> pathsFromDisk = new HashSet<>();
        final Set<String> pathsFromRepo = new HashSet<>();
        final List<Flix> newFlixes =  new ArrayList<>();

        flixFromDisk.forEach(flix -> pathsFromDisk.add(flix.getPath()));
        flixFromRepo.forEach(flix -> pathsFromRepo.add(flix.getPath()));

        flixFromRepo.stream().filter(flix -> isNull(flix.getImdbId())).filter(flix -> !pathsFromDisk.contains(flix.getPath())).forEach(flix -> flix.setDeleted(true));
        flixFromDisk.stream().filter(flix -> !pathsFromRepo.contains(flix.getPath())).forEach(flix -> {
            newFlixes.add(flix);
            System.out.println("Added " + flix.getName() + " " + flix.getPath());
        });

        flixRepository.saveAll(flixFromRepo);
        return newFlixes;
    }

    private Flix createFlix(final Path filePath) {
        BigDecimal size;
        File file = new File(filePath.toString());
        if(!file.isHidden()) {
            if(FileUtils.isDirectory(file)) {
                size = new BigDecimal(FileUtils.sizeOfDirectory(file));
            } else {
                size = new BigDecimal(FileUtils.sizeOf(file));
            }
            BigDecimal sizeInGB = size.divide(new BigDecimal(1024 * 1024 * 1024)).setScale(2, RoundingMode.CEILING);
            return new Flix(filePath.getFileName().toString(), filePath.getRoot().toString(), filePath.toAbsolutePath().toString(), sizeInGB);
        }
        return null;
    }

}
