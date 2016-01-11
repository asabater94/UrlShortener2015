package urlshortener2015.navajowhite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import urlshortener2015.navajowhite.domain.Info;
import urlshortener2015.navajowhite.domain.ShortURL;
import urlshortener2015.navajowhite.domain.ShortURLinfo;
import urlshortener2015.navajowhite.repository.ClickRepository;
import urlshortener2015.navajowhite.repository.ShortURLRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by coke on 23/11/2015.
 */
@Controller
public class ControllerStats {

    @Autowired
    protected ClickRepository clickRepository;

    @Autowired
    protected ShortURLRepository shortURLRepository;

    /**
     * Controller that returns the info about a short URL with identifier = <hash> in JSON format.
     * @param hash identifier of short URL
     * @param name -
     * @return returns: target, date created and number of clicks of the short URL with identifier = <hash>
     */
    //produces={ "application/json"} // MediaType.APPLICATION_JSON_VALUE
    @RequestMapping(value = "/{hash:(?!link).*}" + "+" , method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<?> stats2(@PathVariable String hash,
                                    @RequestParam(value="name", required=false, defaultValue="0") String name) {
        ShortURL url = shortURLRepository.findByKey(hash);
        Long numeroClicks = clickRepository.clicksByHash(hash);
        ShortURLinfo info = new ShortURLinfo(url.getTarget(),url.getCreated(),numeroClicks);
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    /**
     * Controller that returns the info about a short URL with identifier = <hash> in HTML format.
     * @param hash identifier of short URL
     * @param name -
     * @return returns: target, date created and number of clicks of the short URL with identifier = <hash>
     */
    @RequestMapping(value = "/{hash:(?!link).*}" + "+", method = RequestMethod.GET , produces ="text/html" )
    public Object stats(@PathVariable String hash,
                        @RequestParam(value="name", required=false, defaultValue="0") String name, Model model) {
        ShortURL url = shortURLRepository.findByKey(hash);
        Long numeroClicks = clickRepository.clicksByHash(hash);
        model.addAttribute("numeroClicks",numeroClicks);
        model.addAttribute("fechaCreacion",url.getCreated());
        model.addAttribute("urlDestino",url.getTarget());
        return "statsHTML";
    }

    /**
     * Controller that returns the info about a short URL with identifier = <hash>, filtered by time interval
     * and cities/countries, in HTML format.
     * @param hash identifier of short URL
     * @param desde starting date of time interval
     * @param hasta ending date of time interval
     * @param country -
     * @param city -
     * @param type type of info (countries or cities)
     * @return return number of clicks in a time interval, shortened by cities or countries, in HTML format
     */
    @RequestMapping(value = "/{hash:(?!link).*}" + "+ADMIN")
    public ModelAndView admin(@PathVariable String hash,
                              @RequestParam(value = "desde", required = false, defaultValue="") String desde,
                              @RequestParam(value = "hasta", required = false,defaultValue="") String hasta,
                              @RequestParam(value = "country", required = false) String country,
                              @RequestParam(value = "city", required = false) String city,
                              @RequestParam(value = "type", required = false, defaultValue="countries") String type) {

        ModelAndView model = new ModelAndView("adminView");
        model.addObject("type",type);
        model.addObject("hash",hash);
        model.addObject("desde",desde);
        model.addObject("hasta",hasta);

        List<String> sites = null;
        List<Info> data = null;

        if ((desde == null || desde.equalsIgnoreCase("")) && (hasta == null  || hasta.equalsIgnoreCase(""))){
            data = rellenarDatos(type,hash,null,null);
        }

        if (!desde.equalsIgnoreCase("") && (hasta == null  || hasta.equalsIgnoreCase(""))){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = sourceFormat.parse(desde);
            } catch (ParseException e) {
                model.addObject("error", "Error en el formato de la fecha");
                return model;
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            data = rellenarDatos(type,hash,sqlDate,null);
        }

        if (!hasta.equalsIgnoreCase("") && (desde == null  || desde.equalsIgnoreCase(""))){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = sourceFormat.parse(hasta);
            } catch (ParseException e) {
                model.addObject("error", "Error en el formato de la fecha");
                return model;
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            data = rellenarDatos(type,hash,null,sqlDate);
        }

        if (!hasta.equalsIgnoreCase("") && !desde.equalsIgnoreCase("")){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date hastaDate = null;
            Date desdeDate = null;
            try {
                hastaDate = sourceFormat.parse(hasta);
                desdeDate = sourceFormat.parse(desde);
            } catch (ParseException e) {
                model.addObject("error", "Error en el formato de la fecha");
                return model;
            }
            java.sql.Date sqlHastaDate = new java.sql.Date(hastaDate.getTime());
            java.sql.Date sqlDesdeDate = new java.sql.Date(desdeDate.getTime());
            data = rellenarDatos(type,hash,sqlDesdeDate,sqlHastaDate);
        }

        model.addObject("lista", data);
        return model;
    }

    /**
     * Controller that returns the info about a short URL with identifier = <hash>, filtered by time interval
     * and cities/countries, in JSON format.
     * @param hash identifier of short URL
     * @param desde starting date of time interval
     * @param hasta ending date of time interval
     * @param type type of info (countries or cities)
     * @return return number of clicks in a time interval, shortened by cities or countries, in JSON format
     */
    @RequestMapping(value = "/{hash:(?!link).*}" + "+ADMIN" , produces="application/json")
    public ResponseEntity<?> adminJSON(@PathVariable String hash,
                              @RequestParam(value = "desde", required = false, defaultValue="") String desde,
                              @RequestParam(value = "hasta", required = false,defaultValue="") String hasta,
                              @RequestParam(value = "type", required = false, defaultValue="countries") String type) {

        List<String> sites = null;
        List<Info> data = null;

        if ((desde == null || desde.equalsIgnoreCase("")) && (hasta == null  || hasta.equalsIgnoreCase(""))){
            data = rellenarDatos(type,hash,null,null);
        }

        if (!desde.equalsIgnoreCase("") && (hasta == null  || hasta.equalsIgnoreCase(""))){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = sourceFormat.parse(desde);
            } catch (ParseException e) {
                return new ResponseEntity<>("Error en el formato de la fecha", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            data = rellenarDatos(type,hash,sqlDate,null);
        }

        if (!hasta.equalsIgnoreCase("") && (desde == null  || desde.equalsIgnoreCase(""))){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = sourceFormat.parse(hasta);
            } catch (ParseException e) {
                return new ResponseEntity<>("Error en el formato de la fecha", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            data = rellenarDatos(type,hash,null,sqlDate);
        }

        if (!hasta.equalsIgnoreCase("") && !desde.equalsIgnoreCase("")){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date hastaDate = null;
            Date desdeDate = null;
            try {
                hastaDate = sourceFormat.parse(hasta);
                desdeDate = sourceFormat.parse(desde);
            } catch (ParseException e) {
                return new ResponseEntity<>("Error en el formato de la fecha", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            java.sql.Date sqlHastaDate = new java.sql.Date(hastaDate.getTime());
            java.sql.Date sqlDesdeDate = new java.sql.Date(desdeDate.getTime());
            data = rellenarDatos(type,hash,sqlDesdeDate,sqlHastaDate);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * Controller that returns a List of the info about a short URL with identifier = <hash>, filtered by time interval
     * and cities/countries, in HTML format.
     * @param hash identifier of short URL
     * @param desde starting date of time interval
     * @param hasta ending date of time interval
     * @param type type of info (countries or cities)
     * @return return a List of Info with number of clicks in a time interval, shortened by cities or countries, in HTML format
     */
    private List<Info> rellenarDatos (String type, String hash, java.sql.Date desde, java.sql.Date hasta){

        List<Info> data = null;
        List<String> sites = null;

        if (desde==null && hasta==null){
            if(type.equalsIgnoreCase("countries")){
                sites = clickRepository.getCountries();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCountry(hash,s))));
                }
            }
            else{
                sites = clickRepository.getCities();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCity(hash,s))));
                }
            }
        }

        if(desde!=null && hasta==null){
            if(type.equalsIgnoreCase("countries")){
                sites = clickRepository.getCountries();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCountryAndDesde(hash,s,desde))));
                }
            }
            else{
                sites = clickRepository.getCities();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCityAndDesde(hash,s,desde))));
                }
            }
        }

        if(desde==null && hasta !=null){
            if(type.equalsIgnoreCase("countries")){
                sites = clickRepository.getCountries();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCountryAndHasta(hash,s,hasta))));
                }
            }
            else{
                sites = clickRepository.getCities();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCityAndHasta(hash,s,hasta))));
                }
            }
        }

        if(desde!=null & hasta!=null){
            if(type.equalsIgnoreCase("countries")){
                sites = clickRepository.getCountries();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCountryAndDesdeHasta(hash,s,desde,hasta))));
                }
            }
            else{
                sites = clickRepository.getCities();
                data = new ArrayList<>();
                for(String s : sites){
                    data.add(new Info (s,String.valueOf(clickRepository.clicksByHashAndCityAndDesdeHasta(hash,s,desde,hasta))));
                }
            }
        }

        return data;
    }

    //TODO Finish the controller in order to return the location of every click
    @RequestMapping(value = "/{hash:(?!link).*}" + "+ADMINmap")
    public ModelAndView adminMap(@PathVariable String hash,
                              @RequestParam(value = "desde", required = false, defaultValue="") String desde,
                              @RequestParam(value = "hasta", required = false,defaultValue="") String hasta,
                              @RequestParam(value = "country", required = false) String country,
                              @RequestParam(value = "city", required = false) String city,
                              @RequestParam(value = "type", required = false, defaultValue="countries") String type) {

        ModelAndView model = new ModelAndView("adminViewMap");
        model.addObject("type",type);
        model.addObject("hash",hash);
        model.addObject("desde",desde);
        model.addObject("hasta",hasta);

        List<String> sites = null;
        List<Info> data = null;

        if ((desde == null || desde.equalsIgnoreCase("")) && (hasta == null  || hasta.equalsIgnoreCase(""))){
            data = rellenarDatos(type,hash,null,null);
        }

        if (!desde.equalsIgnoreCase("") && (hasta == null  || hasta.equalsIgnoreCase(""))){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = sourceFormat.parse(desde);
            } catch (ParseException e) {
                model.addObject("error", "Error en el formato de la fecha");
                return model;
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            data = rellenarDatos(type,hash,sqlDate,null);
        }

        if (!hasta.equalsIgnoreCase("") && (desde == null  || desde.equalsIgnoreCase(""))){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = sourceFormat.parse(hasta);
            } catch (ParseException e) {
                model.addObject("error", "Error en el formato de la fecha");
                return model;
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            data = rellenarDatos(type,hash,null,sqlDate);
        }

        if (!hasta.equalsIgnoreCase("") && !desde.equalsIgnoreCase("")){
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date hastaDate = null;
            Date desdeDate = null;
            try {
                hastaDate = sourceFormat.parse(hasta);
                desdeDate = sourceFormat.parse(desde);
            } catch (ParseException e) {
                model.addObject("error", "Error en el formato de la fecha");
                return model;
            }
            java.sql.Date sqlHastaDate = new java.sql.Date(hastaDate.getTime());
            java.sql.Date sqlDesdeDate = new java.sql.Date(desdeDate.getTime());
            data = rellenarDatos(type,hash,sqlDesdeDate,sqlHastaDate);
        }

        model.addObject("lista", data);
        return model;
    }

}
