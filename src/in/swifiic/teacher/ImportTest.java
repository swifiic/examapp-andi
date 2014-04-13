package in.swifiic.teacher;

import in.swifiic.android.app.lib.Helper;
import in.swifiic.android.app.lib.xml.Action;
import in.swifiic.exam.SendSoln;
import in.swifiic.examapp.R;
import in.swifiic.examapp.Constants;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ImportTest extends Activity {

	private static int FILE_SELECT_RESULT_CODE = 7733;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_test);

		Button bSelFile = (Button) findViewById(R.id.selectFile);
		Button bSelUser = (Button) findViewById(R.id.selectUsers);
		Button bSndFile = (Button) findViewById(R.id.sendFile);

		bSelFile.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// activity for importing test file
				Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
				fileIntent.setType("gagt/sdf");// (".zip application/zip");
				try {
					startActivityForResult(fileIntent, FILE_SELECT_RESULT_CODE);
				} catch (ActivityNotFoundException e) {
					Log.e("tag",
							"No activity can handle picking a file. Showing alternatives.");
				}
			}
		});

		bSelUser.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO invoke activity to select users from list view
			}

		});

		bSndFile.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO invoke activity to select users from list view
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.import_test, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Fix no activity available
		TextView tFilePath = (TextView) findViewById(R.id.filePath);
		if (data == null)
			return;
		if (requestCode == FILE_SELECT_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				String filePath = data.getData().getPath();
				// FilePath is path of file as a string
				tFilePath.setText(filePath);
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				Action act = new Action("SendQuestions", Constants.aeCtx);
				String dataStr = Helper.fileToB64String(filePath);
				act.setFileData(dataStr);
				// gets Teacher name from SUTA provider - TODO need to set student/teacher roles
				String fromTeacher = sharedPref.getString("my_identity", "UnknownUser");
				act.addArgument("fromTeacher", "aniket2"); 
				// XXX - should be from drop down list - similar to messenger - should have multi-select - New Activity is also fine
				act.addArgument("students", "aniket|abhishek|aniket2"); 
				//TODO add course name argument
				act.addArgument("course", filePath.substring(filePath.lastIndexOf('/')+1, filePath.lastIndexOf('.')));
				String hubAddress = Constants.hubAddress;
                
                Helper.sendAction(act, hubAddress + Constants.hubEndpoint, getBaseContext());
			}
		}
	}
}
