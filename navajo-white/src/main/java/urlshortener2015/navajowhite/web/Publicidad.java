package urlshortener2015.navajowhite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.repository.ClickRepository;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by coke on 23/11/2015.
 */
@Controller
public class Publicidad {

    @Autowired
    protected ShortURLRepository shortURLRepository;

    //mirar si esto hace falta¿?¿?
    @RequestMapping("/publicidad")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name",name);
        return "publicidad";
    }

    @RequestMapping(value = "/{hash:(?!link).*}" + "++")
    public String stats(@PathVariable String hash,
                        @RequestParam(value="name", required=false, defaultValue="0") String name, Model model) {
       // ShortURL shortURL = shortURLRepository.findByKey(hash);
        model.addAttribute("hash",hash);
        //Te muestra la plantilla de publicidad.html
        return "publicidad";
    }
}
