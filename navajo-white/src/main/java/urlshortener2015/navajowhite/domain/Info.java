package urlshortener2015.navajowhite.domain;

/**
 * Created by coke on 12/12/2015.
 */
public class Info {

    private String site;
    private String clicks;

    public Info (String site, String clicks){
        this.site = site;
        this.clicks = clicks;
    }

    public String getSite() {
        return site;
    }

    public String getClicks() {
        return clicks;
    }

}
