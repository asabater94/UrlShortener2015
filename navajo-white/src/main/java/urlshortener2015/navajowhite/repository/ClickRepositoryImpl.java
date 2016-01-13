package urlshortener2015.navajowhite.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import urlshortener2015.navajowhite.domain.Click;


import java.sql.*;
import java.util.List;

/**
 * Alberto Sabater, 546297
 * Jorge Martinez, 571735
 * Adrian Susinos, 650220
 */

@Repository
public class ClickRepositoryImpl implements ClickRepository {

	private static final Logger log = LoggerFactory
			.getLogger(ClickRepositoryImpl.class);

	private static final RowMapper<Click> rowMapper = new RowMapper<Click>() {
		@Override
		public Click mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Click(rs.getLong("id"), rs.getString("hash"),
					rs.getDate("created"), rs.getString("referrer"),
					rs.getString("browser"), rs.getString("platform"),
					rs.getString("ip"), rs.getString("country"), rs.getString("city"));
		}
	};

	@Autowired
	protected JdbcTemplate jdbc;

	public ClickRepositoryImpl() {
	}

	public ClickRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public List<Click> findByHash(String hash) {
		try {
			return jdbc.query("SELECT * FROM click WHERE hash=?",
					new Object[] { hash }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for hash " + hash, e);
			return null;
		}
	}

	@Override
	public Click save(final Click cl) {
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			jdbc.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection conn)
						throws SQLException {
					PreparedStatement ps = conn
							.prepareStatement(
									"INSERT INTO CLICK VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
									Statement.RETURN_GENERATED_KEYS);
					ps.setNull(1, Types.BIGINT);
					ps.setString(2, cl.getHash());
					ps.setDate(3, cl.getCreated());
					ps.setString(4, cl.getReferrer());
					ps.setString(5, cl.getBrowser());
					ps.setString(6, cl.getPlatform());
					ps.setString(7, cl.getIp());
					ps.setString(8, cl.getCountry());
					ps.setString(9, cl.getCity());
					return ps;
				}
			}, holder);
			new DirectFieldAccessor(cl).setPropertyValue("id", holder.getKey()
					.longValue());
		} catch (DuplicateKeyException e) {
			log.debug("When insert for click with id " + cl.getId(), e);
			return cl;
		} catch (Exception e) {
			log.debug("When insert a click", e);
			return null;
		}
		return cl;
	}

	@Override
	public void update(Click cl) {
		log.info("ID2: "+cl.getId()+"navegador: "+cl.getBrowser()+" SO: "+cl.getPlatform()+" Date:"+cl.getCreated());
		try {
			jdbc.update(
					"update click set hash=?, created=?, referrer=?, browser=?, platform=?, ip=?, country=?, city=? where id=?",
					cl.getHash(), cl.getCreated(), cl.getReferrer(),
					cl.getBrowser(), cl.getPlatform(), cl.getIp(),
					cl.getCountry(), cl.getCity(), cl.getId());
			
		} catch (Exception e) {
			log.info("When update for id " + cl.getId(), e);
		}
	}

	@Override
	public void delete(Long id) {
		try {
			jdbc.update("delete from click where id=?", id);
		} catch (Exception e) {
			log.debug("When delete for id " + id, e);
		}
	}

	@Override
	public void deleteAll() {
		try {
			jdbc.update("delete from click");
		} catch (Exception e) {
			log.debug("When delete all", e);
		}
	}

	@Override
	public Long count() {
		try {
			return jdbc
					.queryForObject("select count(*) from click", Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}

	@Override
	public List<Click> list(Long limit, Long offset) {
		try {
			return jdbc.query("SELECT * FROM click LIMIT ? OFFSET ?",
					new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for limit " + limit + " and offset "
					+ offset, e);
			return null;
		}
	}

	@Override
	public Long clicksByHash(String hash) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ?", new Object[]{hash}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return -1L;
	}

	@Override
	public Long clicksByHashAndCountry(String hash, String country) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and country = ?", new Object[]{hash,country}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

	@Override
	public List<String> getCountries() {
		try {
			return jdbc.queryForList("SELECT DISTINCT country from click", String.class);
		} catch (Exception e) {
			log.debug("When DISTINCT country from click ", e);
			return null;
		}
	}

	@Override
	public Long clicksByHashAndCountryAndDesde(String hash, String country, Date desde) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and country = ? and created > ?", new Object[]{hash,country,desde}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

	@Override
	public Long clicksByHashAndCountryAndHasta(String hash, String country, Date hasta) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and country = ? and created < ?", new Object[]{hash,country,hasta}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

	@Override
	public Long clicksByHashAndCountryAndDesdeHasta(String hash, String country, Date desde, Date hasta) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and country = ? and created > ? and created < ?", new Object[]{hash,country,desde,hasta}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

	@Override
	public Long clicksByHashAndCity(String hash, String city) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and city = ?", new Object[]{hash,city}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

	@Override
	public List<String> getCities() {
		try {
			return jdbc.queryForList("SELECT DISTINCT city from click", String.class);
		} catch (Exception e) {
			log.debug("When DISTINCT country from click ", e);
			return null;
		}
	}

	@Override
	public Long clicksByHashAndCityAndDesde(String hash, String city, Date desde) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and city = ? and created > ?", new Object[]{hash,city,desde}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

	@Override
	public Long clicksByHashAndCityAndHasta(String hash, String city, Date hasta) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and city = ? and created < ?", new Object[]{hash,city,hasta}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

	@Override
	public Long clicksByHashAndCityAndDesdeHasta(String hash, String city, Date desde, Date hasta) {
		try {
			return jdbc
					.queryForObject("select count(*) from click where hash = ? and city = ? and created > ? and created < ?", new Object[]{hash,city,desde,hasta}, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash "+hash, e);
		}
		return null;
	}

}
