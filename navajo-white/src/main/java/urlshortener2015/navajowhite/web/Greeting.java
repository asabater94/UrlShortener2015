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
public class Greeting {

    @Autowired
    protected ClickRepository clickRepository;

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name",name);
        return "greeting";
    }

    @RequestMapping(value = "/{hash:(?!link).*}" + "+")
    public String stats(@PathVariable String hash,
                        @RequestParam(value="name", required=false, defaultValue="0") String name, Model model) {
        ShortURL url = shortURLRepository.findByKey(hash);
        Long numeroClicks = clickRepository.clicksByHash(hash);
        model.addAttribute("numeroClicks",numeroClicks);
        model.addAttribute("fechaCreacion",url.getCreated());
        model.addAttribute("urlDestino",url.getTarget());
        //Te muestra la plantilla de greeting.html
        return "greeting";
    }

    @RequestMapping(value = "/{hash:(?!link).*}" + "+ADMIN")
    public String admin(@PathVariable String hash) {
        return "adminView";
    }

}
