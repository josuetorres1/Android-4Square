/**
 * 
 */
package com.josue.square;

/**
 * @author jtorres
 *
 */
import java.util.ArrayList;

public class Venue {

	/**
	 * 
	 */
	private String id;

	private String name;
	
	private String snipped;

	private double latitude;
	
	private double longitude;

	private ArrayList<Category> categories;

	private boolean verified;

	private Statistics stats;

	private HereNow beenHere;

	private HereNow hereNow;

	private long createdAt;

	private String timeZone;

	private String canonicalUrl;

	private String shortUrl;

	private boolean dislike;

	private String url;

	private boolean like;

	public String getTimeZone() {
		return timeZone;
	}

	public String getCanonicalUrl() {
		return canonicalUrl;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public boolean isDislike() {
		return dislike;
	}

	public HereNow getHereNow() {
		return hereNow;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getSnipped() {
		return snipped;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public boolean isVerified() {
		return verified;
	}

	public Statistics getStats() {
		return stats;
	}

	public HereNow getBeenHere() {
		return beenHere;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitud(double longitud) {
		this.longitude = longitud;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSnipped(String snipped) {
		this.snipped = snipped;
	}
	
}
