package com.sayler.inz.database.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


public abstract class AbstractObject implements Identifiable  {
	
	@DatabaseField(generatedId = true)
	protected long id;
	@DatabaseField()
	protected Date createdAt;
	
	
	public AbstractObject(){
		super();
	}
	
	public AbstractObject(long id, Date createdAt) {
		super();
		this.id = id;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


}
