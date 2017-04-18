package com.fjamtechnology.friendlyreminder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /// Register ///
        Button btn = (Button)findViewById(R.id.Register);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertUser();
            }
        });
        ////////////////


    }

    public void InsertUser(){
        DBHelper helper = new DBHelper(getBaseContext());

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("Username", "admin");
        values.put("Password", "password1");

        long newRowID = db.insert("User", null, values);

    }

}
