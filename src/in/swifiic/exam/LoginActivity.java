package in.swifiic.exam;

import in.swifiic.examapp.ExtractAllFiles;
import in.swifiic.examapp.R;
import in.swifiic.examapp.SettingsActivity;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user
 * 
 * @author aniket
 */
@SuppressWarnings("unused")
public class LoginActivity extends Activity {

	// Values for IdNo and password at the time of the login attempt.
	private String mIdNo;
	private String mCode;
	private String mPassword;

	// UI references.
	private EditText mIdNoView;
	private EditText mCodeView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	// helper for getting test data from studentDB
	StudentTestDB helper;
	
	String teacherName;
	String fileName;
	String duration;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		setupActionBar();

		// set up the DB
		helper = new StudentTestDB(this);
		helper.getWritableDatabase();
		// Set up the login form.
		mIdNoView = (EditText) findViewById(R.id.usrId);
		mIdNoView.setText(mIdNo);
		mCodeView = (EditText) findViewById(R.id.courseCode);
		mCodeView.setText(mCode);
		mPasswordView = (EditText) findViewById(R.id.password);

		if (getIntent().hasExtra("course")) {
			mCodeView.setText(getIntent().getStringExtra("course"));
		}

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		if (doesDatabaseExist(getApplication(), StudentTestDB.DB_NAME)) {
			Toast.makeText(getApplicationContext(), "Student DB exists",
					Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(getApplicationContext(),
					"Student DB does not exist", Toast.LENGTH_LONG).show();
	}

	/**
	 * Checks if DB exists for testing only - remove in final version
	 */
	private static boolean doesDatabaseExist(ContextWrapper context,
			String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		return dbFile.exists();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent selectedSettings = new Intent(this, SettingsActivity.class);
			startActivity(selectedSettings);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid IdNo, missing fields, etc.), the errors
	 * are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mIdNoView.setError(null);
		mCodeView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mIdNo = mIdNoView.getText().toString();
		mCode = mCodeView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;
		String path = null;

		// Check for a valid IdNo.
		if (TextUtils.isEmpty(mIdNo)) {
			mIdNoView.setError(getString(R.string.error_field_required));
			focusView = mIdNoView;
			cancel = true;
		}

		// check for a non-empty course code
		if (TextUtils.isEmpty(mCode)) {
			mCodeView.setError(getString(R.string.error_field_required));
			focusView = mCodeView;
			cancel = true;
		}

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		} else {
			// password entered is the password of the compressed test file
			StudentTestDB db = new StudentTestDB(this);
			Cursor testData = db.getTestDataCursor(mCode);

			// TODO If a test is already attempted, prevent it from the solution
			// being resent -- check status column in DB and give a dialog
			// box/set a label along
			// with questions notifying the student
			// TODO Check test day/time before allowing access to a test

			// TODO Set test duration from DB
			
			fileName = mCode;
			int status;
			if (!testData.moveToFirst()) { // if cursor is null, then that test does
									// not exist in database.
				Toast.makeText(
						getApplicationContext(),
						"Test \"" + mCode
								+ "\" does not exist in the database.",
						Toast.LENGTH_LONG).show();
				cancel = true;

			} else {
				teacherName = testData.getString(2);
				duration = testData.getString(5);
				fileName = testData.getString(6);
				status = testData.getInt(7);
				path = Environment.getExternalStorageDirectory() + "/Exam/";

				// checks whether the file exists, if yes, extracts the contents
				// to
				// a folder else throws incorrect p/w if not
				// found
				File file = new File(path + fileName + ".zip");
				ExtractAllFiles ef = new ExtractAllFiles(path, mPassword,
						fileName);
				boolean ext = ef.extract();
				if (!ext) {
					mPasswordView
							.setError(getString(R.string.error_incorrect_password));
					focusView = mPasswordView;
					cancel = true;
				}
			}
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			Intent intent = new Intent(LoginActivity.this, Questions.class);
			intent.putExtra("teacher", teacherName);
			intent.putExtra("path", path);
			intent.putExtra("fileName", fileName);
			intent.putExtra("crsCode", mCode);
			intent.putExtra("idNo", mIdNo);
			intent.putExtra("duration", duration);
			startActivity(intent);
			finish();
		}
	}
	/*
	 * public void onBackPressed() {
	 * android.os.Process.killProcess(android.os.Process.myPid());
	 * System.exit(1); }
	 */
}
