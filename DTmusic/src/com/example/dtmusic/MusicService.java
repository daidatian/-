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
	
	private static final File MUSIC_PATH = Environment.getExternalStorageDirectory(); //找到music存放的路径,即获取外部存储sd的路径
	public List<String> musicList ; //存放找到所有MP3的绝对路径
	public MediaPlayer player;  //定义多媒体对象
	public int songNum;     //当前播放的歌曲在List中的下标
	public String songName; // 当前播放的歌曲名
	
	public MusicService(){
		musicList = new ArrayList<String>();
		player = new MediaPlayer();
		
		if(MUSIC_PATH.listFiles(new MusicFilter()).length > 0){ //listFilea是返回完整路径的文件名，list则是不完整的
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
//		//有问题
//		if(MUSIC_PATH.listFiles(new MusicFilter()).length > 0){ //listFilea是返回完整路径的文件名，list则是不完整的
//			
//			for(File file : MUSIC_PATH.listFiles(new MusicFilter())){
//			   musicList.add(file.getAbsolutePath());
//			}			
//		}			
//		return musicList;
//	}
	
	public void setPlayName(String dataSource){   //设置歌名
		File file = new File(dataSource);
		String name = file.getName();
		int index = name.lastIndexOf(".");
		songName = name.substring(0, index);		
	}
	
	public void start(){
		try{
			player.reset();  //重置多媒体
			String dataSource = musicList.get(songNum);  //得到当前音乐的播放路径
			setPlayName(dataSource);   //截取歌名
			player.setDataSource(dataSource);  //为多媒体设置播放路径
			player.prepare(); //准备播放
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {    //当前音乐播放完成时发生的时间
				
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
	
	public void parse(){         //暂停和播放
		if(player.isPlaying()){
			player.pause();
		}
		else{
			player.start();
		}
	}
	
	public void stop(){        //停止播放
		if(player.isPlaying()){
			player.stop();
		}
	}
	
	class MusicFilter implements FilenameFilter {
		public boolean accept(File dir , String name){
			return (name.endsWith(".mp3")); // 返回当前目录以。mp3为结尾的文件
		}
	}

}
