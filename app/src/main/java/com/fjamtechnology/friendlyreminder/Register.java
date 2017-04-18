package com.fjamtechnology.friendlyreminder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    long Response;
    boolean ValidEmail;
    boolean PassMatch;
    boolean UserEmpty;

    final String SuccesfulReg = "Registration Complete You Can Login Using Your Credentials";
    final String UnSuccesfulReg = "The Username Is Already Taken Or The Email Has Been Used To Register Already";
    final String EmptyUsername = "Please Enter A Username";
    final String InvalidEmail = "Please Enter A Valid Email";
    final String PassDontMatch = "The Passwords Don't Match";
    final String NoEntries = "Please Fill Form To Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /// Register ///
        Button btn = (Button)findViewById(R.id.RegisterBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertUser();

            }
        });
        ////////////////


    }

    public void InsertUser() {
        EditText UsernameText = (EditText) findViewById(R.id.RegUsername);
        String Username = UsernameText.getText().toString();

        EditText EmailText = (EditText) findViewById(R.id.RegEmail);
        String Email = EmailText.getText().toString();
        Email = Email.toLowerCase();

        EditText PasswordText = (EditText) findViewById(R.id.RegPassword);
        String Password = PasswordText.getText().toString();

        EditText PasswordVerifText = (EditText) findViewById(R.id.RegPasswordVerif);
        String PasswordVerif = PasswordVerifText.getText().toString();

        // Checking Paramaters
        ValidEmail = isEmailValid(Email);
        PassMatch = PasswordMatch(Password, PasswordVerif);
        UserEmpty = Username.isEmpty();

        if (ValidEmail == true && PassMatch == true && UserEmpty == false) {

            DBHelper db = new DBHelper(this);

            Response = db.RegistrationPage(Username, Password, Email);

            if (Response < 0) {
                Toast.makeText(getApplicationContext(), UnSuccesfulReg, Toast.LENGTH_LONG).show();
            } else if (Response > 0) {
                Toast.makeText(getApplicationContext(), SuccesfulReg, Toast.LENGTH_LONG).show();
            }

        }else if(UserEmpty == true && ValidEmail == false && PassMatch == false){
            Toast.makeText(getApplicationContext(), NoEntries, Toast.LENGTH_LONG).show();
        }else if(Username.isEmpty() == true) {
            Toast.makeText(getApplicationContext(), EmptyUsername, Toast.LENGTH_LONG).show();
        }else if(ValidEmail == false){
            Toast.makeText(getApplicationContext(), InvalidEmail, Toast.LENGTH_LONG).show();
        }else if(PassMatch == false){
            Toast.makeText(getApplicationContext(), PassDontMatch, Toast.LENGTH_LONG).show();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean PasswordMatch(String Password, String PasswordVerif){
        boolean PassMatch = false;

        if(Password.equals(PasswordVerif) == true){
            PassMatch = true;
        }else if(Password.equals(PasswordVerif) == false){
            PassMatch = false;
        }

        return PassMatch;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

}
