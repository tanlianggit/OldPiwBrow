package com.jd.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME="Jd.db";
	private static final int DATABASE_VERSION=5;
	
	public DbHelper(Context context)
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="create table Neighbors(Id integer primary key autoincrement,DomainName varchar(256),ServerName varchar(256),LoginId varchar(256),Password varchar(256) null,Anonymous integer,NeighborName varchar(256))";
		db.execSQL(sql);
		sql="create table Spell(fid integer primary key autoincrement,fName varchar(1024),playMode integer,gridCount integer,shortTime integer,minClick integer)";
		db.execSQL(sql);
		sql="create table House(Id integer primary key autoincrement,Path varchar(1024),Title varchar(1024),Area varchar(50))";
		db.execSQL(sql);

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
