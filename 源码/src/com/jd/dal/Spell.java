package com.jd.dal;

public class Spell {
	private int sId;
	private String fName;
	private int playMode;//0:任意模式，1相邻模式
	private int gridCount;//图片格数
	public int getPlayMode() {
		return playMode;
	}
	public void setPlayMode(int playMode) {
		this.playMode = playMode;
	}
	public int getGridCount() {
		return gridCount;
	}
	public void setGridCount(int gridCount) {
		this.gridCount = gridCount;
	}
	private int shortTime;
	private int minClick;
	public int getsId() {
		return sId;
	}
	public void setsId(int sId) {
		this.sId = sId;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public int getShortTime() {
		return shortTime;
	}
	public void setShortTime(int shortTime) {
		this.shortTime = shortTime;
	}
	public int getMinClick() {
		return minClick;
	}
	public void setMinClick(int minClick) {
		this.minClick = minClick;
	}
	
}
