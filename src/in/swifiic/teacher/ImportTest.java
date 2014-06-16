package in.swifiic.teacher;

import in.swifiic.android.app.lib.Helper;
import in.swifiic.android.app.lib.xml.Action;
import in.swifiic.examapp.Constants;
import in.swifiic.examapp.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ImportTest extends Activity {

	private static int FILE_SELECT_RESULT_CODE = 1192;
	private static int STUDENT_SELECT_RESULT_CODE = 1193;

	private String studentList = "";
	private String filePath = "";

	private TextView tFilePath;
	private TextView tStud;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_test);

		tStud = (TextView) findViewById(R.id.studCnt);
		tFilePath = (TextView) findViewById(R.id.filePath);
		Button bSelFile = (Button) findViewById(R.id.selectFile);
		Button bSelUser = (Button) findViewById(R.id.selectUsers);
		Button bSndFile = (Button) findViewById(R.id.sendFile);

		bSelFile.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// activity for importing test file
				Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
				fileIntent.setType("application/zip");// use "*/*" if this
														// doesn't work
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
				Intent stdSelect = new Intent(ImportTest.this,
						SelectStudents.class);
				startActivityForResult(stdSelect, STUDENT_SELECT_RESULT_CODE);
			}

		});

		bSndFile.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendFile();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.import_test, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		if (requestCode == FILE_SELECT_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				filePath = data.getData().getPath();
				// FilePath is path of file as a string
				tFilePath.setText(filePath);

			}
		} else if (requestCode == STUDENT_SELECT_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				studentList = data.getStringExtra("selectedStudents");
				int studCnt = 0;
				for (int i = 0; i < studentList.length(); i++) {
					if (studentList.charAt(i) == '|')
						studCnt++;
				}
				tStud.setText("No. of students selected: " + ++studCnt);
			}
		}
	}

	protected void sendFile() {

		if (filePath.equals("") && studentList.equals("")) {
			Toast.makeText(getBaseContext(),
					"Select exam file and students before sending",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (filePath.equals("")) {
			Toast.makeText(getBaseContext(), "Select exam file before sending",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (studentList.equals("")) {
			Toast.makeText(getBaseContext(), "Select students before sending",
					Toast.LENGTH_SHORT).show();
			return;
		} else {

			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			Action act = new Action("SendQuestions", Constants.aeCtx);
			String dataStr = Helper.fileToB64String(filePath);
			act.setFileData(dataStr);
			// gets Teacher name from SUTA provider
			String fromTeacher = sharedPref.getString("my_identity", "aniket2");
			act.addArgument("fromTeacher", fromTeacher);
			// XXX - should be from drop down list - similar to messenger -
			// should have multi-select - New Activity is also fine
			act.addArgument("students", studentList); // "aniket|abhishek|shivam"
			act.addArgument(
					"course",
					filePath.substring(filePath.lastIndexOf('/') + 1,
							filePath.lastIndexOf('.')));
			String hubAddress = sharedPref.getString("hub_address", "");
			if (hubAddress.equals("")) {
				Toast.makeText(getBaseContext(), "SUTA is not running.",
						Toast.LENGTH_SHORT).show();
			} else {
				Helper.sendAction(act, hubAddress + Constants.hubEndpoint,
						getBaseContext());
				Toast.makeText(getBaseContext(), "Test sent successfully",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save state to the savedInstanceState
		savedInstanceState
				.putString("FilePath", tFilePath.getText().toString());
		savedInstanceState
				.putString("StudentCount", tStud.getText().toString());

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore state from savedInstanceState
		tFilePath.setText(savedInstanceState.getString("FilePath"));
		tStud.setText(savedInstanceState.getString("StudentCount"));
	}
}