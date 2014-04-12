package in.swifiic.examApp;

/**
 * @author aniket
 *
 */

import java.io.File;
import java.io.FileOutputStream;

import de.tubs.ibr.dtn.util.Base64;

import in.swifiic.android.app.lib.Helper;
import in.swifiic.android.app.lib.ui.SwifiicActivity;
import in.swifiic.android.app.lib.xml.Notification;
import in.swifiic.examApp.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainScreen extends SwifiicActivity {
	final static String TAG = "MainScreen";
	
	public MainScreen() {
    	super();
    	
    	// This is a must for all applications - hook to get notification from GenericService
    	mDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("notification")) {
                	String payload= intent.getStringExtra("notification");
                	
                    Log.d(TAG, "Handling incoming message: " + payload);
                    Notification notif = Helper.parseNotification(payload);
                	// Checking for opName of Notification
                    if(notif.getNotificationName().equals("DeliverQuestions")) {
                    	Log.d(TAG, "Received Questions.");
                    	String fileBase64Data=notif.getFileData();
                    	String teacherName=notif.getArgument("fromTeacher");
                    	String courseName=notif.getArgument("course");
                    	createFileAndNotification(fileBase64Data,teacherName,courseName );
                    	Log.d(TAG, "Showing notification now...");
                    } else if(notif.getNotificationName().equals("DeliverCopy")) {
                    	Log.d(TAG, "Received Copies.");
                    	String fileBase64Data=notif.getFileData();
                    	String studentName=notif.getArgument("fromStudent");
                    	String courseName=notif.getArgument("course");
                    	saveSubmissionAndNotify(fileBase64Data,studentName,courseName );
                    	Log.d(TAG, "Showing notification now...");
                    }
                } else {
                    Log.d(TAG, "Broadcast Receiver ignoring message - no notification found");
                }
            }
        };      

	}

	private void saveSubmissionAndNotify(String fileBase64Data, String studentName, String courseName ) {
		Log.d(TAG, "saveSubmissionAndNotify");
		String path = Environment.getExternalStorageDirectory() + "/Exam/" + courseName + "/" + studentName;
		Helper.b64StringToFile(fileBase64Data, path + ".zip");
		
		// TO Do add logic to create notification XXX
	}
	
	private void createFileAndNotification(String fileBase64Data, String teacherName, String courseName ){
		Log.d(TAG, "createFileAndNotification");
		String path = Environment.getExternalStorageDirectory() + "/Exam/" + courseName;
		Helper.b64StringToFile(fileBase64Data, path + ".zip");
		
		// TO Do add logic to create notification XXX
		
		// XXX shortcut - just force the activity to launch
		Intent intent = new Intent(MainScreen.this,
				in.swifiic.exam.LoginActivity.class);
		intent.putExtra("course", courseName);
		intent.putExtra("teacher", teacherName);
		startActivity(intent);
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the status bar
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_screen);

		Button exam = (Button) findViewById(R.id.examButton);
		Button teacher = (Button) findViewById(R.id.teacherButton);

		exam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this,
						in.swifiic.exam.LoginActivity.class);
				startActivity(intent);
			}
		});

		teacher.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this,
						in.swifiic.teacher.TeacherActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;

	}
}