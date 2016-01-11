package urlshortener2015.navajowhite.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by alber on 27/12/2015.
 */
@ControllerAdvice
public class ExceptionHandlingController {

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
