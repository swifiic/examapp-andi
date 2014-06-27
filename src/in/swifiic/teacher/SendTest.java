package in.swifiic.teacher;

import in.swifiic.android.app.lib.Helper;
import in.swifiic.android.app.lib.xml.Action;
import in.swifiic.examapp.Constants;
import in.swifiic.examapp.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class SendTest extends Activity {

	private static int FILE_SELECT_RESULT_CODE = 1192;
	private static int STUDENT_SELECT_RESULT_CODE = 1193;

	private String studentList = "";
	private String filePath = "";

	private Button bSelUser;
	private Button bSelFile;
	public Button bSelDate;
	public Button bSelTime;

	private EditText tName;
	private EditText tDur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_test);

		bSelFile = (Button) findViewById(R.id.selectFile);
		bSelUser = (Button) findViewById(R.id.selectUsers);
		bSelDate = (Button) findViewById(R.id.selectTestDate);
		bSelTime = (Button) findViewById(R.id.selectTestTime);
		tName = (EditText) findViewById(R.id.tTestName);
		tDur = (EditText) findViewById(R.id.tTestDur);
		Button bSndFile = (Button) findViewById(R.id.sendFile);
		// tDate = (TextView) findViewById(R.id.testDateText);

		bSelFile.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// activity for importing test file
				Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
				fileIntent.setType("application/zip");// use "*/*" if this
														// doesn't work
				try {
					startActivityForResult(fileIntent, FILE_SELECT_RESULT_CODE);
				} catch (ActivityNotFoundException e) {
					Log.e("filePicker",
							"No activity can handle picking a file. Showing alternatives.");
				}
			}
		});

		bSelUser.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent stdSelect = new Intent(SendTest.this,
						SelectStudents.class);
				startActivityForResult(stdSelect, STUDENT_SELECT_RESULT_CODE);
			}

		});

		bSelDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DialogFragment datePickerDlg = new DatePickerFragment();
				datePickerDlg.show(getFragmentManager(), "datePicker");
			}
		});

		bSelTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DialogFragment timePickerDialog = new TimePickerFragment();
				timePickerDialog.show(getFragmentManager(), "timePicker");
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
				bSelFile.setText(filePath.substring(
						filePath.lastIndexOf('/') + 1,
						filePath.lastIndexOf('.')));
			}
		} else if (requestCode == STUDENT_SELECT_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				studentList = data.getStringExtra("selectedStudents");
				int studCnt = 0;
				for (int i = 0; i < studentList.length(); i++) {
					if (studentList.charAt(i) == '|')
						studCnt++;
				}
				bSelUser.setText("Students selected: " + ++studCnt);
			}
		}
	}

	protected void sendFile() {

		if (tName.getText().toString().equals("")) {
			Toast.makeText(getBaseContext(), "Enter test name",
					Toast.LENGTH_SHORT).show();
			tName.requestFocus();
			return;
		}
		if (filePath.equals("") || studentList.equals("")
				|| tDur.getText().toString().equals("")
				|| bSelDate.getText().toString().equals("Click to choose")
				|| bSelTime.getText().toString().equals("Click to choose")) {
			Toast.makeText(getBaseContext(), "All fields are compulsory",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			filePath = Environment.getExternalStorageDirectory() + "/"
					+ filePath.substring(filePath.lastIndexOf(':') + 1);

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
			act.addArgument("students", studentList); // "student1|student2|student3"
			act.addArgument("course", tName.getText().toString());
			act.addArgument("testDate", bSelDate.getText().toString());
			act.addArgument("testTime", bSelTime.getText().toString());
			act.addArgument("testDuration", tDur.getText().toString());
			act.addArgument("fileName", bSelFile.getText().toString());
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
		savedInstanceState.putString("FilePath", bSelFile.getText().toString());
		savedInstanceState.putString("StudentCount", bSelUser.getText()
				.toString());

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore state from savedInstanceState
		bSelFile.setText(savedInstanceState.getString("FilePath"));
		bSelUser.setText(savedInstanceState.getString("StudentCount"));
	}
}