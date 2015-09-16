package com.example.dtmusic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	
	private ImageButton btnStart, btnStop, btnNext, btnLast, btnlrc;
	private TextView txtInfo, aa;
	private ListView listView;
	private SeekBar seekBar;
	public static MusicService musicService;
	public static MusicHandler musicHandler;    //处理进度条事件
	private MusicThread musicThread;    //自动改变进度条的线程
	private boolean autoChange, manulChange;  //判断进度条是自动改变还是手动改变
	private boolean startThread;      //是否开始自动改变进度条的线程
	private boolean idPause;   //是否自动恢复还是重新播放
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	
	public void init(){
		
		aa = (TextView) findViewById(R.id.local_textView1);
		musicService = new MusicService();
		musicHandler = new MusicHandler();
		musicThread = new MusicThread();
		btnStart = (ImageButton) findViewById(R.id.btn_start);
		btnStop = (ImageButton) findViewById(R.id.btn_stop);
		btnLast = (ImageButton) findViewById(R.id.btn_last);	
		btnNext = (ImageButton) findViewById(R.id.btn_next);
		listView = (ListView) findViewById(R.id.listView);
		txtInfo =  (TextView) findViewById(R.id.txtInfo);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		btnlrc = (ImageButton) findViewById(R.id.local_image2);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnLast.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnlrc.setOnClickListener(this);
		setListViewAdapter();
		//在listview中按某首歌讲自动播放该歌曲
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(musicService.player.isPlaying()){
					musicService.stop();
				}
				musicService.songNum = arg2;       //arg2是视图在一览中的位置索引
				startPlayer();				
			}
		});
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {   //停止拖动时
				// TODO Auto-generated method stub
				int progress = seekBar.getProgress();
				if(!autoChange && manulChange){
					int musicMax = musicService.player.getDuration();  //得到歌曲的最长秒数
					int seekBarMax = seekBar.getMax();    //进度条的总长度
					musicService.player.seekTo(musicMax * progress /seekBarMax);   //跳到该曲该秒
					musicService.parse();
					autoChange = true;
					manulChange = false;
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { //按下拖动
				// TODO Auto-generated method stub
				if(musicService.player.isPlaying()){
					txtInfo.setText("正在缓冲···");
					musicService.parse();
					autoChange = false;
					manulChange = true;
				}
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {        //进度条发生改变时执行
				// TODO Auto-generated method stub
				
			}
		});
		new Thread(musicThread).start();
       
		
	}
	
	
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_start:			
			startPlayer();
			break;
		case R.id.btn_next:
			musicService.next();		
			break;
		case R.id.btn_last:
			musicService.last();
			break;
		case R.id.btn_stop:
			autoChange = false;
			musicService.stop();
			btnStart.setImageResource(R.drawable.isplaying);   
			break;
		case R.id.local_image2:
			Intent intent1 = new Intent(MainActivity.this,PlayActivity.class);
			startActivity(intent1);
			break;		
		default:
			break;
		}
	}
	
	
	private void startPlayer(){
		if(musicService.player.isPlaying()){
			musicService.parse();
			btnStart.setImageResource(R.drawable.isplaying);   
		}
		else{
			musicService.start();
			autoChange = true;
			btnStart.setImageResource(R.drawable.pause1);      //改成暂停图标
		}		
	}
	
	private void setListViewAdapter(){     //播放列表listview的填充方法
		List<Map<String,Object>> date = new ArrayList<Map<String,Object>>();		
		for(String path: musicService.musicList){
			Map<String,Object> map = new HashMap<String, Object>();
			File file = new File(path);
			map.put("filename", file.getName());
			date.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, date,    //适配器
				android.R.layout.simple_list_item_1, new String[] {"filename"},
				new int[]{android.R.id.text1});              //android.R.id.text1
		listView.setAdapter(adapter);		
	}
	
	
	class MusicHandler extends Handler {
		public MusicHandler() {
		}
		public void handleMessage(Message msg){
			if(autoChange){
				try{
					int position = musicService.player.getCurrentPosition();   //获取当前播放时间位置
					int mMax = musicService.player.getDuration();
					int sMax = seekBar.getMax();
					seekBar.setProgress(sMax * position / mMax);   //比例关系，同上
					txtInfo.setText(setPlayInfo(position / 1000, mMax / 1000));
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				seekBar.setProgress(0);
				txtInfo.setText("已停止播放");
			}
		}		
	}
	
	
	private String setPlayInfo(int position , int max ){       //设置txtInfo和时间格式
		String info = "正在播放：" + musicService.songName + "\t\t";
		
		int pMinutes = 0;
		while(position > 60){
			pMinutes++;
			position -= 60;
		}
		String now = (pMinutes < 10 ? "0" + pMinutes : pMinutes) + ":" 
				+(position < 10 ?"0" + position : position);
		
		int mMinutes = 0;
		while (max >= 60) {
			mMinutes++;
			max -= 60;
		}
		String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
				+ (max < 10 ? "0" + max : max);
		return info + now + "/" + all;
	}
	
	
	class MusicThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try{
				musicHandler.sendMessage(new Message());
				Thread.sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();				
				}
			}
		}
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



}
