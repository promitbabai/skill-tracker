package com.iiht.fse4.skilltrackersearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SkillTrackerSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillTrackerSearchApplication.class, args);
	}

}
