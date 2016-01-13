package urlshortener2015.navajowhite.web;

import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
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

    /**
     * Wait for a URL to update and check his status
     */
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
                    urlConn.setConnectTimeout(5000); //<- 5 Seconds Timeout
                    urlConn.connect();
                    if (urlConn.getResponseCode() == 200) {		// URL reachable
                        s.setActive(1);
                        s.setLastReachable(new Timestamp(Calendar.getInstance().getTime().getTime()));
                    } else {				// URL unreachable
                        s.setActive(0);
                    }

                } catch (java.io.IOException e) {
                    s.setActive(0);
                }

                s.setLastChange(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                s.setUpdate_status(0);              // ROW status updated
                shortURLRepository.update(s);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
