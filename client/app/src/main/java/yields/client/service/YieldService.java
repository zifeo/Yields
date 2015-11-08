package yields.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.io.IOException;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.Request;

public class YieldService extends Service {
    final static private int mStartMode = START_STICKY;

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
        IBinder binder = null;

        // A client is binding to the service with bindService()
        if (intent.getBooleanExtra("bindMessageActivity", false)) {
            binder = new MessageBinder(this);
        } else if (intent.getBooleanExtra("bindMessageActivity", false)) {
            //TODO : create a message binder for modifying/creating groups
        }

        return binder;
    }

    public void sendRequest(Request request){
        new SendRequestTask().execute(request);
    }

    private static class SendRequestTask extends AsyncTask<Request, Void, Void> {
        @Override
        protected Void doInBackground(Request... params) {
            //TODO : send Request to Server
            return null;
        }
    }

    @Override
    public void onDestroy() {
        //TODO : disconnect from server
    }
}
