package com.example.dtmusic;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MusicService {
	
	private static final File MUSIC_PATH = Environment.getExternalStorageDirectory(); //�ҵ�music��ŵ�·��,����ȡ�ⲿ�洢sd��·��
	public List<String> musicList ; //����ҵ�����MP3�ľ���·��
	public MediaPlayer player;  //�����ý�����
	public int songNum;     //��ǰ���ŵĸ�����List�е��±�
	public String songName; // ��ǰ���ŵĸ�����
	
	public MusicService(){
		musicList = new ArrayList<String>();
		player = new MediaPlayer();
		
		if(MUSIC_PATH.listFiles(new MusicFilter()).length > 0){ //listFilea�Ƿ�������·�����ļ�����list���ǲ�������
			for(File file : MUSIC_PATH.listFiles(new MusicFilter())){
			   musicList.add(file.getAbsolutePath());
			}
		}
	}
//	public MusicService(){
//		super();
//	}	
//	
//	public List<String> getMusiclist(){
//		musicList = new ArrayList<String>();
//		player = new MediaPlayer();
//		//������
//		if(MUSIC_PATH.listFiles(new MusicFilter()).length > 0){ //listFilea�Ƿ�������·�����ļ�����list���ǲ�������
//			
//			for(File file : MUSIC_PATH.listFiles(new MusicFilter())){
//			   musicList.add(file.getAbsolutePath());
//			}			
//		}			
//		return musicList;
//	}
	
	public void setPlayName(String dataSource){   //���ø���
		File file = new File(dataSource);
		String name = file.getName();
		int index = name.lastIndexOf(".");
		songName = name.substring(0, index);		
	}
	
	public void start(){
		try{
			player.reset();  //���ö�ý��
			String dataSource = musicList.get(songNum);  //�õ���ǰ���ֵĲ���·��
			setPlayName(dataSource);   //��ȡ����
			player.setDataSource(dataSource);  //Ϊ��ý�����ò���·��
			player.prepare(); //׼������
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {    //��ǰ���ֲ������ʱ������ʱ��
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					next();
				}
			});
		}catch(Exception e){
			Log.v("sss", e.getMessage());
		}
		
	}
	
	public void next(){
		songNum = songNum == musicList.size()-1 ? 0 : songNum + 1;
		start();
	}
	
	public void last(){
		songNum = songNum == 0 ? musicList.size() -1 : songNum - 1;
		start();
	}
	
	public void parse(){         //��ͣ�Ͳ���
		if(player.isPlaying()){
			player.pause();
		}
		else{
			player.start();
		}
	}
	
	public void stop(){        //ֹͣ����
		if(player.isPlaying()){
			player.stop();
		}
	}
	
	class MusicFilter implements FilenameFilter {
		public boolean accept(File dir , String name){
			return (name.endsWith(".mp3")); // ���ص�ǰĿ¼�ԡ�mp3Ϊ��β���ļ�
		}
	}

}
