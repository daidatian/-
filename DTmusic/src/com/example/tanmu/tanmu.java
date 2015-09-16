package com.example.tanmu;

public class tanmu {

	private String tanmuText;   //弹幕
	private String songName;  //歌名
	private int position;  //当前播放时间
	
	public String gettanmuText(){
		return tanmuText;
	}
	public void settanmuText(String tanmuText){
		this.tanmuText = tanmuText;
	}
	public String getsongName(){
		return songName;
	}
	public void setsongName(String songName){
		this.songName = songName;
	}
	public int getposition(){
		return position;
	}
	public void setposition(int position){
		this.position = position;
	}
	public void settanmu(tanmu tanmu){
		this.tanmuText = tanmu.tanmuText;
		this.songName = tanmu.songName;
		this.position = tanmu.position;
	}
	public tanmu(String content, String songName, int position){
		super();
		this.tanmuText = content;
		this.songName = songName;
		this.position = position;
	}
	
	
	public tanmu(){
		super();
	}

}
