package in.swifiic.exam;

/**
 * @author aniket
 *
 */

//import in.swifiic.examApp.R;

import in.swifiic.android.app.lib.Helper;
import in.swifiic.android.app.lib.ui.SwifiicActivity;
import in.swifiic.android.app.lib.xml.Action;
import in.swifiic.examapp.R;
import in.swifiic.examapp.Constants;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class SendSoln extends SwifiicActivity {


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

		Button bSend = (Button) findViewById(R.id.sendButton);

		bSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String path = getIntent().getStringExtra("path");
				String fName = getIntent().getStringExtra("fName");
				Log.d("SubmitCopy", "Sending file :" + path + fName + ".zip");
				Action act = new Action("SubmitCopy", Constants.aeCtx);
				act.addFile(Helper.fileToB64String(path + fName + ".zip"));
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				
				// TODO when we got the paper - this should have been saved for
				// the subject and paper
				String teacher = getIntent().getStringExtra("teacher");
				act.addArgument("toTeacher", "aniket2");
				
				String course = getIntent().getStringExtra("course");
				act.addArgument("course", course);
				String studentName = getIntent().getStringExtra("fromStudent");
				act.addArgument("fromStudent", studentName);				

				// Loading hub address from preferences
				String hubAddress = sharedPref.getString("hub_address", "");
				hubAddress = Constants.hubAddress;

				// TODO - Need to convert user name to userId for uniqueness
				Helper.sendAction(act, hubAddress + Constants.hubEndpoint, v.getContext());

				Toast.makeText(getApplicationContext(),
						"Successfully Submitted", Toast.LENGTH_SHORT).show();
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


}