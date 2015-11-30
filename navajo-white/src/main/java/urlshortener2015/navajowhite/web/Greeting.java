package urlshortener2015.navajowhite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import urlshortener2015.common.repository.ClickRepository;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by coke on 23/11/2015.
 */
@Controller
public class Greeting {

    @Autowired
    protected ClickRepository clickRepository;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name",name);
        return "greeting";
    }

    @RequestMapping(value = "/{hash:(?!link).*}" + "+")
    public String stats(@PathVariable String hash,
                        @RequestParam(value="name", required=false, defaultValue="0") String name, Model model) {
        Long numeroClicks = clickRepository.clicksByHash(hash);
        model.addAttribute("numeroClicks",numeroClicks);
        //Te muestra la plantilla de greeting.html
        return "greeting";
    }
}
