package urlshortener2015.navajowhite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
 */

@SpringBootApplication
@EnableScheduling
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {

		SpringApplication.run(Application.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

}