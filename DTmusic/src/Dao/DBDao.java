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
	
	//���һ������,��Ӧtanmu��	
	public void save(tanmu tanmu){
		SQLiteDatabase db = databaseDanmu.getWritableDatabase();   //��ȡʵ��
		db.execSQL("insert into tanmuTable(tanmuText,songName,position) values(?,?,?)", 
				new Object[]{tanmu.gettanmuText(),tanmu.getsongName(),tanmu.getposition()});  
        db.close(); 
	}
	
	//ɾ��һ������,ɾ��һ������������е�Ļ
    public void delete(String songName){  
	        SQLiteDatabase db = databaseDanmu.getWritableDatabase();  
	        db.execSQL("delete from tanmuTable where songName=?", new Object[]{songName});  
	        db.close();  
    }  
    
    //����һ������
    public tanmu find(String songName){
    	SQLiteDatabase db = databaseDanmu.getWritableDatabase();
    	Cursor cursor;
    	tanmu sometanmu;
    	
    	cursor = db.rawQuery("select * from tanmuTable where songName=?", new String[]{songName});  //�洢tanmu
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
    
    //����һ������
    public tanmu find(int tanmuid){
    	SQLiteDatabase db = databaseDanmu.getWritableDatabase();
    	Cursor cursor;
    	tanmu sometanmu;
    	String aid = String.valueOf(tanmuid);
    	cursor = db.rawQuery("select * from tanmuTable where tanmuid=?", new String[]{aid});  //�洢tanmu
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
    
    //��ȡ��������
    public long getCount(){  
        SQLiteDatabase db = databaseDanmu.getReadableDatabase();  
        Cursor cursor =db.rawQuery("select count(*) from tanmuTable", null);  
        cursor.moveToFirst();  
        long reslut = cursor.getLong(0);  
        return reslut;  
    }  
	
	
	
}
