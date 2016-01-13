package urlshortener2015.navajowhite.web;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
 */

public class NotFoundException extends RuntimeException {

    private String text;

    public NotFoundException(String text) {
        this.text = text;
    }

    public String getText() { return text; }
}
