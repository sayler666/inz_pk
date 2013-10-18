package com.sayler.inz.history;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sayler.inz.database.model.Road;

public class ImportRoadFromGPX implements I_ImportRoadToDB {

	private String _url;
	private StringBuilder _totalLine;

	public ImportRoadFromGPX() {
		super();
	}
	
	public ImportRoadFromGPX(String url) {
		super();
		this.setURL(url);
	}

	@Override
	public void setURL(String url) {
		this._url =url;
	}


	@Override
	public String getURL() {
		return this._url;
	}


	@Override
	public void read() {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(new FileInputStream(
					this._url)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		_totalLine = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
				_totalLine.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Road getRoad() {
		// TODO Auto-generated method stub
		return null;
	}


}
