package com.notification_service.backend;

import com.notification_service.backend.Services.InternalServices.ConfigLoaderServiceImpl;
import com.notification_service.backend.Services.DBServices.MongoDBServiceImpl;
import com.notification_service.backend.Services.DBServices.MySQLDBServiceImpl;
import com.notification_service.backend.Services.InternalServices.EncryptionServiceImpl;
import com.notification_service.backend.Services.InternalServices.ObjectMapperServiceImpl;
import com.notification_service.backend.Services.InternalServices.SchedulerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication @EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, QuartzAutoConfiguration.class}) public class BackendApplication {

	@Autowired MySQLDBServiceImpl mySQLDBService;
	@Autowired SchedulerServiceImpl schedulerService;
	@Autowired MongoDBServiceImpl mongoDBService;
	@Autowired ObjectMapperServiceImpl objectMapperService;
	@Autowired ConfigLoaderServiceImpl configLoaderService;
	@Autowired EncryptionServiceImpl encryptionService;

	List<String>initServices = Arrays.asList("quartz");

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@PostConstruct private void initServices() {
		if(initServices.contains("quartz")) {
			mySQLDBService.initService();
		}
		loadMandatoryServices();
	}
	private void loadMandatoryServices() {
		//System.out.println("INITIALIZE MANDATORY SERVICES");
		schedulerService.initService();
		mongoDBService.initService();
		objectMapperService.initService();
		configLoaderService.initService();
		encryptionService.initService();
	}

}
