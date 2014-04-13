package in.swifiic.examapp;

import in.swifiic.android.app.lib.Constants;
import in.swifiic.android.app.lib.GenericService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ExamNoticeReceiver extends BroadcastReceiver {
	//protected Class<IntentService> className = null; 
	static final String TAG = "IBRDtnReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "onReceive:" + action);
        if (action.equals(Constants.NEWMSG_RECEIVED))
        {
        	if (!intent.hasExtra("notification")) {
        		 Log.e(TAG, "onReceive:missing Notification");
        		 return;
        	}
            // We received a notification about a new bundle and
            // wake-up the local MsngrService to received the bundle.
            Intent i = new Intent(context, ReceiverService.class);
            i.setAction(Constants.IBR_DTN_RECEIVE); 
            String notif = intent.getStringExtra("notification");
            i.putExtra("notification", notif);
            
            context.startService(i);
        }
    }

}
