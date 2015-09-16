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
	public static MusicHandler musicHandler;    //����������¼�
	private MusicThread musicThread;    //�Զ��ı���������߳�
	private boolean autoChange, manulChange;  //�жϽ��������Զ��ı仹���ֶ��ı�
	private boolean startThread;      //�Ƿ�ʼ�Զ��ı���������߳�
	private boolean idPause;   //�Ƿ��Զ��ָ��������²���
	

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
		//��listview�а�ĳ�׸轲�Զ����Ÿø���
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(musicService.player.isPlaying()){
					musicService.stop();
				}
				musicService.songNum = arg2;       //arg2����ͼ��һ���е�λ������
				startPlayer();				
			}
		});
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {   //ֹͣ�϶�ʱ
				// TODO Auto-generated method stub
				int progress = seekBar.getProgress();
				if(!autoChange && manulChange){
					int musicMax = musicService.player.getDuration();  //�õ������������
					int seekBarMax = seekBar.getMax();    //���������ܳ���
					musicService.player.seekTo(musicMax * progress /seekBarMax);   //������������
					musicService.parse();
					autoChange = true;
					manulChange = false;
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { //�����϶�
				// TODO Auto-generated method stub
				if(musicService.player.isPlaying()){
					txtInfo.setText("���ڻ��塤����");
					musicService.parse();
					autoChange = false;
					manulChange = true;
				}
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {        //�����������ı�ʱִ��
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
			btnStart.setImageResource(R.drawable.pause1);      //�ĳ���ͣͼ��
		}		
	}
	
	private void setListViewAdapter(){     //�����б�listview����䷽��
		List<Map<String,Object>> date = new ArrayList<Map<String,Object>>();		
		for(String path: musicService.musicList){
			Map<String,Object> map = new HashMap<String, Object>();
			File file = new File(path);
			map.put("filename", file.getName());
			date.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, date,    //������
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
					int position = musicService.player.getCurrentPosition();   //��ȡ��ǰ����ʱ��λ��
					int mMax = musicService.player.getDuration();
					int sMax = seekBar.getMax();
					seekBar.setProgress(sMax * position / mMax);   //������ϵ��ͬ��
					txtInfo.setText(setPlayInfo(position / 1000, mMax / 1000));
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				seekBar.setProgress(0);
				txtInfo.setText("��ֹͣ����");
			}
		}		
	}
	
	
	private String setPlayInfo(int position , int max ){       //����txtInfo��ʱ���ʽ
		String info = "���ڲ��ţ�" + musicService.songName + "\t\t";
		
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
