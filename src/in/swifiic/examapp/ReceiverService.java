package in.swifiic.examapp;

import java.io.File;

import in.swifiic.android.app.lib.Helper;
import in.swifiic.android.app.lib.xml.Notification;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ReceiverService extends IntentService {
	static final String TAG = "ReceiverService";
	int notificationID = 1;
	
	public ReceiverService() {
		super(TAG);
		Log.d(TAG, "Service Created without name");
	}
	
	
	public ReceiverService(String name) {
		super(name);
		Log.d(TAG, "Service Created");
	}




	private void saveSubmissionAndNotify(String fileBase64Data,
			String studentName, String courseName) {
		Log.d(TAG, "saveSubmissionAndNotify");
		String path = Environment.getExternalStorageDirectory() + "/Exam/"
				+ courseName + "/";
		File filePath = new File(path);
		if (!filePath.exists())
			filePath.mkdir();
		path = path + studentName;
		Helper.b64StringToFile(fileBase64Data, path + ".zip");

		//TODO call the class which is used for evaluating students' submissions 
		Intent notifIntent = new Intent(ReceiverService.this,
				MainScreen.class);
		notifIntent.putExtra("course", courseName);
		
		PendingIntent pIntent = PendingIntent.getActivity(ReceiverService.this, 0,
				notifIntent, 0);

		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		
		android.app.Notification notif = new NotificationCompat.Builder(this)
        .setContentTitle("New Submission for course: " + courseName + "!")
        .setContentText("Student ID: " + studentName)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(pIntent)
        .setSound(soundUri).setVibrate(new long []{150, 400, 150, 400})
        .build();

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// If you want to hide the notification after it was selected, do the code below
        notif.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
		
		nm.notify(notificationID, notif);
		notificationID++;

	}

	private void createFileAndNotification(String fileBase64Data,
			String teacherName, String courseName) {
		Log.d(TAG, "createFileAndNotification");
		String path = Environment.getExternalStorageDirectory() + "/Exam/"
				+ courseName;
		Helper.b64StringToFile(fileBase64Data, path + ".zip");
		
		Intent notifIntent = new Intent(ReceiverService.this,
				in.swifiic.exam.LoginActivity.class);
		notifIntent.putExtra("course", courseName);
		
		PendingIntent pIntent = PendingIntent.getActivity(ReceiverService.this, 0,
				notifIntent, 0);

		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

/*		android.app.Notification notif = new android.app.Notification(
				R.drawable.ic_launcher,
				"Test Available",
				System.currentTimeMillis());
	*/	
		android.app.Notification notif = new NotificationCompat.Builder(this)
        .setContentTitle("New Test Available!")
        .setContentText("Course Name: " + courseName)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(pIntent)
        .setSound(soundUri).setVibrate(new long []{150, 400, 150, 400})
        .build();

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// If you want to hide the notification after it was selected, do the code below
        notif.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
		
		nm.notify(notificationID, notif);
		notificationID++;
		
		/* old code 
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Intent intent = new Intent(ReceiverService.this,
				in.swifiic.exam.LoginActivity.class);
		intent.putExtra("course", courseName);
		intent.putExtra("teacher", teacherName);
		startActivity(intent);
		*/

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.hasExtra("notification")) {
			String payload = intent.getStringExtra("notification");

			Log.d(TAG, "Handling incoming message: " + payload);
			Notification notif = Helper.parseNotification(payload);
			// Checking for opName of Notification
			if (notif.getNotificationName().equals("DeliverQuestions")) {
				Log.d(TAG, "Received Questions.");
				String fileBase64Data = notif.getFileData();
				String teacherName = notif.getArgument("fromTeacher");
				String courseName = notif.getArgument("course");
				createFileAndNotification(fileBase64Data, teacherName,
						courseName);
				Log.d(TAG, "Showing notification now...");
			} else if (notif.getNotificationName().equals("DeliverCopy")) {
				Log.d(TAG, "Received Copies.");
				String fileBase64Data = notif.getFileData();
				String studentName = notif.getArgument("fromStudent");
				String courseName = notif.getArgument("course");
				saveSubmissionAndNotify(fileBase64Data, studentName,
						courseName);
				Log.d(TAG, "Showing notification now...");			
			}
		}
	}
}
