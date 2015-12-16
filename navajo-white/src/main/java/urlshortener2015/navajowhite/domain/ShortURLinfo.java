package urlshortener2015.navajowhite.domain;

import java.sql.Date;

/**
 * Created by coke on 14/12/2015.
 */
public class ShortURLinfo {

    private String target;
    private Date created;
    private Long clicks;

    public ShortURLinfo (String target, Date created, Long clicks){
        this.target = target;
        this.created = created;
        this.clicks = clicks;
    }

    public String getTarget() {
        return target;
    }

    public Date getCreated() {
        return created;
    }

    public Long getClicks() {
        return clicks;
    }

}
