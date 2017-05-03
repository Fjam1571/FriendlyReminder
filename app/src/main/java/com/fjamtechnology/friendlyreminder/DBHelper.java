package com.fjamtechnology.friendlyreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mindrot.jbcrypt.BCrypt;

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
            " `MarkersID` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
            " `idUser` INTEGER, " +
            " `MarkerName` TEXT, " +
            " `LongLat` TEXT UNIQUE, " +
            " FOREIGN KEY(`idUser`) REFERENCES `User` " +
            "); ";

    private static final String Sql_Delete = "DROP TABLE IF EXISTS User, Markers, Reminders";

    /////////Table And ID Names ////////////////////////////////////////////////////////////////////
    private static final String UserTbl = "User", RemindersTbl = "Reminders", MarkersTbl = "Markers";
    private static final String UserIDCol = "idUser", UsernameCol = "Username", PasswordCol = "Password", EmailCol = "Email";
    private static final String ReminderIDCol = "RemindersID", MarkersIDCol = "MarkersID", ReminderTextCol = "Reminder";
    private static final String MarkerNameCol = "MarkerName", LongLatCol = "LongLat", MarkerID ="MarkersID";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * creation method for the database
     * @param db the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Sql_Create);
        db.execSQL(Sql_Create2);
        db.execSQL(Sql_Create3);
    }

    /**
     * runs if the database gets upgraded
     * @param db  the database
     * @param oldVersion  the old version
     * @param newVersion  the new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Sql_Delete);
        onCreate(db);
    }

    public String GetUserID(String Username){
        String UserID;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(UserTbl, new String[] {UserIDCol}, UsernameCol + " = ?", new String[]{Username}, null, null, null);
        cursor.moveToFirst();

        UserID = cursor.getString(0);

        return UserID;
    }

    /**
     * runs if the database gets downgraded
     * @param db  the database
     * @param oldVersion  the old version
     * @param newVersion  the new version
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     *
     * @param Username  users desired username that they want to register
     * @param Password  users desired password that they want to register
     * @param Email     users desired email that they want to register
     * @return
     */
    public long RegistrationPage(String Username, String Password, String Email){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        Password = BCrypt(Password);

        values.put("Username", Username);
        values.put("Password", Password);
        values.put("Email", Email);

        long Return = db.insert("User", null, values);

        return Return;

    }

    //////// Password Encryption BCrypt ///////////////////////////////////////////////////////////////

    /**
     * password hasing algorithm using the Bcrypt library
     * @param Password  users password to be hashed
     * @return hash
     */
    private String BCrypt(final String Password) {
        String hash = BCrypt.hashpw(Password, BCrypt.gensalt());
        return hash;
    }

    /**
     * verifys that the entered password matches the hashed password
     * @param Password users entered password
     * @param PasswordVerif hash in database
     * @return
     */
    public Boolean VerifyPass(final String Password, final String PasswordVerif){
        boolean PassMatch;

        PassMatch = BCrypt.checkpw(Password, PasswordVerif);

        return  PassMatch;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ///////// Verify User Exists and Password matches //////////////////////////////////////////////

    /**
     * Verify User Exists and Password matches
     * @param Username  he users username
     * @return  if verified
     */
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

    /**
     * methd that gets all LatLng locations for the user
     * @return cursor holding locations
     */
    public Cursor getAllLocations(String UserID){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MarkersTbl, new String[] {MarkerID, MarkerNameCol, LongLatCol}, UserIDCol + " = ?", new String[]{UserID}, null, null, null);

        return cursor;
    }

    public Cursor getAllReminders(String ID){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(RemindersTbl, new String[] {ReminderIDCol, ReminderTextCol}, MarkersIDCol+ " = ?", new String[]{ID}, null, null, null);

        return cursor;
    }


    //// Marker DB Options///////////////////////////////////////////////////////////////////////////
    public int InsertNewMarker(String MarkerName, String MarkerLatLong, String UserID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("idUser", UserID);
        values.put("MarkerName", MarkerName);
        values.put("LongLat", MarkerLatLong);

        long Return = db.insert("Markers", null, values);

        int id = (int)Return;

        return id;
    }

    public void DeleteAllMarkerReminders(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RemindersTbl, MarkerID + " = ?", new String[] {ID});
    }

    public void AddNewReminder(String MarkerID, String ReminderText){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MarkersIDCol, MarkerID);
        values.put(ReminderTextCol, ReminderText);

        db.insert(RemindersTbl, null, values);
    }

    public int ReturnMarkerID(String LatLong){
        int i;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MarkersTbl, new String[] {MarkerID}, LongLatCol + " = ?", new String[]{LatLong}, null, null, null);
        cursor.moveToFirst();

        i = cursor.getInt(0);

        return i;
    }

    public void RemoveMarker(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MarkersTbl, MarkerID + " = ?", new String[] {ID});
    }

    public void RemoverMarkerAndReminders(String ID){
        RemoveMarker(ID);
        DeleteAllMarkerReminders(ID);
    }

    public String GetMarkerPos (String ID){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MarkersTbl, new String[] {LongLatCol}, MarkerID + " = ?", new String[]{ID}, null, null, null);
        cursor.moveToFirst();

        String Position = cursor.getString(0);

        return  Position;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

}
