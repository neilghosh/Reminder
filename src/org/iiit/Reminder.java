package org.iiit;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable
public class Reminder {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
	@Persistent
	private Date when;
	@Persistent
	private String username;
	@Persistent
	private boolean deleted;
	@Persistent
	private String what;
	
	
	public String getWhat() {
		return what;
	}
	public void setWhat(String what) {
		this.what = what;
	}
	public Date getWhen() {
		return when;
	}
	public void setWhen(Date when) {
		this.when = when;
	}
	public String getUsername() {
		return username;
	}
	public Reminder(Date when, String username,String what , boolean deleted) {
		super();
		this.when = when;
		this.username = username;
		this.deleted = deleted;
		this.what = what;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Key getKey() {
		return key;
	} 

}
