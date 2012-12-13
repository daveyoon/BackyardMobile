package com.backyard.backyard;

public class Report {
	int _id;
	String _sector;
	String _description;
	public Report(){}
	// constructor
    public Report(int id, String sector, String description){
        this._id = id;
        this._sector = sector;
        this._description = description;
    }
 
    // constructor
    public Report(String sector, String description){
        this._sector = sector;
        this._description = description;
    }

}
