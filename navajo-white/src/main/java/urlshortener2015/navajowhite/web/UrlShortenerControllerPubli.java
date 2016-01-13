package urlshortener2015.navajowhite.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
 */

@RestController
public class UrlShortenerControllerPubli {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UrlShortenerControllerPubli.class);
    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected CheckActive checkActvive;

    protected String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    /**
     * Method that returns the final web (target) of a URI (id) that obtains in the value parameter.
     * @param id
     * @param request
     */
    @RequestMapping(value = "/redireccion/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request) {

        ShortURL l = shortURLRepository.findByKey(id);
        HttpHeaders h = new HttpHeaders();
        return new ResponseEntity<>(l.getTarget(), h, HttpStatus.OK);



    }

    protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

    /**
     * Method that creates a new URI with publicity and call to the method createAndSaveIfValid for saving it in the
     * database.
     * @param url -
     * @param sponsor -
     * @param brand -
     * @param request -
     */
    @RequestMapping(value = "/publicidad", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              @RequestParam(value = "brand", required = false) String brand,
                                              HttpServletRequest request) {
        ShortURL su = createAndSaveIfValid(url, sponsor, brand, UUID
                .randomUUID().toString(), extractIP(request));
        if (su != null) {
            checkActvive.addNewURL(su);

            HttpHeaders h = new HttpHeaders();
            h.setLocation(su.getUri());
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method that save a new URI with publicity in the database if this is valid.
     * @param url -
     * @param sponsor define if the URI has publicity
     * @param brand -
     * @param owner -
     * @param ip -
     */
    protected ShortURL createAndSaveIfValid(String url, String sponsor,
                                            String brand, String owner, String ip) {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http",
                "https" });
        if (urlValidator.isValid(url)) {
            String id = Hashing.murmur3_32()
                    .hashString(url, StandardCharsets.UTF_8).toString();
            ShortURL su = new ShortURL(id, url,
                    linkTo(
                            methodOn(UrlShortenerController.class).redirectTo(  //save with publicity
                                    id, null)).toUri(), "SI", new Date(
                    System.currentTimeMillis()), owner,
                    HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null,0);
            su.setUpdate_status(2);

            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }
}
