package org.helico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class GrimoireApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrimoireApplication.class, args);
	}

}
