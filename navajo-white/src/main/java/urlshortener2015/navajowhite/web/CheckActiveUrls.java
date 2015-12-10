package urlshortener2015.navajowhite.web;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

import java.util.ArrayList;

/**
 * Created by alber on 30/11/2015.
 */
public class CheckActiveUrls implements Runnable {

    @Autowired
    ShortURLRepository shortURLRepository;

    private ArrayList<ShortURL> urls;
    private long numUrls;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CheckActiveUrls.class);

    public CheckActiveUrls() {
        logger.info("Thread creado");
    }

    @Override
    public void run() {
        logger.info("Thread runeado");
        while(true) {
            numUrls = shortURLRepository.count();
            urls = (ArrayList<ShortURL>) shortURLRepository.list(numUrls, (long) 0);

            logger.info("Número de URLS" + String.valueOf(numUrls));

            for (ShortURL s:urls) {
                logger.info("Url encontrada: " + s.getUri());
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
