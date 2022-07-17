package law.android.BirthdayReminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_FNAME = "firstname";
	public static final String KEY_LNAME = "lastname";
	public static final String KEY_BIRTHDATE = "birthdate";
	private static final String TAG = "DBAdapter";
	
	private static final String DATABASE_NAME = "BDAY";
	private static final String DATABASE_TABLE = "contacts";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "create table contacts (_id integer primary key autoincrement, "
		+ "firstname text not null, lastname text, birthdate text);";
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBAdapter(Context c)
	{
		this.context = c;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DATABASE_CREATE);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old date");
			db.execSQL("DROP TABLE IF EXISTS contacts");
			onCreate(db);
		}
	}//end of class DatabaseHelper
	
	//open the database
	public DBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	//close the database
	public void close()
	{
		DBHelper.close();
	}
	//insert contact into the database
	public long insertContact(String fname, String lname, String birthdate)
	{
		ContentValues initValues = new ContentValues();
		initValues.put(KEY_FNAME, fname);
		initValues.put(KEY_LNAME, lname);
		initValues.put(KEY_BIRTHDATE, birthdate);
		return db.insert(DATABASE_TABLE, null, initValues);
	}
	//delete a particular contact
	public boolean deleteContact(long rowId)
	{
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	//retrieves all the contacts
	public Cursor getAllContacts()
	{
		return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_FNAME, KEY_LNAME, KEY_BIRTHDATE}, null, null, null, null, null);
	}
	//retrieves a particular contact
	public Cursor getContact(long rowId) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_FNAME, KEY_LNAME, KEY_BIRTHDATE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	//update a contact
	public boolean updateContact(long rowId, String fname, String lname, String birthdate)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_FNAME, fname);
		args.put(KEY_LNAME, lname);
		args.put(KEY_BIRTHDATE, birthdate);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
