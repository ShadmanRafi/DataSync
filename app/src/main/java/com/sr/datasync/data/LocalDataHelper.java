package com.sr.datasync.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sr.datasync.EntryClass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class LocalDataHelper extends SQLiteOpenHelper {
    public static final String DATA_TABLE = "DATA_TABLE";
    public static final String ENTRY_ID = "ENTRY_ID";
    public static final String ENTRY_TITLE = "ENTRY_TITLE";
    public static final String ENTRY_DATA = "ENTRY_DATA";

    public LocalDataHelper(@Nullable Context context) {
        super(context, "local_data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + DATA_TABLE + " ( " + ENTRY_ID + " TEXT PRIMARY KEY, " + ENTRY_TITLE + " TEXT, " + ENTRY_DATA + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+DATA_TABLE);
        onCreate(db);
    }

    public boolean addToTable(String dataID, EntryClass entryClass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRY_ID, dataID);
        contentValues.put(ENTRY_TITLE, entryClass.getTitle());
        contentValues.put(ENTRY_DATA, entryClass.getData());
        long res = db.insert(DATA_TABLE, null, contentValues);
        return res != -1;
    }

    public boolean addMultipleToTable(ArrayList<String> ids, ArrayList<EntryClass> entryClassArrayList){
        SQLiteDatabase db = this.getWritableDatabase();
        int newData = ids.size();
        boolean res = true;
        for(int i=0; i<newData; i++){
            boolean eachRes = addToTable(ids.get(i), entryClassArrayList.get(i));
            res = res | eachRes;
        }
        return res;
    }

    public boolean updateToTable(String dataID, EntryClass entryClass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRY_TITLE, entryClass.getTitle());
        contentValues.put(ENTRY_DATA, entryClass.getData());
        String whereClause = ENTRY_ID+" = '"+dataID+"'";
        long res = db.update(DATA_TABLE, contentValues, whereClause, null);
        return res == 1;
    }

    public List<ArrayList> getAllData(){
        String query = "SELECT * FROM "+DATA_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<EntryClass> entryClassList = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String newDataID = cursor.getString(0);
                String entryTitle = cursor.getString(1);
                String entryData = cursor.getString(2);
                idList.add(newDataID);
                entryClassList.add(new EntryClass(entryTitle, entryData));
            } while (cursor.moveToNext());
        }
        cursor.close();

        List resList = new ArrayList();
        resList.add(idList);
        resList.add(entryClassList);

        return resList;
    }

    public void resetAllData(){
        String query = "DELETE FROM "+DATA_TABLE;
        this.getWritableDatabase().execSQL(query);
    }

    public boolean deleteFromDB(String dataID){
        String whereClause = ENTRY_ID+" = '"+dataID+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(DATA_TABLE, whereClause, null);
        return res>0;
    }

    public EntryClass getSingleDataFromID(String id){
        String dataText, titleText;
        String query = "SELECT * FROM "+DATA_TABLE+" WHERE "+ENTRY_ID+" = '"+id+"'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        titleText = cursor.getString(1);
        dataText = cursor.getString(2);
        cursor.close();

        return new EntryClass(titleText, dataText);
    }

    public boolean checkIfDataExists(String dataID){
        String dataText, titleText;
        String query = "SELECT * FROM "+DATA_TABLE+" WHERE "+ENTRY_ID+" = '"+dataID+"'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount()>=1;
    }
}
