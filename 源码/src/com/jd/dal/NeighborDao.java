package com.jd.dal;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.*;

public class NeighborDao {
	private Context context;
	private SQLiteDatabase db;
	
	public NeighborDao(Context context)
	{
		this.context=context;
	}
	
	public ArrayList<Neighbor> GetAllNeighbors() throws Exception
	{
		
		ArrayList<Neighbor> lst=new ArrayList<Neighbor>();
		db=new DbHelper(context).getReadableDatabase();
		SQLiteCursor cur=(SQLiteCursor)db.query("Neighbors", null, null, null, null, null, null);
		
		while(cur.moveToNext()){
			Neighbor neighbor=new Neighbor();
			neighbor.setId(cur.getInt(0));
			neighbor.setDomainName(cur.getString(1));
			neighbor.setServerName(cur.getString(2));
			neighbor.setLoginId(cur.getString(3));
			neighbor.setPassword(cur.getString(4));
			neighbor.setAnonymous(cur.getInt(5)==1?true:false);
			neighbor.setNeighborName(cur.getString(6));
			lst.add(neighbor);
		}
		
		cur.close();
		db.close();
		return lst;
	}
	
	

	public Neighbor GetNeighborById(int aId) throws Exception
	{

		db=new DbHelper(context).getReadableDatabase();
		SQLiteCursor cur=(SQLiteCursor)db.query("Neighbors", null, "Id=?", new String[]{aId+""}, null, null, null);
		
		Neighbor neighbor=null;
		if(cur.moveToNext()){
			neighbor=new Neighbor();
			neighbor.setId(cur.getInt(0));
			neighbor.setDomainName(cur.getString(1));
			neighbor.setServerName(cur.getString(2));
			neighbor.setLoginId(cur.getString(3));
			neighbor.setPassword(cur.getString(4));
			neighbor.setAnonymous(cur.getInt(5)==1?true:false);
			neighbor.setNeighborName(cur.getString(6));
		}
		
		cur.close();
		db.close();
		
		return neighbor;
	}
	

	public int addNeighbor(Neighbor neighbor) throws Exception
	{
		db=new DbHelper(context).getWritableDatabase();
		ContentValues values=new ContentValues();
		
		values.put("DomainName", neighbor.getDomainName());
		values.put("ServerName", neighbor.getServerName());
		values.put("LoginId", neighbor.getLoginId());
		values.put("Password", neighbor.getPassword());
		values.put("Anonymous", neighbor.isAnonymous()?1:0);
		values.put("NeighborName", neighbor.getNeighborName());
	
		int res=(int)db.insert("Neighbors", "Id", values);
		db.close();
		return res;
	}
	
	public int deleteNeighbor(int id)
	{
		db=new DbHelper(context).getWritableDatabase();
		int res=(int)db.delete("Neighbors", " Id=?", new String[]{id+""});
		db.close();
		return res;
	}
	
	public int updateNeighbor(Neighbor neighbor) throws Exception
	{
		db=new DbHelper(context).getWritableDatabase();	
		ContentValues values=new ContentValues();
		values.put("DomainName", neighbor.getDomainName());
		values.put("ServerName", neighbor.getServerName());
		values.put("LoginId", neighbor.getLoginId());
		values.put("Password", neighbor.getPassword());
		values.put("Anonymous", neighbor.isAnonymous()?1:0);
		values.put("NeighborName", neighbor.getNeighborName());
		int res=db.update("Neighbors", values, " Id=?", new String[]{neighbor.getId()+""});
		db.close();
		return res;
		
	}
}
