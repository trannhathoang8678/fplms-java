package fpt.plms;

import fpt.plms.config.interceptor.GatewayConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ProjectBasedLearningManagementApplication {

	public static void main(String[] args) {
		GatewayConstant.addApiEntities();
		SpringApplication.run(ProjectBasedLearningManagementApplication.class, args);
	}

}
