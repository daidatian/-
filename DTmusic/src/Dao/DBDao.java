package Dao;

import java.util.List;

import org.w3c.dom.UserDataHandler;

import com.example.tanmu.tanmu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.WebChromeClient.CustomViewCallback;
import Database.DatabaseDanmu;

public class DBDao {
	DatabaseDanmu databaseDanmu;
	public DBDao(Context context) {
		// TODO Auto-generated constructor stub
		this.databaseDanmu = new DatabaseDanmu(context);
	}
	
	//添加一条数据,对应tanmu类	
	public void save(tanmu tanmu){
		SQLiteDatabase db = databaseDanmu.getWritableDatabase();   //获取实例
		db.execSQL("insert into tanmuTable(tanmuText,songName,position) values(?,?,?)", 
				new Object[]{tanmu.gettanmuText(),tanmu.getsongName(),tanmu.getposition()});  
        db.close(); 
	}
	
	//删除一条数据,删除一条歌里面的所有弹幕
    public void delete(String songName){  
	        SQLiteDatabase db = databaseDanmu.getWritableDatabase();  
	        db.execSQL("delete from tanmuTable where songName=?", new Object[]{songName});  
	        db.close();  
    }  
    
    //查找一条数据
    public tanmu find(String songName){
    	SQLiteDatabase db = databaseDanmu.getWritableDatabase();
    	Cursor cursor;
    	tanmu sometanmu;
    	
    	cursor = db.rawQuery("select * from tanmuTable where songName=?", new String[]{songName});  //存储tanmu
    	if(cursor.moveToFirst()){
    	    String text = cursor.getString(cursor.getColumnIndex("tanmuText"));
    	    String songName2 = cursor.getString(cursor.getColumnIndex("songName"));
    		int position = cursor.getInt(cursor.getColumnIndex("position"));
    		tanmu tanmu1 = new tanmu(text, songName2, position);
            return  tanmu1;    				 
    	}
    	
    	cursor.close();
    	return null;
    }
    
    //查找一条数据
    public tanmu find(int tanmuid){
    	SQLiteDatabase db = databaseDanmu.getWritableDatabase();
    	Cursor cursor;
    	tanmu sometanmu;
    	String aid = String.valueOf(tanmuid);
    	cursor = db.rawQuery("select * from tanmuTable where tanmuid=?", new String[]{aid});  //存储tanmu
    	if(cursor.moveToFirst()){
    	    String text = cursor.getString(cursor.getColumnIndex("tanmuText"));
    	    String songName2 = cursor.getString(cursor.getColumnIndex("songName"));
    		int position = cursor.getInt(cursor.getColumnIndex("position"));
    		tanmu tanmu1 = new tanmu(text, songName2, position);
            return  tanmu1;    				 
    	}
    	
    	cursor.close();
    	return null;
    }
    
    //获取数据总数
    public long getCount(){  
        SQLiteDatabase db = databaseDanmu.getReadableDatabase();  
        Cursor cursor =db.rawQuery("select count(*) from tanmuTable", null);  
        cursor.moveToFirst();  
        long reslut = cursor.getLong(0);  
        return reslut;  
    }  
	
	
	
}
