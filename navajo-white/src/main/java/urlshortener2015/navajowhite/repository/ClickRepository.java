package urlshortener2015.navajowhite.repository;


import urlshortener2015.navajowhite.domain.Click;

import java.sql.Date;
import java.util.List;

public interface ClickRepository {

	List<Click> findByHash(String hash);

	Long clicksByHash(String hash);

	Click save(Click cl);

	void update(Click cl);

	void delete(Long id);

	void deleteAll();

	Long count();

	List<Click> list(Long limit, Long offset);

	Long clicksByHashAndCountry(String hash, String country);

	List<String> getCountries();

	Long clicksByHashAndCountryAndDesde(String hash, String country, Date desde);

	Long clicksByHashAndCountryAndHasta(String hash, String country, Date hasta);

	Long clicksByHashAndCountryAndDesdeHasta(String hash, String country, Date desde, Date hasta);

	List<String> getCities();

	Long clicksByHashAndCityAndDesde(String hash, String city, Date desde);

	Long clicksByHashAndCityAndHasta(String hash, String city, Date hasta);

	Long clicksByHashAndCityAndDesdeHasta(String hash, String city, Date desde, Date hasta);

	Long clicksByHashAndCity(String hash, String city);
}
