package com.example.tanmu;

public class tanmu {

	private String tanmuText;   //��Ļ
	private String songName;  //����
	private int position;  //��ǰ����ʱ��
	
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
