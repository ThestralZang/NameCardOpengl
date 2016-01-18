package Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Interface.DatabaseController;

/**
 * Created by Wentao on 2015/12/29.
 *
 * The implement of interface DatabaseController
 */
public class MyDatabaseController implements DatabaseController {

    private static String WHERE_CLAUSE_ID = MyDatabaseHelper.COLUMN_ID + " = ?";

    @Override
    public void saveCardPhotoPath(Context context, final NameCard nameCard) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, DatabaseController.DATABASE_NAME, null, 1);
        final SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        if (nameCard.getId() != -1){
            (new Thread(){
                @Override
                public void run() {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MyDatabaseHelper.COLUMN_PHOTOPATH, nameCard.getPhotoPath());
                    db.update(MyDatabaseHelper.TABLE_NAMECARD, contentValues, WHERE_CLAUSE_ID,
                            new String[]{Integer.toString(nameCard.getId())});
                    contentValues.clear();
                }
            }).start();
        }
    }

    @Override
    public void deleteCard(Context context, final NameCard nameCard) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, DatabaseController.DATABASE_NAME, null, 1);
        final SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        if (nameCard.getId() != -1){
            (new Thread(){
                @Override
                public void run() {
                    db.delete(MyDatabaseHelper.TABLE_NAMECARD, WHERE_CLAUSE_ID,
                            new String[]{Integer.toString(nameCard.getId())});
                }
            }).start();
        }

    }

    @Override
    public void deleteCardList(Context context, List<NameCard> cardList) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, DatabaseController.DATABASE_NAME, null, 1);
        final SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        final Iterator i = cardList.iterator();//set as a final variable so that can be used in another thread

        (new Thread(){
            @Override
            public void run() {
                NameCard nameCard;
                db.beginTransaction();
                try {
                    while (i.hasNext()) {
                        nameCard = (NameCard) i.next();
                        if (nameCard.getId() != -1){
                            db.delete(MyDatabaseHelper.TABLE_NAMECARD, WHERE_CLAUSE_ID,
                                    new String[]{Integer.toString(nameCard.getId())});
                        }
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                }
            }
        }).start();


    }

    @Override
    public List<NameCard> getCardList(Context context) {
        List<NameCard> cardList = new ArrayList<>();
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, DatabaseController.DATABASE_NAME, null, 1);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAMECARD, null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                NameCard nameCard = new NameCard(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_JOB)),
                        cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS)));
                nameCard.setId(cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID)));
                nameCard.setPhotoPath(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PHOTOPATH)));
                cardList.add(nameCard);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return cardList;
    }

    @Override
    public void saveCard(Context context, final NameCard nameCard) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, DatabaseController.DATABASE_NAME, null, 1);
        final SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        if (nameCard.getId() == -1){
            (new Thread(){
                @Override
                public void run() {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MyDatabaseHelper.COLUMN_NAME, nameCard.getName());
                    contentValues.put(MyDatabaseHelper.COLUMN_JOB, nameCard.getJob());
                    contentValues.put(MyDatabaseHelper.COLUMN_PHONE, nameCard.getPhone());
                    contentValues.put(MyDatabaseHelper.COLUMN_EMAIL, nameCard.getEmail());
                    contentValues.put(MyDatabaseHelper.COLUMN_ADDRESS, nameCard.getAddress());
                    contentValues.put(MyDatabaseHelper.COLUMN_PHOTOPATH, nameCard.getPhotoPath());
                    nameCard.setId((int) db.insert(MyDatabaseHelper.TABLE_NAMECARD, null, contentValues));
                    contentValues.clear();
                }
            }).start();
        }else {
            (new Thread(){
                @Override
                public void run() {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MyDatabaseHelper.COLUMN_NAME, nameCard.getName());
                    contentValues.put(MyDatabaseHelper.COLUMN_JOB, nameCard.getJob());
                    contentValues.put(MyDatabaseHelper.COLUMN_PHONE, nameCard.getPhone());
                    contentValues.put(MyDatabaseHelper.COLUMN_EMAIL, nameCard.getEmail());
                    contentValues.put(MyDatabaseHelper.COLUMN_ADDRESS, nameCard.getAddress());
                    db.update(MyDatabaseHelper.TABLE_NAMECARD, contentValues, WHERE_CLAUSE_ID,
                            new String[]{Integer.toString(nameCard.getId())});
                    contentValues.clear();
                }
            }).start();
        }

    }

    @Override
    public void insertCardList(Context context, List<NameCard> insertCardList) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, DatabaseController.DATABASE_NAME, null, 1);
        //set as a final variable so that can be used in another thread
        final SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        final Iterator i = insertCardList.iterator();

        //Start a new thread to do insert data to DB
        (new Thread(){
            @Override
            public void run() {
                NameCard nameCard;
                ContentValues contentValues = new ContentValues();
                db.beginTransaction();
                try {
                    while (i.hasNext()){
                        nameCard = (NameCard) i.next();
                        contentValues.put(MyDatabaseHelper.COLUMN_NAME, nameCard.getName());
                        contentValues.put(MyDatabaseHelper.COLUMN_JOB, nameCard.getJob());
                        contentValues.put(MyDatabaseHelper.COLUMN_PHONE, nameCard.getPhone());
                        contentValues.put(MyDatabaseHelper.COLUMN_EMAIL, nameCard.getEmail());
                        contentValues.put(MyDatabaseHelper.COLUMN_ADDRESS, nameCard.getAddress());
                        nameCard.setId((int) db.insert(MyDatabaseHelper.TABLE_NAMECARD, null, contentValues));
                        contentValues.clear();
                    }
                    db.setTransactionSuccessful();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                }
            }
        }).start();

    }


}
