package com.jd.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.*;

public class SpellDao {
	Context context;
	private SQLiteDatabase db;
	
	public SpellDao(Context context)
	{
		this.context=context;
	}
	
	public Spell getSpellByfName(String fName,int playMode,int gridCount)
	{
		db=new DbHelper(context).getReadableDatabase();
		SQLiteCursor cur=(SQLiteCursor)db.query("Spell", null, "fName=? and playMode=? and gridCount=?", new String[]{fName,playMode+"",gridCount+""}, null, null, null);
		Spell spell=null;
		if(cur.moveToFirst())
		{
			spell=new Spell();
			spell.setsId(cur.getInt(0));
			spell.setfName(cur.getString(1));
			spell.setGridCount(gridCount);
			spell.setPlayMode(playMode);
			spell.setShortTime(cur.getInt(4));
			spell.setMinClick(cur.getInt(5));
			
		}
		cur.close();
		db.close();
		
		return spell;
	}
	
	public void addSpell(Spell spell)
	{
		Spell sp=getSpellByfName(spell.getfName(),spell.getPlayMode(),spell.getGridCount());
		db=new DbHelper(context).getWritableDatabase();
		ContentValues val=new ContentValues();
		val.put("fName", spell.getfName());
		val.put("playMode", spell.getPlayMode());
		val.put("gridCount", spell.getGridCount());
		val.put("shortTime", spell.getShortTime());
		val.put("minClick", spell.getMinClick());
		
		if(sp==null)
		{
			db.insert("Spell", "fId", val);
		}else
		{
			db.update("Spell", val, "fId=?", new String[]{spell.getsId()+""});
		}
		
		db.close();
	}
	
	public void deleteSpellByFname(String fName,int playMode,int gridCount)
	{
		db=new DbHelper(context).getWritableDatabase();
		db.delete("Spell", "fName=? and playMode=? and gridCount=?", new String[]{fName,playMode+"",gridCount+""});
		db.close();
	}
	
	
	public void deleteSpell()
	{
		db=new DbHelper(context).getWritableDatabase();
		db.delete("Spell",null,null);
		db.close();
	}
}
