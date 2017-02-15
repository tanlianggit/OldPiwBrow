package com.jd.dal;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;

public class HouseDao {
	private Context context;
	private SQLiteDatabase db;
	
	public HouseDao(Context context)
	{
		this.context=context;
	}
	
	public ArrayList<House> GetAllHouses() throws Exception
	{
		
		ArrayList<House> lst=new ArrayList<House>();
		db=new DbHelper(context).getReadableDatabase();
		SQLiteCursor cur=(SQLiteCursor)db.query("House", null, null, null, null, null, null);
		
		while(cur.moveToNext()){
			House house=new House();
			house.setId(cur.getInt(0));
			house.setPath(cur.getString(1));
			house.setTitle(cur.getString(2));
			house.setArea(cur.getString(3));
			lst.add(house);
		}
		
		cur.close();
		db.close();
		return lst;
	}
	
	

	public House GetHouseByPath(String path) throws Exception
	{

		db=new DbHelper(context).getReadableDatabase();
		SQLiteCursor cur=(SQLiteCursor)db.query("House", null, "Path=?", new String[]{path+""}, null, null, null);
		
		House house=null;
		if(cur.moveToNext()){
			house=new House();
			house.setId(cur.getInt(0));
			house.setPath(cur.getString(1));
			house.setTitle(cur.getString(2));
			house.setArea(cur.getString(3));
		}
		
		cur.close();
		db.close();
		
		return house;
	}
	

	public int addHouse(House house) throws Exception
	{
		db=new DbHelper(context).getWritableDatabase();
		ContentValues values=new ContentValues();
		
		//values.put("Id", house.getId());
		values.put("Path", house.getPath());
		values.put("Title", house.getTitle());
		values.put("Area", house.getArea());
		
		int res=(int)db.insert("House", "Id", values);
		db.close();
		return res;
	}
	
	public int deleteHouse(int id)
	{
		db=new DbHelper(context).getWritableDatabase();
		int res=(int)db.delete("House", " Id=?", new String[]{id+""});
		db.close();
		return res;
	}
	
	public int updateHouse(House house) throws Exception
	{
		db=new DbHelper(context).getWritableDatabase();	
		ContentValues values=new ContentValues();
		values.put("Id", house.getId());
		values.put("Path", house.getPath());
		values.put("Title", house.getTitle());
		values.put("Area", house.getArea());
		
		int res=db.update("House", values, " Id=?", new String[]{house.getId()+""});
		db.close();
		return res;
		
	}
}
