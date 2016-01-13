package urlshortener2015.navajowhite.domain;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
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
