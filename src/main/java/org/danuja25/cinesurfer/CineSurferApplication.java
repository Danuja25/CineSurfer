package org.danuja25.cinesurfer;

import org.danuja25.cinesurfer.service.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CineSurferApplication implements CommandLineRunner {

	@Autowired
	private MapperService mapperService;

	public static void main(String[] args) {
		SpringApplication.run(CineSurferApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		mapperService.map();
	}
}
