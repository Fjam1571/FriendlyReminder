package com.fjamtechnology.friendlyreminder;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //test

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// Open Register page ///
        btnRegister = (Button)findViewById(R.id.Register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));

            }
        });
        //////////////////////////

        Button btn2 = (Button)findViewById(R.id.SignIn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

    }

    public void SignIn (){
        String Userpass;

        EditText UsernameText = (EditText)findViewById(R.id.Username);
        String Username = UsernameText.getText().toString();

        EditText PasswordText = (EditText)findViewById(R.id.Password);
        String Password = PasswordText.getText().toString();

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

    }



}
