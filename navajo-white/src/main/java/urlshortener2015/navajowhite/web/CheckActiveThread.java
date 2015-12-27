package urlshortener2015.navajowhite.web;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

/**
 * Created by alber on 17/12/2015.
 */
public class CheckActiveThread implements Runnable {

    private ShortURLRepository shortURLRepository;
    private BlockingQueue<ShortURL> queue;
    private int id;

    public CheckActiveThread(int id, ShortURLRepository shortURLRepository, BlockingQueue<ShortURL> queue) {
        System.out.println(id + " Thread created");
        this.shortURLRepository = shortURLRepository;
        this.queue = queue;
        this.id = id;
    }

    @Override
    public void run() {

        while (true) {

            try {
                ShortURL s = queue.take();
                System.out.println(id + " Taking " + s.getTarget() + "   activo:" + s.getActive() + " size:" + queue.size() + " status:" + s.getUpdate_status());

                URL urlServer = null;
                try {
                    urlServer = new URL(s.getTarget());

                    HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                    urlConn.setConnectTimeout(3000); //<- 3 Seconds Timeout
                    urlConn.connect();
                    if (urlConn.getResponseCode() == 200) {		// URL reachable
                        s.setActive(1);
                    } else {				// URL unreachable
                        s.setActive(0);
                    }

                } catch (java.io.IOException e) {
                    s.setActive(0);
                }

                s.setLastChange(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                s.setUpdate_status(0);              // ROW status updated
                shortURLRepository.update(s);
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
