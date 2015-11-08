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

    /**
     * Connects the service to the server when it is created
     */
    @Override
    public void onCreate() {
        //TODO connect to server
    }

    /**
     * Starts the service and creates a User if necessary
     *
     * @param intent The Intent which states if we have to create a new User
     * @return
     *      The starting Mode which makes the service never stop or
     *      at least restarts when it is closed by the system
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("newUser", false)) {
            String email = intent.getStringExtra("email");
            //TODO Call to create new User to the server and connect to server
        }

        return START_STICKY;
    }

    /**
     * Returns the correct binder depending on the activity binding
     * @param intent
     *      The intent of the binding which contains a boolean that states
     *      which binder to send
     * @return The Binder concerned
     */
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

    /**
     * Sends request to server
     * @param request The request to send
     */
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

    /**
     * Disconnects to server when the service is stopped
     */
    @Override
    public void onDestroy() {
        //TODO : disconnect from server
    }
}
