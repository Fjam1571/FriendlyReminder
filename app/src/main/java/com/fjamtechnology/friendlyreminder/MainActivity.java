package com.fjamtechnology.friendlyreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// Open Register page ///
        Button btn = (Button)findViewById(R.id.Register);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
                
            }
        });
        //////////////////////////

        Button btn2 = (Button)findViewById(R.id.SignIn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

    }

    public void SignIn (){

        EditText UsernameText = (EditText)findViewById(R.id.Username);
        String Username = UsernameText.getText().toString();

        EditText PasswordText = (EditText)findViewById(R.id.Password);
        String Password = PasswordText.getText().toString();



    }



}
