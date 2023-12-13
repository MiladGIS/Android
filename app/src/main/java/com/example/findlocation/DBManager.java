package com.example.findlocation;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBManager {
    DBHelper helper;
    public DBManager(Context context) { helper = new DBHelper(context);}


    public long insertData(String Info, String Xc, String Yc, String SSID, String BSSID,  String RSS) {
        SQLiteDatabase DBB = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.Info, Info);
        contentValues.put(DBHelper.Xc, Xc);
        contentValues.put(DBHelper.Yc, Yc);
        contentValues.put(DBHelper.SSID, SSID);
        contentValues.put(DBHelper.BSSID, BSSID);
        contentValues.put(DBHelper.RSS, RSS);
        return DBB.insert(DBHelper.TABLE_NAME, null , contentValues);
    }

    public Cursor fetch() {
        SQLiteDatabase DB = helper.getWritableDatabase();
        return DB.rawQuery("Select * from AccessPointTable", null);
    }

    public Cursor fetchData(String BSSID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.rawQuery("Select * from AccessPointTable where BSSID = ?", new String[]{BSSID});
    }


    @SuppressLint("Recycle")
    public Boolean deletedata(String Xc, String Yc) {
        SQLiteDatabase DB = helper.getWritableDatabase();
        Cursor cursor;
        cursor = DB.rawQuery("Select * from AccessPointTable where Xc = ? and Yc = ?", new String[]{Xc, Yc});
        if (cursor.getCount() > 0) {
            int result = DB.delete("AccessPointTable", "Xc = ? and Yc = ?", new String[]{Xc, Yc});
            return result != -1;
        } else {
            return false;
        }
    }


    static class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "WiFiDatabase";    // Database Name
        private static final String TABLE_NAME = "AccessPointTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String UID="_id";     // Column I (Primary Key)
        private static final String Info = "Info";    //Column II
        private static final String Xc = "Xc";    //Column III
        private static final String Yc = "Yc";    //Column IV
        private static final String SSID = "SSID";    //Column V
        private static final String BSSID = "BSSID";    //Column VI
        private static final String RSS = "RSS";    // Column VII
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                "("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ Info +" VARCHAR(512), "+ Xc +" DECIMAL(10,5), "+ Yc +" DECIMAL(10,5), "+ SSID +" VARCHAR(225), "+ BSSID +" VARCHAR(225), "+ RSS +" INTEGER);";
        private final Context context;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }



        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Toast.makeText(context ,""+e,Toast.LENGTH_LONG).show();
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
