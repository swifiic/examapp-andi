package in.swifiic.examapp;

/**
 * @author aniket
 *
 */

import java.io.File;

import in.swifiic.android.app.lib.ui.SwifiicActivity;
import in.swifiic.examapp.R;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.View.OnClickListener;

public class MainScreen extends SwifiicActivity {
	final static String TAG = "MainScreen";
	boolean serviceStarted = false;

	SharedPreferences sharedPreferences;
	public static final String examappPREFERENCES = "examappPrefs";
	public static final String ROLE = "roleKey";

	public MainScreen() {
		super();
		// This is a must for all applications - hook to get notification from
		// GenericService
		@SuppressWarnings("unused")
		BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.hasExtra("notification")) {
					String payload = intent.getStringExtra("notification");

					Log.d(TAG, "Doing Nothing with incoming message: "
							+ payload);
					// Notification notif = Helper.parseNotification(payload);
				} else {
					Log.d(TAG,
							"Broadcast Receiver ignoring message - no notification found");
				}
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		File folderPath = new File(Environment.getExternalStorageDirectory()
				+ "/Exam/");
		if (!folderPath.exists())
			folderPath.mkdir();

		sharedPreferences = getSharedPreferences(examappPREFERENCES,
				Context.MODE_PRIVATE);

		if (sharedPreferences.contains(ROLE)) {
			String role = sharedPreferences.getString(ROLE, "");
			if (role != null) {
				startRoleActivity(role);
			}
		} else { // called when app is launched for the first time
			setContentView(R.layout.activity_main_screen);

			final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
			final Button saveBtn = (Button) findViewById(R.id.saveButton);

			final Editor prefEditor = sharedPreferences.edit();

			saveBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int roleId = radioGroup.getCheckedRadioButtonId();
					if (roleId == -1) {
						// no role is selected before clicking on save, so we
						// give an alert
						AlertDialog alertDialog;
						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainScreen.this);
						builder.setTitle("Alert Dialog");
						builder.setMessage("You have to select a role to proceed");
						builder.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
						alertDialog = builder.create();
						alertDialog.show();
					} else {
						int selOption = radioGroup.getCheckedRadioButtonId();
						RadioButton rBtn = (RadioButton) findViewById(selOption);
						String role = rBtn.getText().toString();
						prefEditor.putString(ROLE, role);
						prefEditor.commit();
						startRoleActivity(role);
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;

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
	
	void startRoleActivity(String role) {
		if (role.equals("Teacher")) {
			Intent intent = new Intent(MainScreen.this,
					in.swifiic.teacher.TeacherActivity.class);
			startActivity(intent);

		} else if (role.equals("Student")) {
			Intent intent = new Intent(MainScreen.this,
					in.swifiic.exam.LoginActivity.class);
			startActivity(intent);
		}
	}
	
}