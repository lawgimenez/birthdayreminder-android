package law.android.BirthdayReminder;



import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

public class BirthdayReminderActivity extends Activity {
	String month, day, year, strDate;
	Calendar ci = Calendar.getInstance();
	final DBAdapter db = new DBAdapter(this);
	protected ListAdapter adapter;
	private DatePicker dp;
	private EditText txtFname;
	private EditText txtLname;
	//private static final int CONTACT_PICKER_RESULT = 10;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        dp = (DatePicker) findViewById(R.id.birthdatePicker);
        txtFname = (EditText) findViewById(R.id.firstname);
        txtLname = (EditText) findViewById(R.id.lastname);
        //set hint for EditText widgets when empty
        txtFname.setHint("First Name");
        txtLname.setHint("Last Name");
        //onCreate check for birthdays
        
        showDialog(0);
       
        Button btnSave = (Button) findViewById(R.id.btnSave);
        //dp.init(year, monthOfYear, dayOfMonth, onDateChangedListener)
        //save contacts to database
        btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if(txtFname.getText().toString().equals(""))
				{
					Toast.makeText(getBaseContext(), "First name cannot be left blank.", Toast.LENGTH_LONG).show();
				}
				else
				{
					//get the date
			        month = (dp.getMonth() + 1) + "";
			        day = dp.getDayOfMonth() + "";
			        year = dp.getYear() + "";
			        strDate = month + "/" + day + "/" + year + " ";
			        //add values to database
			        db.open();
			        @SuppressWarnings("unused")
					long id;
			        id = db.insertContact(txtFname.getText().toString(), txtLname.getText().toString(), strDate);
			        db.close();
			        Toast.makeText(getBaseContext(), txtFname.getText().toString() + " 's birthdate saved.", Toast.LENGTH_SHORT).show();
			        reset();
				}
			}
		}); 
        //view contacts in database
        Button view = (Button) findViewById(R.id.btnView);
        view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DisplayContact();
				startActivity(new Intent("law.android.ContactList"));
				
			}
		});
        Button reset = (Button) findViewById(R.id.btnReset);
        reset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reset();
			}
		});
    }//end of onCreate 
    //display contacts
    public void DisplayContact()
    {
    	db.open();
    	Cursor c = db.getAllContacts();
    	if(c.moveToFirst())
    	{
    		do {
    			Toast.makeText(this, "id: " + c.getString(0) + "\n" + "First Name: " + c.getString(1) + "\n"
    	    			+ "Last Name: " + c.getString(2) + "\n" + "Birth Date: " + c.getString(3), Toast.LENGTH_LONG).show();
    		} while(c.moveToNext());
    	}
    	
    	//Intent intent = new Intent(this, ContactDetails.class);
    	//intent.putExtra("CONTACT_ID", c.getInt(c.getColumnIndex("_id")));
    	//startActivity(intent);
    }
    //check for birthdays
    public Cursor CheckBirthdays(Cursor c)
    {
    	
    	return c;
    }
    private void CreateMenu(Menu menu)
    {
    	MenuItem menuCheck = menu.add(0, 0, 0, "About");
    	{
    		menuCheck.setAlphabeticShortcut('c');
    		menuCheck.setIcon(R.drawable.icon);
    	}
    	
    }
    private boolean MenuChoice(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case 0:
    		showDialog(0);  
    		//startActivity(new Intent("law.android.BirthdayList"));
    		return true;
		
    	}
    	return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
    	CreateMenu(menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	return MenuChoice(item);
    }
    public void reset() {
    	dp.init(ci.get(Calendar.YEAR), ci.get(Calendar.MONTH), ci.get(Calendar.DAY_OF_MONTH), null);
		txtFname.setText("");
		txtLname.setText("");
    }
    @Override
    protected Dialog onCreateDialog(int id) {
    	boolean flag = false;
    	int ctr = 0;
    	Integer curM, curD;
    	curM = ci.get(Calendar.MONTH) + 1;
    	curD = ci.get(Calendar.DAY_OF_MONTH);
    	db.open();
    	Cursor c = db.getAllContacts();
    	switch(id) {
    	case 0:
    		if(c.moveToFirst()){
    			do {
    				String[] arrTemp = c.getString(3).split("/");
    				if(curM.toString().equalsIgnoreCase(arrTemp[0]) && curD.toString().equalsIgnoreCase(arrTemp[1])) {
        	    		flag = true;
        	    		ctr = ctr + 1; //increment notification id
        	    		displayNotification(c.getString(1), ctr);
        	    		Toast.makeText(getBaseContext(), "It's " + c.getString(1) + "'s birthday today.", Toast.LENGTH_LONG).show();
    				}
    			} while(c.moveToNext());
    		}
    		if(flag == false) {
    			return new AlertDialog.Builder(this)
    			.setMessage("No one is having a birthday today.")
    			.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				})
    			.create();
    		}
    		
    	case 1:
    		return new AlertDialog.Builder(this)
    		.setMessage("Birthday Reminder. Version 1.1")
    		.setNegativeButton("Close", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).create();
    		
        }
    	db.close();
    	return null;
    }
    
    protected void displayNotification(String contact, int notifId) {
    	Intent i = new Intent("law.android.GreetEmail");
    	i.putExtra("notificationID", notifId);
    	PendingIntent pending = PendingIntent.getActivity(this, 0, i, 0);
    	NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	
    	Notification notif = new Notification(R.drawable.icon, "Happy Birthday to " + contact, System.currentTimeMillis());
    	CharSequence from = "Birthday Reminder";
    	CharSequence msg = "Greet " + contact + " a happy birthday.";
    	
    	notif.setLatestEventInfo(this, from, msg, pending);
    	
    	nm.notify(notifId, notif);
    } 
    
}