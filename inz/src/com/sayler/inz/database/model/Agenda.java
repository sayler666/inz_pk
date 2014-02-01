package com.sayler.inz.database.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Agenda extends AbstractObject {

	
	@DatabaseField()
	protected Date dateOfEvent;

	public Agenda() {
		super();

	}
	public Agenda(Date d) {
		this.dateOfEvent = d;
		
	}
	public Date getDateOfEvent() {
		return dateOfEvent;
	}

	public void setDateOfEvent(Date dateOfEvent) {
		this.dateOfEvent = dateOfEvent;
	}

	
}
