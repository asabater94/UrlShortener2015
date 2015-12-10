package urlshortener2015.navajowhite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import urlshortener2015.navajowhite.web.CheckActiveUrls;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {

		SpringApplication.run(Application.class, args);
		//CheckActiveUrls cau = new CheckActiveUrls();
		//cau.run();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

}