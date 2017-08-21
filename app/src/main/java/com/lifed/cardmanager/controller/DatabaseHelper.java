package com.lifed.cardmanager.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lifed.cardmanager.model.Card;
import com.lifed.cardmanager.model.CardType;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "Cards.db";
    public static final String CARDS_TABLE = "Cards";
    public static final String CARD_TYPES_TABLE = "CardTypes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        //onCreate(db);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CARDS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "number TEXT UNIQUE NOT NULL, " +
                "username TEXT, " +
                "id_card_type INTEGER NOT NULL, " +
                "FOREIGN KEY (id_card_type) REFERENCES " + CARD_TYPES_TABLE + "(id))");
        db.execSQL("CREATE TABLE " + CARD_TYPES_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type_name TEXT NOT NULL, discount INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CARDS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CARD_TYPES_TABLE);
        onCreate(db);
    }

    public void dropDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1, 1);
    }

    public boolean addCardType(String type_name, Integer discount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type_name", type_name);
        contentValues.put("discount", discount);
        long result = db.insert(CARD_TYPES_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public List<CardType> getAllCardTypes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CARD_TYPES_TABLE, null);
        List<CardType> data = new ArrayList<>();

        while(res.moveToNext()){
            CardType cardType = new CardType();
            try {
                cardType.setId(res.getInt(0));
                cardType.setTypeName(res.getString(1));
                cardType.setDiscount(res.getInt(2));
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }

            data.add(cardType);
        }

        return data;
    }

    public CardType getCardType(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CARD_TYPES_TABLE + " WHERE id = " + id, null);
        if(res.getCount() == 0)
            return null;

        res.moveToNext();
        CardType cardType = new CardType();
        try {
            cardType.setId(res.getInt(0));
            cardType.setTypeName(res.getString(1));
            cardType.setDiscount(res.getInt(2));
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        return cardType;
    }

    public boolean editCardType(Integer id, String type_name, Integer discount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type_name", type_name);
        contentValues.put("discount", discount);
        long result = db.update(CARD_TYPES_TABLE, contentValues, "id = ?", new String[] { id.toString() });
        if(result == 1)
            return true;
        else
            return false;
    }

    public Integer deleteCardType(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Integer res = db.delete(CARD_TYPES_TABLE, "id = ?", new String[] { id.toString() });
            return res;
        }catch (SQLiteConstraintException e){
            Log.e(TAG, e.getMessage());
            return 0;
        }
    }

    public List<Card> getAllCards(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CARDS_TABLE, null);
        List<Card> data = new ArrayList<>();

        if(res.getCount() == 0){
            return data;
        }

        while(res.moveToNext()){
            Card card = new Card();
            try {
                card.setId(res.getInt(0));
                card.setNumber(res.getString(1));
                card.setUsername(res.getString(2));
                card.setCardType(getCardType(res.getInt(3)));
            }catch (Exception e){

            }

            data.add(card);
        }

        return data;
    }

    public Card getCard(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CARDS_TABLE + " WHERE id = " + id, null);
        if(res.getCount() == 0)
            return null;

        res.moveToNext();
        Card card = new Card();
        try {
            card.setId(res.getInt(0));
            card.setNumber(res.getString(1));
            card.setUsername(res.getString(2));
            card.setCardType(getCardType(res.getInt(3)));
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        return card;
    }

    public boolean addCard(String number, String username, Integer idCardType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("username", username);
        contentValues.put("id_card_type", idCardType);
        long result = db.insert(CARDS_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean editCard(Integer id, String number, String holder, Integer id_card_type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("username", holder);
        contentValues.put("id_card_type", id_card_type);
        long result = db.update(CARDS_TABLE, contentValues, "id = ?", new String[] { id.toString() });
        if(result == 1)
            return true;
        else
            return false;
    }

    public Integer deleteCard(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CARDS_TABLE, "id = ?", new String[] { id.toString() });
    }

    public void deleteAllCards(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CARDS_TABLE, "", null);
    }
}
