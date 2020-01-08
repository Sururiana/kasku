package com.sururiana.kasku.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Kasku";
    private static final int DATABASE_VERSION = 1;

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //TODO auto-generated construcktor stub
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE transaksi (transaksi_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, status TEXT," +
                        "jumlah DOUBLE, keterangan TEXT, tanggal DATE DEFAULT CURRENT_DATE );"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //TODO Auto-generated method stub
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS transaksi");
    }
}
