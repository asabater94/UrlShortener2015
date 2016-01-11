package urlshortener2015.navajowhite.web;

/**
 * Created by alber on 27/12/2015.
 */
public class NotFoundException extends RuntimeException {

    private String text;

    public NotFoundException(String text) {
        this.text = text;
    }

    public String getText() { return text; }
}
