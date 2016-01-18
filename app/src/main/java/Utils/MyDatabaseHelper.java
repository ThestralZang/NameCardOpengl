package Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Wentao on 2015/12/27.
 *
 * Use to get and set Name Cards data in DataBase.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAMECARD = "NameCard";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_JOB = "job";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHOTOPATH = "photoPath";

    private static final String CREATE_NAMECARD = "Create table "+TABLE_NAMECARD+" ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME+" text, "
            + COLUMN_PHOTOPATH+" text, "
            + COLUMN_JOB+" text, "
            + COLUMN_PHONE+" text, "
            + COLUMN_EMAIL+" text, "
            + COLUMN_ADDRESS+" text )";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NAMECARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        switch (i){
            default: break;
        }
    }

}
