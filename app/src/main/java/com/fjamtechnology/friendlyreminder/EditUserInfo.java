package com.fjamtechnology.friendlyreminder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditUserInfo extends AppCompatActivity {

    boolean UserPass;
    boolean ValidEmail;
    boolean ValidPass;
    boolean PassMatch;
    String UserID;

    DBHelper helper = new DBHelper(this);

    /**
     * creation method for instance
     * @param savedInstanceState - the instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UserID = getIntent().getExtras().getString("126516516513246");
        String UpdatedUserID = UserID;

        Button Email = (Button) findViewById(R.id.BtnEmail);
        Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText EmailText = (EditText) findViewById(R.id.Email);
                String Email = EmailText.getText().toString();
                Email = Email.toLowerCase();
                String Email2 = Email.replace(" ", "");

                EditText PasswordText = (EditText) findViewById(R.id.Password);
                String Password = PasswordText.getText().toString();
                String Password2 = Password.replace(" ", "");
                ValidEmail = isEmailValid(Email);

                if(Email.isEmpty() && Email2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Make Sure You Have Entered A New Email Adress", Toast.LENGTH_SHORT).show();
                }else if(Password.isEmpty() && Password2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Make Sure You Have Entered Your Password", Toast.LENGTH_SHORT).show();
                }else if(Email.isEmpty() && Email2.isEmpty() && Password.isEmpty() && Password2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Make Sure You Have Entered Your Password And Email", Toast.LENGTH_SHORT).show();
                }else if(ValidEmail == false){
                    EmailText.setText("");
                    PasswordText.setText("");
                    Toast.makeText(getApplicationContext(),"Please Make Sure You Have Entered A Valid Email", Toast.LENGTH_SHORT).show();
                }else if(Email.isEmpty() == false && Email2.isEmpty() == false && Password.isEmpty() == false && Password2.isEmpty() == false && ValidEmail == true){
                    long EmailInSystem = helper.ChangeEmail(Email, UpdatedUserID);

                    if(EmailInSystem < 0){
                        Toast.makeText(getApplicationContext(), "Email Already Exists In System Or You Have Entered The Same Email", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Your New Email Adress is = " + Email, Toast.LENGTH_SHORT).show();
                        EmailText.setText("");
                        PasswordText.setText("");
                    }

                }

            }
        });

        Button Pass = (Button) findViewById(R.id.ChangePass);
        Pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText OldPasswordText = (EditText) findViewById(R.id.OldPass);
                String OldPassword = OldPasswordText.getText().toString();
                String OldPassword2 = OldPassword.replace(" ", "");

                EditText PasswordText = (EditText) findViewById(R.id.NewPass);
                String Password = PasswordText.getText().toString();
                String Password2 = Password.replace(" ", "");

                EditText PasswordVerifText = (EditText) findViewById(R.id.NewPassVerify);
                String PasswordVerif = PasswordVerifText.getText().toString();
                String PasswordVerif2 = PasswordVerif.replace(" ", "");

                UserPass = VerifyUserPass(UpdatedUserID, OldPassword);
                ValidPass = isPassValid(Password);
                PassMatch = PasswordMatch(Password, PasswordVerif);

                if(OldPassword.isEmpty() && OldPassword2.isEmpty() || Password.isEmpty() && Password2.isEmpty() || PasswordVerif.isEmpty() && PasswordVerif2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Make Sure You Have Entered All Parameters To Change Password", Toast.LENGTH_LONG).show();
                }else if(OldPassword.isEmpty() && OldPassword2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Make Sure You Have Entered Your Old Password", Toast.LENGTH_SHORT).show();
                }else if(Password.isEmpty() && Password2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Make Sure You Have Entered A New Password", Toast.LENGTH_SHORT).show();
                }else if(PasswordVerif.isEmpty() && PasswordVerif2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Make Sure You Have Entered Your New Password In Verification", Toast.LENGTH_SHORT).show();
                }else if(UserPass == false){
                    Toast.makeText(getApplicationContext(), "Your Old Password Does Not Match Our Records, Please Try Again", Toast.LENGTH_LONG).show();
                    OldPasswordText.setText("");
                    PasswordText.setText("");
                    PasswordVerifText.setText("");
                }else if(ValidPass == false){
                    Toast.makeText(getApplicationContext(), "Your New Password Is Not Valid Make Sure Its Atleast 12 Characters and Includes Special Characters", Toast.LENGTH_LONG).show();
                    PasswordText.setText("");
                    PasswordVerifText.setText("");
                }else if(PassMatch == false){
                    Toast.makeText(getApplicationContext(), "New Password Does Not Match With Password Verification", Toast.LENGTH_LONG).show();
                    PasswordText.setText("");
                    PasswordVerifText.setText("");
                }else if(UserPass == true && ValidPass == true && PassMatch == true && OldPassword.isEmpty() == false && OldPassword2.isEmpty() == false
                        && Password.isEmpty() == false && Password2.isEmpty() == false && PasswordVerif.isEmpty() == false && PasswordVerif2.isEmpty() == false){
                    String HashedPass = helper.BCrypt(Password);
                    helper.ChangePass(HashedPass,UpdatedUserID);
                    Toast.makeText(getApplicationContext(), "You Have Successfully Changed Your Password", Toast.LENGTH_LONG).show();
                    OldPasswordText.setText("");
                    PasswordText.setText("");
                    PasswordVerifText.setText("");
                }

            }
        });

    }


    /**
     * Verifys the two passwords field match
     * @param UserID - the users id
     * @param Pass - the users enetered password
     * @return if they match
     */
    private boolean VerifyUserPass(String UserID, String Pass){
        Boolean PassMatch;
        String UserPass;
        UserPass = helper.VerifyUserPass(UserID);
        if(UserPass == null){
            PassMatch = false;
            return PassMatch;
        }else {
            PassMatch = helper.VerifyPass(Pass, UserPass);
            return PassMatch;
        }
    }

    /**
     * Checks if the users entered email is a valid email
     * @param email  - the enetered email
     * @return  if the email is valid
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
