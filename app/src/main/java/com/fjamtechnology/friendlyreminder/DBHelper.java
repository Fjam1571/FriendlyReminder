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

class DBHelper extends SQLiteOpenHelper {

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
            " `Completed` INTEGER DEFAULT 0, " +
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
    private static final String ReminderIDCol = "RemindersID", MarkersIDCol = "MarkersID", ReminderTextCol = "Reminder", ReminderCompleted = "Completed";
    private static final String MarkerNameCol = "MarkerName", LongLatCol = "LongLat", MarkerID ="MarkersID";

    DBHelper(Context context){
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

    String GetUserID(String Username){
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

    /**
     * Method that is run when a user is changing their password
     * @param Pass the users password
     * @param UserID the users id
     */
     void ChangePass(String Pass, String UserID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(PasswordCol, Pass);

        db.update(UserTbl,values, UserIDCol + " = ?", new String[]{UserID});
    }

    /**
     * method run for a user to change their email
     * @param Email - the users new email
     * @param UserID - the users userID
     * @return ID - the user id
     */
    long ChangeEmail(String Email, String UserID){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(UserTbl, new String[] {EmailCol}, EmailCol + " = ?", new String[]{Email}, null, null, null);

        int AlreadyInSystem = cursor.getCount();
        cursor.close();
        long id;

        if(AlreadyInSystem > 0){
            id = -1;
            return id;
        }else{
            ContentValues values = new ContentValues();

            values.put(EmailCol, Email);

            id = db.update(UserTbl,values, UserIDCol + " = ?", new String[]{UserID});

            return id;
        }


    }

    //////// Password Encryption BCrypt ///////////////////////////////////////////////////////////////

    /**
     * password hasing algorithm using the Bcrypt library
     * @param Password  users password to be hashed
     * @return hash
     */
    String BCrypt(final String Password) {
        String hash = BCrypt.hashpw(Password, BCrypt.gensalt());
        return hash;
    }

    /**
     * verifys that the entered password matches the hashed password
     * @param Password users entered password
     * @param PasswordVerif hash in database
     * @return
     */
    Boolean VerifyPass(final String Password, final String PasswordVerif){
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
    String Verification(final String Username){
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
     * verify the users password
     * @param UserID - the users id
     * @return the pass
     */
    String VerifyUserPass(String UserID){
        String Pass = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query
                (UserTbl, new String[] {PasswordCol}, UserIDCol + " = ?", new String[]{UserID}, null, null, null);

        cursor.moveToFirst();

        if(cursor.getCount() <= 0){
            cursor.close();

            return Pass;
        }else{
            Pass = cursor.getString(0);

            return Pass;
        }
    }

    /**
     * method that gets all LatLng locations for the user
     * @return cursor holding locations
     */
    Cursor getAllLocations(String UserID){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MarkersTbl, new String[] {MarkerID, MarkerNameCol, LongLatCol}, UserIDCol + " = ?", new String[]{UserID}, null, null, null);

        return cursor;
    }

    /**
     * returns all reminders from the database through a cursor
     * @param ID - the users id
     * @return  cursor of reminders
     */
    public Cursor getAllReminders(String ID){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(RemindersTbl, new String[] {ReminderIDCol, ReminderTextCol, ReminderCompleted}, MarkersIDCol+ " = ?", new String[]{ID}, null, null, null);

        return cursor;
    }

    /**
     * method that allows a user to set a reminder as being completed in the database
     * @param ID - the reminder id
     * @param Completed  - the completed value
     */
    public void SetReminderCompletion (String ID, int Completed){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ReminderCompleted, Completed);

        db.update(RemindersTbl,values, ReminderIDCol + " = ?", new String[]{ID});
    }

    /**
     * method that allows a user to delete a reminder from the database
     * @param ID - the reminder id
     */
    public void DeleteReminder(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RemindersTbl, ReminderIDCol + " = ?", new String[] {ID});
    }

    /***
     * change the value of a reminder to another
     * @param ID = reminder id
     * @param Text - the new reminders text
     */
    public void EditReminder(String ID, String Text){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ReminderTextCol, Text);

        db.update(RemindersTbl,values, ReminderIDCol + " = ?", new String[]{ID});
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

    /**
     * Delete all reminders for a given marker
     * @param ID - the markers id
     */
    public void DeleteAllMarkerReminders(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RemindersTbl, MarkerID + " = ?", new String[] {ID});
    }

    /**
     * Add a new reminder for a marker in database
     * @param MarkerID - the marker id the reminder is going to
     * @param ReminderText - the text of the reminder
     */
    public void AddNewReminder(String MarkerID, String ReminderText){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MarkersIDCol, MarkerID);
        values.put(ReminderTextCol, ReminderText);

        db.insert(RemindersTbl, null, values);
    }

    /**
     * returns the id of a marker
     * @param LatLong - the latLong of the marker
     * @return - the id value
     */
    int ReturnMarkerID(String LatLong){
        int i;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MarkersTbl, new String[] {MarkerID}, LongLatCol + " = ?", new String[]{LatLong}, null, null, null);
        cursor.moveToFirst();

        i = cursor.getInt(0);

        return i;
    }

    /**
     * removing a marker form the database
     * @param ID - id of the marker
     */
    private void RemoveMarker(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MarkersTbl, MarkerID + " = ?", new String[] {ID});
    }

    /**
     * remove a marker and all its associated rmeinders
     * @param ID id of the marker
     */
    void RemoverMarkerAndReminders(String ID){
        RemoveMarker(ID);
        DeleteAllMarkerReminders(ID);
    }

    /**
     * returns the postion of a marker
     * @param  - id of the marker
     * @return the position of the marker (latlng)
     */
    String GetMarkerPos (String ID){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MarkersTbl, new String[] {LongLatCol}, MarkerID + " = ?", new String[]{ID}, null, null, null);
        cursor.moveToFirst();

        String Position = cursor.getString(0);

        return  Position;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

}
