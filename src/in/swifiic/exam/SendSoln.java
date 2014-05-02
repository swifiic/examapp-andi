package in.swifiic.exam;

/**
 * @author aniket
 *
 */

//import in.swifiic.examApp.R;

import in.swifiic.android.app.lib.Helper;
import in.swifiic.android.app.lib.xml.Action;
import in.swifiic.examapp.R;
import in.swifiic.examapp.Constants;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class SendSoln extends Activity {

	private String teacher = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the status bar
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_send_soln);

		// get path and file name from bundle

		TextView tName = (TextView) findViewById(R.id.teacherName);
		Button bSend = (Button) findViewById(R.id.sendButton);

		// TODO when we got the paper - this should have been saved for
		// the subject and paper
		if (getIntent().hasExtra("teacher")) {
			teacher = getIntent().getStringExtra("teacher");
			tName.setText(teacher);
		}
		
		bSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String path = getIntent().getStringExtra("path");
				String fName = getIntent().getStringExtra("fName");
				Log.d("SubmitCopy", "Sending file :" + path + fName + ".zip");
				Action act = new Action("SubmitCopy", Constants.aeCtx);
				act.addFile(Helper.fileToB64String(path + fName + ".zip"));

				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				TextView tName = (TextView) findViewById(R.id.teacherName);
				teacher = tName.getText().toString();
				if(teacher==null||teacher.isEmpty()){
					teacher = addTeacherName();
					return;
				}
				act.addArgument("toTeacher", teacher);
				String course = getIntent().getStringExtra("course");
				act.addArgument("course", course);
				String studentName = getIntent().getStringExtra("fromStudent");
				act.addArgument("fromStudent", studentName);

				// Loading hub address from preferences
				String hubAddress = sharedPref.getString("hub_address", "");
				// hubAddress = Constants.hubAddress;

				// TODO - Need to convert user name to userId for uniqueness

				Helper.sendAction(act, hubAddress + Constants.hubEndpoint,
						v.getContext());

				Toast.makeText(getApplicationContext(),
						"Successfully Submitted", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(SendSoln.this,
						in.swifiic.examapp.MainScreen.class);
				startActivity(i);
				finish();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_soln, menu);
		return true;

	}

	private String addTeacherName() {
		final StringBuilder teacherName = new StringBuilder("");
		new AlertDialog.Builder(SendSoln.this).setTitle("Enter Teacher Name!")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();

		// Create the AlertDialog

		return teacherName.toString();
	}

}