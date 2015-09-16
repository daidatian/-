package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseDanmu extends SQLiteOpenHelper{
	
	private static String DB_NAME = "tanmu.db";
	private static int VERSION = 1; 

	public DatabaseDanmu(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	//��д���췽��
	public DatabaseDanmu(Context te){
		super(te, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO �������ݿ�󣬶����ݿ�Ĳ���
		String SQL = "create table tanmuTable (tanmuid integer primary key autoincrement, tanmuText TEXT,songName varchar(80) not null,position int not null)";
		db.execSQL(SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO �������ݿ�汾�Ĳ���
		
	}

}
