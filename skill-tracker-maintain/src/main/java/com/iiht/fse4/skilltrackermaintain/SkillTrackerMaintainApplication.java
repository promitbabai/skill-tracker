package com.iiht.fse4.skilltrackermaintain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SkillTrackerMaintainApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillTrackerMaintainApplication.class, args);
	}

}
