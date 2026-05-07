package org.scottishtecharmy.wishaw_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WishawJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(WishawJavaApplication.class, args);
	}

}
