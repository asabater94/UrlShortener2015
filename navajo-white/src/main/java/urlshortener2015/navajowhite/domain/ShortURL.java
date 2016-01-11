package urlshortener2015.navajowhite.domain;

import java.net.URI;
import java.sql.Date;
import java.sql.Timestamp;

public class ShortURL implements Comparable {

	private String hash;
	private String target;
	private URI uri;
	private String sponsor;
	private Date created;
	private String owner;
	private Integer mode;
	private Boolean safe;
	private String ip;
	private String country;
	private int active;
	private int update_status;		// 0 -> pending to update; 1 -> updating
	private Timestamp last_change;
	private Timestamp lastReachable;


	public ShortURL(String hash, String target, URI uri, String sponsor,
					Date created, String owner, Integer mode, Boolean safe, String ip,
					String country, Timestamp last_change, int active, int update_status, Timestamp lastReachable) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.sponsor = sponsor;
		this.created = created;
		this.owner = owner;
		this.mode = mode;
		this.safe = safe;
		this.ip = ip;
		this.country = country;
		this.last_change = last_change;
		this.active = active;
		this.update_status = update_status;
		this.lastReachable = lastReachable;
	}

	public ShortURL(String hash, String target, URI uri, String sponsor,
					Date created, String owner, Integer mode, Boolean safe, String ip,
					String country, int active) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.sponsor = sponsor;
		this.created = created;
		this.owner = owner;
		this.mode = mode;
		this.safe = safe;
		this.ip = ip;
		this.country = country;
		this.active = active;
	}

	public ShortURL() {
	}

	public String getHash() {
		return hash;
	}

	public String getTarget() {
		return target;
	}

	public URI getUri() {
		return uri;
	}

	public Date getCreated() {
		return created;
	}

	public String getOwner() {
		return owner;
	}

	public Integer getMode() {
		return mode;
	}

	public String getSponsor() {
		return sponsor;
	}

	public Boolean getSafe() {
		return safe;
	}

	public String getIP() {
		return ip;
	}

	public String getCountry() {
		return country;
	}

	public Timestamp getLastChange() { return last_change; }

	public int getActive() { return active; }

	public int getUpdate_status() { return update_status; }

	public Timestamp getLastReachable() { return lastReachable; }

	public void setActive(int active) { this.active = active; }

	public void setLastChange(Timestamp last_change) { this.last_change = last_change; }

	public void setUpdate_status(int update_status) { this.update_status = update_status; }

	public void setLastReachable(Timestamp lastReachable) { this.lastReachable = lastReachable; }


	/**
	 * Return the priority for the PriorityBlockingQueue
	 * The less value is the head of the queue
	 * The new URLs will be in the head
     */
	@Override
	public int compareTo(Object o) {
		ShortURL newUrl = (ShortURL) o;

		if (newUrl.update_status == this.update_status) {
			return 1;
		}
		else if (newUrl.update_status < this.update_status){
			return -1;
		}
		else {
			return 1;
		}

		//return (-update_status);
	}
}
