package urlshortener2015.navajowhite.domain;

import java.sql.Date;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
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
