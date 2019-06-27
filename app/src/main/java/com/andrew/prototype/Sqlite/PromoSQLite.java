package com.andrew.prototype.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PromoSQLite extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "recent_promo";
    private static final String KEY_ID = "id";
    private static final String KEY_TEMPLATE_BACKGROUND = "template_background";
    private static final String KEY_TEMPLATE = "promo_template";
    private static final String KEY_PROMO_TITLE = "promo_title";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_OFFICE_PHONE_NUMBER = "office_phone_number";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_PROMOTION_CATEGORY = "promotion_category";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DESCRIPTION = "description";
    private static final int VERSION = 2;

    public PromoSQLite(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL = "create table " + TABLE_NAME + " (" +
                KEY_ID + " integer primary key not null," +
                KEY_TEMPLATE_BACKGROUND + " integer," +
                KEY_TEMPLATE + " varchar," +
                KEY_PROMO_TITLE + " varchar," +
                KEY_EMAIL + " varchar," +
                KEY_PHONE_NUMBER + " varchar," +
                KEY_OFFICE_PHONE_NUMBER + " varchar," +
                KEY_START_DATE + " varchar," +
                KEY_END_DATE + " varchar," +
                KEY_START_TIME + " varchar," +
                KEY_END_TIME + " varchar," +
                KEY_PROMOTION_CATEGORY + " varchar," +
                KEY_ADDRESS + " varchar," +
                KEY_DESCRIPTION + " varchar" +
                ")";
        sqLiteDatabase.execSQL(SQL);
    }

    public void insertData(int id, String template, int template_background, String promo_title, String email, String phone_number
            , String office_phone, String start_date, String end_date, String start_time
            , String end_time, String promotion_category, String address, String description) {
        String SQL = "insert into " + TABLE_NAME + " values (" +
                "'" + id + "'," +
                "'" + template_background + "'," +
                "'" + template + "'," +
                "'" + promo_title + "'," +
                "'" + email + "'," +
                "'" + phone_number + "'," +
                "'" + office_phone + "'," +
                "'" + start_date + "'," +
                "'" + end_date + "'," +
                "'" + start_time + "'," +
                "'" + end_time + "'," +
                "'" + promotion_category + "'," +
                "'" + address + "'," +
                "'" + description + "'" +
                ")";
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(SQL);
    }

    public void deleteAll(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String delete = "delete from " + TABLE_NAME + " where " + KEY_ID + " = '" + id + "'";
        sqLiteDatabase.execSQL(delete);
    }

    public Cursor getData(int id) {
        String SQL = "select * from " + TABLE_NAME + " where " + KEY_ID + " = '" + id + "'";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(SQL, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
