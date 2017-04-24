package com.fjamtechnology.friendlyreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Franc on 4/17/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Friendly.db";

    private static final String Sql_Create = "CREATE TABLE `User` (" +
            "`idUser`INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
            "`Username` TEXT UNIQUE," +
            "`Password` TEXT," +
            "`Email` TEXT UNIQUE" +
            "); ";
    private static final String Sql_Create2 = "CREATE TABLE `Reminders` (" +
            " `RemindersID` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
            " `MarkersID` INTEGER, " +
            " `Reminder` TEXT, " +
            " FOREIGN KEY(`MarkersID`) REFERENCES Markers " +
            "); ";
    private static final String Sql_Create3 =
            "CREATE TABLE `Markers` ( " +
            " `MarkersID` INTEGER UNIQUE, " +
            " `idUser` INTEGER, " +
            " `MarkerName` TEXT UNIQUE, " +
            " `LongLat` TEXT UNIQUE, " +
            " PRIMARY KEY(`MarkersID`), " +
            " FOREIGN KEY(`idUser`) REFERENCES `User` " +
            "); ";

    private static final String Sql_Delete = "DROP TABLE IF EXISTS User, Markers, Reminders";

    /////////Table And ID Names ////////////////////////////////////////////////////////////////////
    private static final String UserTbl = "User", RemindersTbl = "Reminders", MarkersTbl = "Markers";
    private static final String UserIDCol = "idUser", UsernameCol = "Username", PasswordCol = "Password", EmailCol = "Email";
    private static final String ReminderIDCol = "RemindersID", MarkersIDCol = "MarkersID", ReminderTextCol = "Reminder";
    private static final String MarkerNameCol = "MarkerName", LongLatCol = "LongLat";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Sql_Create);
        db.execSQL(Sql_Create2);
        db.execSQL(Sql_Create3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Sql_Delete);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long RegistrationPage(String Username, String Password, String Email){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        Password = md5(Password);

        values.put("Username", Username);
        values.put("Password", Password);
        values.put("Email", Email);

        long Return = db.insert("User", null, values);

        return Return;

    }

    //////// Password Encryption MD5 ///////////////////////////////////////////////////////////////

    public String md5(final String Password) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(Password.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////// Verify User Exists and Password matches //////////////////////////////////////////////
    public String Verification(final String Username){
        String Pass = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query
        (UserTbl, new String[] {PasswordCol}, "Username = ?", new String[]{Username}, null, null, null);

        cursor.moveToFirst();

        if(cursor.getCount() <= 0){
            cursor.close();
            return Pass;
        }else{
            Pass = cursor.getString(0);
            return Pass;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Cursor getAllLocations(){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MarkersTbl, new String[] {MarkerNameCol, LongLatCol}, UserIDCol + " = ?", new String[]{"1"}, null, null, null);

        return cursor;
    }

}
