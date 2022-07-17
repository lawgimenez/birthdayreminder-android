package law.android.BirthdayReminder;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GreetEmail extends Activity {
	
	private static final int PICKER_RESULT = 10;
	EditText emailAddress;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_greet);
		
		final EditText emailAddress = (EditText) findViewById(R.id.greet_email);
		final EditText emailSubject = (EditText) findViewById(R.id.greet_subject);
		final EditText emailBody = (EditText) findViewById(R.id.greet_body);
		Button sendEmail = (Button) findViewById(R.id.email_send);
		sendEmail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String emailAdd = emailAddress.getText().toString();
				String emailSubj = emailSubject.getText().toString();
				String emailBod = emailBody.getText().toString();
				
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdd});
				intent.putExtra(Intent.EXTRA_SUBJECT, emailSubj);
				intent.putExtra(Intent.EXTRA_TEXT, emailBod);
				//startActivity(Intent.createChooser(intent, "Choose app to send email."));
				startActivity(intent);
			}
		});
		
		launchContactPicker();		
	}
	public void launchContactPicker() {
		Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactIntent, PICKER_RESULT);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case PICKER_RESULT:
				Cursor cursor = null;
				String email = "";
				try {
					Uri  result = data.getData();
					Log.v("Intent data result", "Got a result: " + result.toString());
				
					//get the contact id from the Uri
					String id = result.getLastPathSegment();
				
					//query for everything email
					cursor = getContentResolver().query(Email.CONTENT_URI, null, Email.CONTACT_ID + "=?", new String[]{id}, null);
					int emailId = cursor.getColumnIndex(Email.DATA);
				
					if(cursor.moveToFirst()) {
						email = cursor.getString(emailId);
						Log.v("Get email", email);
					}
					else
					{
						Log.w("Email no results", "No results");
					}
				} catch (Exception e) {
					Log.e("Intent data fail", "Failed to get email data", e);
				} finally {
					if(cursor != null) {
						cursor.close();
					}
				}
				emailAddress = (EditText) findViewById(R.id.greet_email);
				emailAddress.setText(email);
				if(email.length() == 0) {
					Toast.makeText(this, "No email found for subject", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			
		}
		else
		{
			Log.w("Debug Activity", "Warning: activity result not ok.");
		}
	}
}
