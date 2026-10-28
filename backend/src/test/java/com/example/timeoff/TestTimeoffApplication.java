package com.example.timeoff;

import org.springframework.boot.SpringApplication;

public class TestTimeoffApplication {

	public static void main(String[] args) {
		SpringApplication.from(TimeoffApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
