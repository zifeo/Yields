package yields.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import yields.client.id.Id;
import yields.client.node.User;

public class YieldService extends Service {
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind

    @Override
    public void onCreate() {
        //TODO connect to server
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("newUser", false)) {
            String email = intent.getStringExtra("email");
            //TODO Call to create new User to the server and connect to server
        }

        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        if (intent.getBooleanExtra("bindMessageActivity", false)) {
            //TODO : create a message binder for sending messages
        } else if (intent.getBooleanExtra("bindMessageActivity", false)) {
            //TODO : create a message binder for modifying/creating groups
        }

        return mBinder;
    }

    @Override
    public void onDestroy() {
        //TODO : disconnect from server
    }
}
