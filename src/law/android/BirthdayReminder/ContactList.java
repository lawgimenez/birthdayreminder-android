package law.android.BirthdayReminder;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ContactList extends ListActivity {

	protected ListView cList;
	protected ListAdapter adapter;
	protected Cursor cursor;
	protected DBAdapter db = new DBAdapter(this);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.list);
		cList = (ListView) findViewById(R.id.listviewcontacts);
		
		db.open();
		cursor = db.getAllContacts();
		adapter = new SimpleCursorAdapter(this, R.layout.contact_list, cursor, new String[]{"firstname", "lastname", "birthdate"}, new int[]{R.id.fName, R.id.lName, R.id.bDate});
		setListAdapter(adapter);
		
		db.close();
	}
}
