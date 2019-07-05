package com.atolcd.document.metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.atolcd.document.metadata.CustomProperty.PropertyType;
import com.sun.istack.NotNull;

public class Metadata {
	
	private String title;
	private String subject;
	private String comment;
	private String author;
	private String creator;
	private Date creationDate;
	private List<String> keywords;
	private HashMap<String, CustomProperty> customProperties;
	
	public Metadata() {
		this.title = null;
		this.subject = null;
		this.comment = null;
		this.author = null;
		this.creator = null;
		this.creationDate = new Date();
		this.keywords = new ArrayList<String>();
		this.customProperties = new HashMap<String, CustomProperty>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public List<String> getKeywords() {
		return keywords;
	}
	
	public void addKeyword(String keyword){
		this.keywords.add(keyword);
	}

	public List<CustomProperty> getCustomProperties() {
		return new ArrayList<CustomProperty>(customProperties.values());
	}
	
	public void getCustomProperty(String name){
		this.customProperties.get(name);
	}
	
	public void addCustomProperty(String name, long value){
		this.customProperties.put(
			name,
			new CustomProperty(
				name,
				PropertyType.LONG,
				value
			)
		);
	}
	
	public void addCustomProperty(String name, double value){
		this.customProperties.put(
			name,
			new CustomProperty(
				name,
				PropertyType.DOUBLE,
				value
			)
		);
	}
	
	public void addCustomProperty(String name, String value){
		this.customProperties.put(
			name,
			new CustomProperty(
				name,
				PropertyType.STRING,
				value
			)
		);
	}
	
	public void addCustomProperty(String name, boolean value){
		this.customProperties.put(
			name,
			new CustomProperty(
				name,
				PropertyType.BOOLEAN,
				value
			)
		);
	}
	
	public void addCustomProperty(String name, @NotNull Date value){
		this.customProperties.put(
			name,
			new CustomProperty(
				name,
				CustomProperty.PropertyType.DATE,
				value
			)
		);
	}

}
