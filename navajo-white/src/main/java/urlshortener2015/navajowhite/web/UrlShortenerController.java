package urlshortener2015.navajowhite.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import urlshortener2015.navajowhite.domain.Click;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ClickRepository;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerController {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	@Autowired
	protected CheckActive checkActvive;


	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
		if (l != null && l.getActive() == 1) {
			createAndSaveClick(id, extractIP(request));
			if (l.getSponsor().equals("NO")) {	//vamos a la direccion normal
				return createSuccessfulRedirectToResponse(l);
			} else {	//vamos a la direccion con "++" para que vaya a la publi
				HttpHeaders h = new HttpHeaders();
				h.setLocation(URI.create(l.getHash() + "++"));
				return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
			}
		}
		else {
			//return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			String msg = "";
			if (l == null) {
				msg = "URL has never been active";
			}
			else {
				Timestamp lastReachable = l.getLastReachable();
				if (lastReachable == null) {
					msg = "URL has never been active";
				}
				else {
					msg = "URL was active for last time: " + lastReachable.toString().substring(0, 16);
				}
			}

			throw new NotFoundException(msg);
		}
	}

	protected void createAndSaveClick(String hash, String ip) {

		URL whatismyip = null;
		BufferedReader inIP = null;
		String country = null;
		String city = null;
		String position = null;
		try {
			whatismyip = new URL("http://ip-api.com/line/" + ip + "?fields=country,city,lat,lon");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			inIP = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			country = inIP.readLine();
			city = inIP.readLine();
			position = inIP.readLine() + "," + inIP.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, null, position, ip, country, city);
		cl=clickRepository.save(cl);
		//log.info(cl!=null?"["+hash+"] saved with id ["+cl.getId()+"]":"["+hash+"] was not saved");
	}

	/*@RequestMapping(value = "/NOT_FOUND")
	public String notFound(Model model) {
		return "publicidad";
	}*/

	protected String extractIP(HttpServletRequest request) {

		URL whatismyip = null;
		BufferedReader inIP = null;
		try {
			whatismyip = new URL("https://api.ipify.org");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			inIP = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			return inIP.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

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

		// Create new URL with update_status = 2
		ShortURL su = createAndSaveIfValid(url, sponsor, brand, UUID
				.randomUUID().toString(), extractIP(request));

		if (su != null) {		// Valid URL
			/*try {
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
			su.setActive(0);*/


			checkActvive.addNewURL(su);

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
									id, null)).toUri(), "NO", new Date(
					System.currentTimeMillis()), owner,
					HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null, 0);
			su.setUpdate_status(2);

			return shortURLRepository.save(su);
		} else {
			return null;
		}
	}

}
