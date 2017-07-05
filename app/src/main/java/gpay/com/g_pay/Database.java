package gpay.com.g_pay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adetunji on 6/9/17.
 */

public class Database extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 1;
    private static final String USER_TABLE_NAME = "phed_app_user";
    private static final String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE_NAME + " (id INTEGER PRIMARY KEY autoincrement,customerId INTEGER UNIQUE,name VARCHAR, account VARCHAR UNIQUE,address VARCHAR,phone VARCHAR,aType VARCHAR);";

    Database(Context context)
    {
        super(context, "PHED_APP_DB", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(USER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public boolean insertValue(int customerId,String name,String account,String address,String phone,String type)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customerId",customerId);
        contentValues.put("name",name);
        contentValues.put("account",account);
        contentValues.put("address",address);
        contentValues.put("phone",phone);
        contentValues.put("aType",type);


       long val = sqLiteDatabase.insert(USER_TABLE_NAME,null,contentValues);
        if (val == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getDbAccounts()
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+USER_TABLE_NAME,null);
        return cursor;
    }
}