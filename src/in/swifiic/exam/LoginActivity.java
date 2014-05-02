package in.swifiic.exam;


import java.io.File;

import in.swifiic.examapp.R;
import in.swifiic.examapp.ExtractAllFiles;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

/**
 * @author aniket
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@SuppressWarnings("unused")
public class LoginActivity extends Activity {

	/*
	 * The default IdNo to populate the IdNo field with.
	 */
//	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		// Hide the status bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_login);
		setupActionBar();

		// Set up the login form.
//		mIdNo = getIntent().getStringExtra(EXTRA_EMAIL);
		mIdNoView = (EditText) findViewById(R.id.usrId);
		mIdNoView.setText(mIdNo);
		mCodeView = (EditText) findViewById(R.id.courseCode);
		mCodeView.setText(mCode);
		mPasswordView = (EditText) findViewById(R.id.password);
		
		if(getIntent().hasExtra("course")) {
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
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
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
			// password entered is the filename of the test file
			path = Environment.getExternalStorageDirectory() + "/Exam/";
			// checks whether the file exists, if yes, extracts the contents to
			// a folder else throws incorrect p/w if not
			// found
			File file = new File(path + mCode + ".zip");
			if (file.exists()||mCode.equals(getIntent().getStringExtra("course"))) {
				ExtractAllFiles ef = new ExtractAllFiles(path, mPassword, mCode);

			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				focusView = mPasswordView;
				cancel = true;
			}

		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			String teacherName = getIntent().getStringExtra("teacher");
			Intent intent = new Intent(LoginActivity.this, Questions.class);
			intent.putExtra("teacher", teacherName);
			intent.putExtra("path", path);
			intent.putExtra("crsCode", mCode);
			intent.putExtra("idNo", mIdNo);
			startActivity(intent);
			finish();
		}
	}
}
