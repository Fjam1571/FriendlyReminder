package com.fjamtechnology.friendlyreminder;

import android.content.Intent;
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
    boolean ValidPass;
    boolean PassMatch;
    boolean UserEmpty;

    final String SuccesfulReg = "Registration Complete You Can Login Using Your Credentials";
    final String UnSuccesfulReg = "The Username Is Already Taken Or The Email Has Been Used To Register Already";
    final String EmptyUsername = "Please Enter A Username";
    final String InvalidEmail = "Please Enter A Valid Email";
    final String InvalidPass = "Please Enter A Valid Password That Meets The Displayed Requirements";
    final String PassDontMatch = "The Passwords Don't Match";
    final String NoEntries = "Please Fill Form To Register";

    /**
     * method run when activity is created
     * @param savedInstanceState
     */
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Register.this, MainActivity.class));
        finish();
    }

    /**
     * code to insert a new user into the database
     */
    public void InsertUser() {
        EditText UsernameText = (EditText) findViewById(R.id.RegUsername);
        String Username = UsernameText.getText().toString();
        Username = Username.replace(" ", "");

        EditText EmailText = (EditText) findViewById(R.id.RegEmail);
        String Email = EmailText.getText().toString();
        Email = Email.toLowerCase();

        EditText PasswordText = (EditText) findViewById(R.id.RegPassword);
        String Password = PasswordText.getText().toString();

        EditText PasswordVerifText = (EditText) findViewById(R.id.RegPasswordVerif);
        String PasswordVerif = PasswordVerifText.getText().toString();

        // Checking Paramaters
        ValidEmail = isEmailValid(Email);
        ValidPass = isPassValid(Password);
        PassMatch = PasswordMatch(Password, PasswordVerif);
        UserEmpty = Username.isEmpty();

        if (ValidEmail == true && ValidPass == true && PassMatch == true && UserEmpty == false) {

            DBHelper db = new DBHelper(this);

            Response = db.RegistrationPage(Username, Password, Email);

            if (Response < 0) {
                Toast.makeText(getApplicationContext(), UnSuccesfulReg, Toast.LENGTH_LONG).show();
            } else if (Response > 0) {
         ///////////////////////////////////////////////////////////////////////////////////////////
                Toast.makeText(this.getApplicationContext(), SuccesfulReg, Toast.LENGTH_LONG).show();

                final Intent intent = new Intent(this, MainActivity.class);

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(Toast.LENGTH_LONG);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
            }
         ///////////////////////////////////////////////////////////////////////////////////////////
        }else if(UserEmpty == true && ValidEmail == false && PassMatch == false){
            Toast.makeText(getApplicationContext(), NoEntries, Toast.LENGTH_LONG).show();
        }else if(UserEmpty == true) {
            Toast.makeText(getApplicationContext(), EmptyUsername, Toast.LENGTH_LONG).show();
        }else if(ValidEmail == false){
            Toast.makeText(getApplicationContext(), InvalidEmail, Toast.LENGTH_LONG).show();
        }else if(ValidPass == false){
            Toast.makeText(getApplicationContext(), InvalidPass, Toast.LENGTH_LONG).show();
        }else if(PassMatch == false){
            Toast.makeText(getApplicationContext(), PassDontMatch, Toast.LENGTH_LONG).show();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *  mehtod that confrms that the entered email is valid in format
     * @param email  - users entered email
     * @return if its valid
     */
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

    /**
     * method that confirms if the entered password is valid
     * @param Pass
     * @return if the password matches approiate password format
     */
    public static boolean isPassValid(String Pass) {
        boolean isValid = false;

        String expression1 = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{12,}$";
        CharSequence inputStr = Pass;

        Pattern pattern1 = Pattern.compile(expression1, Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(inputStr);
        if (matcher1.matches()) {
            isValid = true;
        }
        return isValid;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * mehtod that checks if the enetered password matches the password that is stored in the database
     * @param Password - users entered password in field one
     * @param PasswordVerif - users enetered password in field to
     * @return if tthe password in field one matches the same in field 2
     */
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
