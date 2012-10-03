package com.venefica.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

/**
 * Comment for an ad.
 * 
 * @author Sviatoslav Grebenchukov
 */
@Entity
@SequenceGenerator(name = "comment_gen", sequenceName = "comment_seq", allocationSize = 1)
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_gen")
	private Long id;

	@ManyToOne(optional = false)
	@ForeignKey(name = "comment_ad_fk")
	private Ad ad;

	@ManyToOne(optional = false)
	@ForeignKey(name = "comment_usr_fk")
	private User publisher;

	@Basic(optional = false)
	private String text;

	@Basic(optional = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	public Comment() {
		createdAt = new Date();
	}
	
	public Comment(Ad ad, User publisher, String text) {
		this.ad = ad;
		this.publisher = publisher;
		this.text = text;
		
		createdAt = new Date();
	}

	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}

	public User getPublisher() {
		return publisher;
	}

	public void setPublisher(User publisher) {
		this.publisher = publisher;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}		
}
