package in.swifiic.exam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StudentTestDB extends SQLiteOpenHelper {

	public static final String DB_NAME = "Student_Test_DB"; // name of db
	private static final int DB_VERSION = 1;
	private static final String COLUMN_ID = "id";
	private static final String TEST_NAME = "test_name";
	private static final String TEACHER_NAME = "teacher_name";
	private static final String TEST_DATE = "test_date";
	private static final String TEST_TIME = "test_time";
	private static final String TEST_DURATION = "test_duration";
	private static final String FILE_NAME = "file_name";
	private static final String STATUS = "status"; // boolean value:
													// attempted = 1
													// not attempted = 0

	public StudentTestDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String tableCreate = "create table if not exists " + DB_NAME
				+ " ( " + COLUMN_ID + " integer primary key autoincrement , "
				+ TEST_NAME + " text not null unique , " + TEACHER_NAME
				+ " text not null , " + TEST_DATE + " text not null , "
				+ TEST_TIME + " text not null , " + TEST_DURATION
				+ " text not null , " + FILE_NAME + " text not null , "
				+ STATUS + " int default 0)";

		db.execSQL(tableCreate);
		Log.d("DB_INFO", "Table created");
	}

	/**
	 * Insert a row into StudentTestDB, which stores available tests
	 * 
	 * @param testName
	 *            - String
	 * @param teacherName
	 *            - String
	 * @param testDate
	 *            - TODO get as extra from teacher
	 * @param testTime
	 *            - TODO get as extra from teacher
	 * @param testDur
	 *            - TODO get as extra from teacher
	 * @param fileName
	 *            - full path of the test file
	 * @param status
	 *            - integer value: 0 if test is not attempted, 1 if attempted or
	 *            if test is for practice
	 */
	public void insert(String testName, String teacherName, String testDate,
			String testTime, String testDur, String fileName, int status)
			throws SQLiteException {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues vals = new ContentValues();
		vals.put(TEST_NAME, testName);
		vals.put(TEACHER_NAME, teacherName);
		vals.put(TEST_DATE, testDate);
		vals.put(TEST_TIME, testTime);
		vals.put(TEST_DURATION, testDur);
		vals.put(FILE_NAME, fileName);
		vals.put(STATUS, status);
		db.insertOrThrow(DB_NAME, null, vals);
		Log.d("DB_INFO", "New row added");
		db.close();
	}

	/**
	 * Queries the Student DB with the given test name
	 * 
	 * @param testName
	 * @return Cursor with test details if a test with the given testName exists
	 */
	public Cursor getTestDataCursor(String testName) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor testData = db.query(DB_NAME, null, TEST_NAME + "=?",
				new String[] { testName }, null, null, null, null);
		// db.close();
		return testData;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.d("StudentDB_onUpgrade", "Entered upgrade with old, new : "
				+ oldVersion + ", " + newVersion);
	}
}
