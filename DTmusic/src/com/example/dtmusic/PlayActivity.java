package com.example.dtmusic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Dao.DBDao;
import Database.DatabaseDanmu;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Printer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;


import com.example.dtmusic.MainActivity;
import com.example.tanmu.tanmu;

public class PlayActivity extends Activity implements OnClickListener{
	
	private String me;
	
	private DBDao dbdao;
	
	private tanmu tanmu1;
	private tanmu tanmu2;
	private tanmu tanmu3;
	private tanmu tanmu4;
	private tanmu tanmu5;
	private tanmu tanmu6;
	
    private MyHandler handler;
	
	//��Ļ����
	private TanmuBean tanmuBean;
	private TanmuBean tanmuBean2;
	//���õ�Ļ���ݵĸ����
	private RelativeLayout containerVG;
	
	//������ĸ߶�
	private int validHeightSpace;
	//���Ͱ�ť
	private Button send;
	private TextView newtanmu;
	ImageButton startTanmuView;
	private List<tanmu> alltanmu;
	private List<tanmu> sometanmu;
	
	//private MusicService musicService;
	private MusicHandler musicHandler;    //����������¼�
	private MusicThread musicThread;    //�Զ��ı���������߳�
	private boolean autoChange, manulChange;  //�жϽ��������Զ��ı仹���ֶ��ı�
	private boolean startThread; 
	
	int position;//��õ�ǰ�������ֵ�λ�ã�-1����û�и������š�
	
	ImageView iv1_back,iv2_pre,iv3_next;
	ToggleButton togglebutton;
	SeekBar seekBar;
	private TextView tv1_music,tv2_left_time,tv3_right_time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
//		DatabaseDanmu dm = new DatabaseDanmu(this); //ʵ��������
//		SQLiteDatabase db = dm.getWritableDatabase();  //��ȡд�����ݶ��󡣼���ȡ�Խ����ݿ��ʵ��
//		ContentValues values = new ContentValues();  //ʵ�����������ݶ���
//		values.put("tanmuText", tanmu2.gettanmuText());
//		values.put("songName", tanmu2.getsongName());
//		values.put("position", tanmu2.getposition());
//		db.insert("tanmu.db", null, values);
//		db.close();		
		me = new String("����DDT����һ�Ρ�������2015��6��24��()");
		
		send = (Button) findViewById(R.id.sendTanmu);
		newtanmu = (TextView) findViewById(R.id.tanmuText);
		
		containerVG = (RelativeLayout) findViewById(R.id.tanmu_container);
		tanmuBean = new TanmuBean();
		alltanmu = new ArrayList<tanmu>();
		sometanmu = new ArrayList<tanmu>();		
		
		tanmu6 = new tanmu("�㱻ƭ����", "yueding", 9000);
//		tanmu4 = new tanmu("123123","adb remount qwe2",9000);  //���ݿⵯĻ
//		tanmu3 = new tanmu("��ǰ���¹⣬���ǵ���˪����ͷ�����£���ͷ˼���硣","bigapple",9000);
//		tanmu2 = new tanmu("��Ļ���壡��","adb remount qwe2",6000);  //��ʱ��Ļ	
//		alltanmu.add(tanmu2);
//		alltanmu.add(tanmu3);
//		alltanmu.add(tanmu6);
		//sometanmu.add(tanmu2);
		
		//д�����ݿ�
		dbdao = new DBDao(this);
		
		dbdao.save(tanmu6);
//		dbdao.save(tanmu4);
////		dbdao.save(tanmu2);
//		tanmu5 = new tanmu();
//		tanmu5 = dbdao.find("adb remount qwe2");
//		alltanmu.add(tanmu5);
		
		getDatabase(alltanmu);  //�����ݿ�
		
		tanmusort(sometanmu);   //�������е�Ļ
		sometanmu.add(tanmu6);
		//alltanmu.add("23333333");
		//alltanmu.add("��ǰ���¹⣬���ǵ���˪����ͷ�����£���ͷ˼����");		
		tanmuBean.setItems(sometanmu);
		//tanmuBean.setItems(alltanmu);
		
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(newtanmu.getText().toString().equals("")){  //���ı����ܷ��͵�Ļ
					return;
				}
				//tanmu1 = new tanmu(newtanmu.getText().toString(), MainActivity.musicService.songName, 4000);
				if(MainActivity.musicService.songName == null){
					newtanmu.setText("��ǰҳ�治�ܷ��͵�Ļ");
					return;
				}
				tanmu1 = new tanmu();				
				tanmu1.setposition(13000);//���ó��ֵ�Ļ��ʱ��
				tanmu1.settanmuText(newtanmu.getText().toString()); //���õ�Ļ�ı�				
				tanmu1.setsongName(MainActivity.musicService.songName);  //ƥ��Ϊ��ǰ����
				dbdao.save(tanmu1);   //������ݿ�
				alltanmu.add(tanmu1);  //���뵯Ļ��List��
				tanmusort(sometanmu);
				new Thread(new CreateTanmuThread()).start();
				newtanmu.setText("");
			}
		});
		
		
		
		handler = new MyHandler(this);
		
		//��ʼ��Ļ
		startTanmuView = (ImageButton) findViewById(R.id.startTanmu);
		startTanmuView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(containerVG.getChildCount() > 1){
					return;
				}				
				existMarginValues.clear();
				tanmusort(sometanmu);
				new Thread(new CreateTanmuThread()).start();
				
			}
		});
		
		init();
	}	
	
	
	public void init(){
		iv1_back = (ImageView) findViewById(R.id.play_linear1_ig1);
		iv2_pre = (ImageView) findViewById(R.id.play_linear3_img1);
		iv3_next = (ImageView) findViewById(R.id.play_linear3_img3);
		tv1_music = (TextView) findViewById(R.id.play_linear1_text1);  
		togglebutton = (ToggleButton) findViewById(R.id.play_linear3_toggleButton);
		startTanmuView = (ImageButton) findViewById(R.id.startTanmu);
		tv1_music.setText("12");  
		
		iv1_back.setOnClickListener(this);
		iv2_pre.setOnClickListener(this);
		iv3_next.setOnClickListener(this);
		
		togglebutton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					MainActivity.musicService.parse();
				}else{
					MainActivity.musicService.start();
				}
			}
		});

	
		
		//new Thread(musicThread).start();	
		new Thread(new MusicThread()).start();
		
		

	}
	
	class MusicHandler extends Handler {
		public MusicHandler() {
		}
		public void handleMessage(Message msg){
			if(true){
				try{	
					int position = MainActivity.musicService.player.getCurrentPosition();   //��ȡ��ǰ����ʱ��λ��
					int mMax = MainActivity.musicService.player.getDuration();
					int sMax = seekBar.getMax();
					seekBar.setProgress(sMax * position / mMax);   //������ϵ��ͬ��
					tv1_music.setText(MainActivity.musicService.songName);
					//txtInfo.setText(setPlayInfo(position / 1000, mMax / 1000));
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				seekBar.setProgress(0);
				tv1_music.setText("��������");
				//txtInfo.setText("��ֹͣ����");
			}
		  }		
		}	
	
	
	class MusicThread implements Runnable{
		
			@Override
			public void run() {
				tv1_music.setText(MainActivity.musicService.songName);	
				// TODO Auto-generated method stub
				while(true){
					try{
						//musicHandler.sendMessage(new Message());							
					    Thread.sleep(1000);
					}catch(InterruptedException e){
						e.printStackTrace();				
					}
				}
			}
		
	}
	
	@Override	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.play_linear1_ig1:
			onBackPressed();    //������һ������
			break;
		case R.id.play_linear3_img1:
			MainActivity.musicService.last();
			break;
		case R.id.play_linear3_img3:
			MainActivity.musicService.next();
			break;		
		default:
			break;
		}
	}
	
	
	//��ȡ���ݿ�,�Ž�list	
	private void getDatabase(List<tanmu> tanmuList){
		tanmu tanmu;
		tanmuList.clear();
		int N = (int) dbdao.getCount();
		for(int i = 1; i <= N; i++){
			tanmu = dbdao.find(i);
			tanmuList.add(tanmu);
		}		
	}
	
	//���ݸ������൯Ļ������ʱ������Ļ
	    private void tanmusort(List<tanmu> sometanmu1){
	    	tanmu temp;	
	    	sometanmu1.clear();
	    	if(MainActivity.musicService.songName == null ){
	    		temp = new tanmu(me, null, 2000);
	    		sometanmu1.add(temp);
	    		sometanmu1.add(new tanmu("�ܷ������ҳ��˵����Ҳ����һ����", null, 4000));
	    		sometanmu1.add(new tanmu("˳������㵱ǰ���޸�������", null, 6000));
	    		sometanmu1.add(new tanmu("����������ô֪����", null, 6000));
	    		sometanmu1.add(new tanmu("��������һ������", null, 6000));
	    		sometanmu1.add(new tanmu("���ҵĵ�ľ���������š�yueding�����о�ϲŶ!", null, 6000));
	    		sometanmu1.add(new tanmu("һ��Ҫ�����Ƴ���Լ������������Ҫ��Ϊ������ƴ��������Ͳ�����", null, 6000));
	    		sometanmu1.add(new tanmu("���Եģ�����ȥ�ˣ�����û�е�Ļ�ˣ�ƭ��Сè", null, 6000));
	    		sometanmu1.add(new tanmu(" ", null, 6000));
	    		sometanmu1.add(new tanmu(" ", null, 6000));
	    		sometanmu1.add(new tanmu("����", null, 6000));
	    		return;
	    	}
	    	//int N = alltanmu.size();
	    	int N = alltanmu.size();
	    	for(int i = 0; i < N; i++ ){
	    		if(alltanmu.get(i).getsongName().equals(MainActivity.musicService.songName) ){
	    			sometanmu1.add(alltanmu.get(i));         //���˲�ͬ�����ĵ�Ļ
	    		}
	    	}
	    	//0x
//	    	int M = sometanmu1.size();
//	    	for(int x = 0; x < M - 1; x++){     //����position��������
//	    		for(int y = 1; y < M; y++){
//	    			if(sometanmu1.get(x).getposition() > sometanmu1.get(y).getposition()){
//	    				   temp = alltanmu.get(y);
//	    				   sometanmu1.get(y).settanmu(sometanmu1.get(x));
//	    				   sometanmu1.get(x).settanmu(temp);	    				   
//	    			}
//	    		}	    		 
//	    	}
	    	//1x
	    }
	
	
	//ÿ2���Զ����һ����Ļ
		private class CreateTanmuThread implements Runnable{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int N = tanmuBean.getItems().size();
				//int N = sometanmu.size();
				for(int i = 0; i < N; i++){					
					handler.obtainMessage(1,i,0).sendToTarget();
					SystemClock.sleep(2000);
				}
			}
			
		}
	
	
	//��Ҫ�����߳���������
	private static class MyHandler extends Handler {
		private WeakReference<PlayActivity> ref;
		
		MyHandler(PlayActivity ac ){
			ref = new WeakReference<PlayActivity>(ac);
		}
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			
			if( msg.what == 1 ){
				PlayActivity ac = ref.get();
				if(ac != null && ac.tanmuBean != null){
					int index = msg.arg1;
					String content = ac.tanmuBean.getItems().get(index).gettanmuText();
					float textSize = (float) (ac.tanmuBean.getMinTextSize() * 
							(1 + Math.random() * ac.tanmuBean.getRange())); 					
					
					ac.showTanmu(content, textSize, ac.tanmuBean.getColor());
					
				}
			}
		}		
	}
	
	
	private void showTanmu(String content, float textSize, int textColor){
		final TextView textView = new TextView(this);
		
		textView.setTextSize(textSize);
		textView.setText(content);
		textView.setTextColor(textColor);
		
		int leftMargin = containerVG.getRight() -
				containerVG.getLeft() - containerVG.getPaddingLeft();  
		//���㱾����Ļ��topMargin(���ֵ��������Ļ�����еĲ��ظ�)
		final int verticalMargin = getRandomTopMargin();
		textView.setTag(verticalMargin);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT); 
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.topMargin = verticalMargin;
		
		textView.setLayoutParams(params);
		Animation anim = AnimationHelper.createTranslateAnim(this,leftMargin, -ScreenUtils.getScreenW(this));       
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//�Ƴ������
				containerVG.removeView(textView);
				//�Ƴ�ռλ
				int vertivakMargin = (Integer) textView.getTag();
				existMarginValues.remove(verticalMargin);
			}
		});
		textView.startAnimation(anim);
		
		containerVG.addView(textView);
		
	}
	
	//��¼��ǰ������ʾ״̬�ĵ�Ļ��λ�ã������ظ���
	private Set existMarginValues = new HashSet();
	
	private int linesCount;
	
	private int getRandomTopMargin(){
		//�������ڵ�Ļ��ʾ�Ŀռ�߶�
		if(validHeightSpace == 0){
			validHeightSpace = containerVG.getBottom() - containerVG.getTop() -
					containerVG.getPaddingTop() - containerVG.getPaddingBottom();
		}
		//������õ�����
		if(linesCount == 0){
			linesCount = 10;
			//linesCount = validHeightSpace / ScreenUtils.dp2px(this,
				//	tanmuBean.getMinTextSize() * (1 + tanmuBean.getRange()));
			if(linesCount == 0){
			throw new RuntimeException("not enouth space to show text.");
			}
		}	
		while(true){
			int randomIndex = (int) (Math.random() * linesCount);
			int marginValue = randomIndex * (validHeightSpace / linesCount);
			
			if(!existMarginValues.contains(marginValue)){
				existMarginValues.add(marginValue);
				return marginValue;
			}
		}	
	}
	
	
	//ƽ�ƶ������ɹ���
	public static class AnimationHelper{
		//����ƽ�ƶ���
		public static Animation createTranslateAnim(Context context, int fromX, int toX){
			TranslateAnimation tlAnim = new TranslateAnimation(fromX, toX, 0, 0);
			long duration = (long) (Math.abs(toX - fromX) * 
					1.0f / ScreenUtils.getScreenW(context) * 4000);
			tlAnim.setDuration(duration);
			tlAnim.setInterpolator(new Interpolator() {
				
				@Override
				public float getInterpolation(float input) {
					// TODO Auto-generated method stub
					return (float) (Math.tan((input * 2 -1) / 4 * Math.PI)) / 2.0f + 0.5f;
				}
			});
			tlAnim.setFillAfter(true); 
			
			return tlAnim;
		}
	}
	
	
	//�Զ����Interpolator
		public class DecelerateAccelerateInterpolator implements Interpolator{
	        //input��0~1������ֵҲ��0~1������ֵ�����߱����ٶȼӼ�����
			@Override
			public float getInterpolation(float input) {
				// TODO Auto-generated method stub
				return (float) (Math.tan((input * 2 -1) / 4 * Math.PI)) / 2.0f + 0.5f;
			}		
		}
	
	
	public class TanmuBean {
		private List<tanmu> items;
		private int color;
		private int minTextSize;
		private float range;
		
		public TanmuBean(){
			//int default value
			color = Color.parseColor("#eeeeee");
			minTextSize = 16;
			range = 0.5f;
		}
		public List<tanmu> getItems(){
			return items;
		}
		public void setItems(List<tanmu> items){
			this.items = items;
		}
		public int getColor(){
			return color;
		}
		public void setColor(int color){
			this.color = color;			
		}
		public int getMinTextSize(){
			return minTextSize;
		}
		public void setMinTextSize(int MinTextSize){
			this.minTextSize = MinTextSize;
		}
		public float getRange(){
			return range;
		}
		public void setRange(float range){
			this.range = range;
		}
		
	}

	


}
