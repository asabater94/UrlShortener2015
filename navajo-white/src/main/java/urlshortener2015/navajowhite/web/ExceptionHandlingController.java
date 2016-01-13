package urlshortener2015.navajowhite.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
 */

@ControllerAdvice
public class ExceptionHandlingController {

    /**
     * Manage NotFoundException redirecting to default 404 page
     * @param ex
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView notFoundException(NotFoundException ex) {
        ModelAndView model = new ModelAndView("404_NOT_FOUND");
        model.addObject("lastDate", ex.getText());
        return model;
    }

    /*@ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex) {
        ModelAndView model = new ModelAndView("404_NOT_FOUND");
        return model;

    }*/
}
