package org.danuja25.cinesurfer.controller;

import org.danuja25.cinesurfer.service.ImdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

@RestController
public class RatingsController {

    @Autowired
    ImdbService imdbService;

    @GetMapping("/ratings")
    public ResponseEntity getRatings(@RequestParam String phrase)
    {
        AtomicReference<String> result = new AtomicReference<>();
        imdbService.searchByPhrase(phrase).subscribe(result::set);
        return ResponseEntity.ok(result.get());
    }
}
