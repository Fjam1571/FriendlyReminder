package com.fjamtechnology.friendlyreminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Reminders extends AppCompatActivity {

    String MarkerID, UserID, UpdatedUserID;
    DBHelper helper = new DBHelper(this);
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MarkerID = getIntent().getExtras().getString("165165165165166");
        String UpdatedMarkerID = MarkerID;
        UserID = getIntent().getExtras().getString("126516516513246");
        UpdatedUserID = UserID;

        PopulateListView(UpdatedMarkerID);

    }


    //// On Back Arrow Pressed //////
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(Reminders.this, ReminderMap.class);
        intent.putExtra("126516516513246", UpdatedUserID);
        startActivity(intent);
        finish();
        return true;
    }
    /////////////////////////////////

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Reminders.this, ReminderMap.class);
        intent.putExtra("126516516513246", UpdatedUserID);
        startActivity(intent);
        finish();
    }

    public void PopulateListView(String MarkerID){
        lv = (ListView) findViewById(R.id.LV);

        ////////////////////////////////////////////////////////////////////////////////////////////
        Cursor c;
        c = helper.getAllReminders(MarkerID);
        int NumbReminders = c.getCount();

        c.moveToFirst();
        String[] textString = new String[NumbReminders];
        int [] drawableIds = new int[NumbReminders];
        int [] RemindersID = new int[NumbReminders];
        int Completed [] = new int[NumbReminders];

        String[] textStringNoR = new String[1];
        int [] drawableIdsNoR = new int[1];
        int [] RemindersIDNoR = new int[1];

        if(NumbReminders > 0){

            for(int i = 0; i < NumbReminders; i++){
                RemindersID [i] = c.getInt(0);
                textString [i] = c.getString(1);
                Completed [i] = c.getInt(2);
                if(Completed [i] == 0){
                    drawableIds [i] = R.drawable.reminder_unchecked;
                }else{
                    drawableIds [i] = R.drawable.reminder_checked;
                }

                c.moveToNext();
            }
            c.close();
            CustomAdapter adapter = new CustomAdapter(this,  textString, drawableIds);
            lv.setAdapter(adapter);
        }else{
            RemindersIDNoR [0] = 0;
            textStringNoR [0] = "No Reminders Yet";
            drawableIdsNoR [0] = R.drawable.reminder_unchecked;

            CustomAdapter adapter = new CustomAdapter(this,  textStringNoR, drawableIdsNoR);
            lv.setAdapter(adapter);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        //// Click Listener On Items ////
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ID = "";

                DBHelper helper = new DBHelper(getApplicationContext());

                if(NumbReminders > 0){
                    if(Completed [position] == 0){
                        ID = String.valueOf(RemindersID [position]);
                        helper.SetReminderCompletion(ID, 1);
                        PopulateListView(MarkerID);
                    }else{
                        ID = String.valueOf(RemindersID [position]);
                        helper.SetReminderCompletion(ID, 0);
                        PopulateListView(MarkerID);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please Add A Reminder", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///////////////////////////////////////

        //// Long Item Click Listener //////
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(NumbReminders > 0){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Reminders.this);
                    alertDialog.setTitle("What Would You Like To Do With This Reminder");
                    alertDialog.setNegativeButton("Edit Reminder", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(Reminders.this);
                            alert.setTitle("Reminder Editor"); //Set Alert dialog title here
                            alert.setMessage("Please Enter Your New Reminder"); //Message here

                            // Set an EditText view to get user input
                            final EditText input1 = new EditText(Reminders.this);
                            alert.setView(input1);

                            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String NewText = input1.getEditableText().toString();
                                    String EmptyText = NewText.replace(" ", "");

                                    if(NewText.isEmpty() || EmptyText == ""){
                                        AlertDialog.Builder alert2 = new AlertDialog.Builder(Reminders.this);
                                        alert2.setTitle("Reminder Edit Was Left Blank"); //Set Alert dialog title here
                                        alert2.setMessage("Please Make Sure You Have Entered Your Changes For The Reminder");

                                        alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(),"Please Try Again",Toast.LENGTH_SHORT).show();
                                                onItemLongClick(parent, view, position,id);
                                            }
                                        });

                                        alert2.show();
                                    }else{
                                        String ID = "";
                                        ID = String.valueOf(RemindersID [position]);
                                        helper.EditReminder(ID, NewText);
                                        PopulateListView(MarkerID);
                                        Toast.makeText(getApplicationContext(),"Done", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }

                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alert.show();

                        }
                    });
                    alertDialog.setPositiveButton("Delete Reminder",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            /////////////////////////////////////////////////////////////////////////////
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Reminders.this);
                            alertDialog.setTitle("!!! Reminder Will Be Deleted Permanenlty, Continue !!!");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String ID = "";
                                    ID = String.valueOf(RemindersID [position]);
                                    helper.DeleteReminder(ID);
                                    PopulateListView(MarkerID);
                                    Toast.makeText(getApplicationContext(),"Deleted", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });

                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alertDialog.show();
                            ///////////////////////////////////////////////////////////////////////////////
                        }
                    });
                    alertDialog.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }else{
                    Toast.makeText(getApplicationContext(),"This Is Not A Reminder Cannot Edit", Toast.LENGTH_SHORT).show();
                }



                return true;
            }
        });
        /////////////////////////////////////

    }

}
