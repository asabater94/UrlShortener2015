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
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
 */

@Controller
public class Publicidad {

    @Autowired
    protected ShortURLRepository shortURLRepository;

    /**
     * Method that returns a html (publicidad.html) and allows to use the attributes target and hash in it.
     * @param hash identifier of the short URL
     * @param name -
     * @param model -
     */
    @RequestMapping(value = "/{hash:(?!link).*}" + "++")
    public String publi(@PathVariable String hash,
                        @RequestParam(value="name", required=false, defaultValue="0") String name, Model model) {

        ShortURL shortURL = shortURLRepository.findByKey(hash);
        model.addAttribute("target", shortURL.getTarget().toString());
        model.addAttribute("hash", hash);

        return "publicidad";

    }
}
