package com.fjamtechnology.friendlyreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    boolean EmptyUsername = true;
    boolean EmptyPassword = true;

    final String EmptyUsernameSTR = "Please Enter Your Username";
    final String EmptyPasswordSTR = "Pleaes Enter Your Password";
    final String EmptyUsernamePassword = "Please Make Sure You Have Filled In Credentials";
    final String NoUserFound = "The User Is Not Registerd Please Create An Account";
    final String PassNotCorrect = "Please Try Entering Password Again";

    /**
     * code that runs upon a new instance creation of the app
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// Open Register page ///
        Button RegisterBtn = (Button)findViewById(R.id.Register);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
                
            }
        });
        //////////////////////////

        Button SignInBtn = (Button)findViewById(R.id.SignIn);
        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

    }

    /**
     * method for signing a user in
     */
    public void SignIn (){

        String Pass;
        boolean SamePass;
        DBHelper helper = new DBHelper(this);

        EditText UsernameText = (EditText)findViewById(R.id.Username);
        String Username = UsernameText.getText().toString();
        Username = Username.replace(" ", "");

        EditText PasswordText = (EditText)findViewById(R.id.Password);
        String Password = PasswordText.getText().toString();

        EmptyUsername = Username.isEmpty();
        EmptyPassword = Password.isEmpty();

        Intent intent = new Intent(MainActivity.this, ReminderMap.class);
        intent.putExtra("126516516513246", "1");
        startActivity(intent);

        /*if(EmptyUsername == true && EmptyPassword == true){
            Toast.makeText(getApplicationContext(), EmptyUsernamePassword, Toast.LENGTH_LONG).show();
        }else if(EmptyUsername == true){
            Toast.makeText(getApplicationContext(), EmptyUsernameSTR, Toast.LENGTH_LONG).show();
        }else if(EmptyPassword == true){
            Toast.makeText(getApplicationContext(), EmptyPasswordSTR, Toast.LENGTH_LONG).show();
        }else if(EmptyUsername == false && EmptyPassword == false){

            Pass = helper.Verification(Username);

            if(Pass == null){
                SamePass = false;
            }else {
                SamePass = helper.VerifyPass(Password, Pass);
            }

            if(Pass == null){
                Toast.makeText(getApplicationContext(), NoUserFound, Toast.LENGTH_LONG).show();
            }else if(SamePass == true){

                String UserID = helper.GetUserID(Username);

                Toast.makeText(getApplicationContext(), UserID, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, ReminderMap.class);
                intent.putExtra("126516516513246", UserID);
                startActivity(intent);

                PasswordText.setText("");
            }else if(SamePass == false){
                Toast.makeText(getApplicationContext(), PassNotCorrect, Toast.LENGTH_LONG).show();
                PasswordText.setText("");
            }

        }*/
    }



}
