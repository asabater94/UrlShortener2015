package urlshortener2015.navajowhite.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.google.common.hash.Hashing;
import urlshortener2015.navajowhite.domain.Click;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ClickRepository;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

@RestController
public class UrlShortenerController {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;


	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
		if (l != null) {
			createAndSaveClick(id, extractIP(request));
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	protected void createAndSaveClick(String hash, String ip) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, null, null, ip, null);
		cl=clickRepository.save(cl);
		//log.info(cl!=null?"["+hash+"] saved with id ["+cl.getId()+"]":"["+hash+"] was not saved");
	}

	protected String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {

		ShortURL su = createAndSaveIfValid(url, sponsor, brand, UUID
				.randomUUID().toString(), extractIP(request));

		if (su != null) {		// Valid URL
			try {
				logger.info("URL " + url);
				URL urlServer = new URL(url);
				HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
				urlConn.setConnectTimeout(3000); //<- 3 Seconds Timeout
				urlConn.connect();
				if (urlConn.getResponseCode() == 200) {		// URL reachable
					HttpHeaders h = new HttpHeaders();
					h.setLocation(su.getUri());
					return new ResponseEntity<>(su, h, HttpStatus.CREATED);
				} else {				// URL unreachable
					logger.error("URL unreachable -> " + url);

				}
			}
			catch (IOException e) {
				logger.error("IOException -> " + url);
			}
			su.setActive(0);
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		}
		else {	// No valid URL
			logger.error("SU==null -> " + url);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}


	}

	protected ShortURL createAndSaveIfValid(String url, String sponsor,
			String brand, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			String id = Hashing.murmur3_32()
					.hashString(url, StandardCharsets.UTF_8).toString();
			ShortURL su = new ShortURL(id, url,
					linkTo(
							methodOn(UrlShortenerController.class).redirectTo(
									id, null)).toUri(), sponsor, new Date(
							System.currentTimeMillis()), owner,
					HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null, 1);
			return shortURLRepository.save(su);
		} else {
			return null;
		}
	}


}
