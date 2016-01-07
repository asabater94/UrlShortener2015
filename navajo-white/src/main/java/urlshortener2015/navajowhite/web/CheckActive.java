package urlshortener2015.navajowhite.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

@Component
public class CheckActive implements InitializingBean {

    @Autowired
    private ShortURLRepository shortURLRepository;
    private final int NUM_THREADS = 2;                  // Number of threads checking urls
    private final int TIME_DIFF = 5*60*1000;            // Min time difference between two active checks (ms)

    private BlockingQueue<ShortURL> queue;


    @Override
    public void afterPropertiesSet() throws Exception {
        queue = new PriorityBlockingQueue<ShortURL>();

        Thread[] t = new Thread[NUM_THREADS];
        for (int i=0; i<t.length; i++) {
            t[i] = new Thread(new CheckActiveThread(i, shortURLRepository, queue));
            t[i].start();
        }
    }


    @Scheduled(fixedRate = 10000)
    public void reportCurrentTime() {

        java.sql.Timestamp minTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime() - TIME_DIFF);
        //System.out.println( new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()) + "   " + minTimestamp);
        List<ShortURL> list = shortURLRepository.listToUpdate(minTimestamp);

        //System.out.println("URLs found: " + list);

        for (ShortURL s:list) {
            //System.out.println("Puting " + s.getTarget() + "   " + currentTimestamp + "   " + s.getLastChange());

            //long diff = nextTimestamp.getTime() - s.getLastChange().getTime();

            // TO_DO :
                // OBTENER CALCULO DIRECTAMENTE DE BD

           // if (diff >= TIME_DIFF) {
                try {

                    s.setUpdate_status(1);      // Updating active URL
                    System.out.println("Puting " + s.getTarget() + "   activo:" + s.getActive() + " size:" + queue.size() + " status:" + s.getUpdate_status());

                    shortURLRepository.update(s);
                    queue.put(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            //}
        }
    }


    /**
     * Adds a new ShortURL to the queue
     */
    public void addNewURL(ShortURL url) {
        System.out.println("URL created -> " + url.getTarget());
        try {
            Object[] array = queue.toArray();
            //System.out.println("--------------------------------------------");
           // printArray(array);
            queue.put(url);
            Object[] array2 = queue.toArray();
            //printArray(array2);
            //System.out.println("--------------------------------------------");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void printArray(Object[] array) {
        for (Object o:array) {
            ShortURL s = (ShortURL) o;

            System.out.println("\t " + s.getTarget() + "\t" + s.getHash() + "\t" + s.getUpdate_status() + "\tsize:" + queue.size() + "\tactivo:" + s.getActive());
        }
        System.out.println();
    }


}