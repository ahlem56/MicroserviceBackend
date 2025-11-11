package tn.esprit.carpooling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "tn.esprit.carpooling")
@EntityScan(basePackages = "tn.esprit.carpooling.entities")
@EnableJpaRepositories(basePackages = "tn.esprit.carpooling.repositories")
public class CarpoolingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarpoolingApplication.class, args);
	}

}
