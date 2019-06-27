package com.andrew.prototype.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class PromoStuffSQLite extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "stuff_promo";
    private static final String KEY_ID = "key_id";
    private static final String KEY_PREFERENCE_ID = "key_preference_id";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_IMAGE = "key_image";
    private static final String KEY_VALUE = "key_value";
    private static final int VERSION = 2;

    public PromoStuffSQLite(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL = "create table " + TABLE_NAME + "(" +
                KEY_ID + " integer primary key autoincrement," +
                KEY_PREFERENCE_ID + " integer not null," +
                KEY_NAME + " varchar not null," +
                KEY_IMAGE + " blob," +
                KEY_VALUE + " varchar not null" +
                ");";
        sqLiteDatabase.execSQL(SQL);
    }

    public void insertData(int prefId, String name, byte[] image, String value) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

//        if (image == null)
//            SQL = "insert into " + TABLE_NAME + " values (NULL,'" + prefId + "','"+ name +'", ?,'"+value + "');";

//        sqLiteDatabase.execSQL(SQL);
        if (image == null) {
            String SQL = "insert into " + TABLE_NAME + " values (NULL,'" + prefId + "','" + name + "',NULL,'" + value + "');";
            sqLiteDatabase.execSQL(SQL);
        } else {
            String SQL = "insert into " + TABLE_NAME + " values (NULL,'" + prefId + "','" + name + "',?,'" + value + "');";
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(SQL);
            sqLiteStatement.clearBindings();
//        sqLiteStatement.bindString(2, name);
//        if (image != null) {
            sqLiteStatement.bindBlob(1, image);
//            sqLiteStatement.bindString(4, value);
//        }
//        else sqLiteStatement.bindBlob(3, "NULL");
            sqLiteStatement.executeInsert();
        }
    }

    public void deleteAll(int ID) {
        String SQL = "delete from " + TABLE_NAME + " where " + KEY_PREFERENCE_ID + " = '" + ID + "';";
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(SQL);
    }

    public Cursor getData(int ID) {
        String SQL = "select * from " + TABLE_NAME + " where " + KEY_PREFERENCE_ID + " = '" + ID + "';";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(SQL, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
