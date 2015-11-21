package yields.client.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import yields.client.R;
import yields.client.activities.GroupActivity;
import yields.client.activities.MessageActivity;
import yields.client.activities.NotifiableActivity;
import yields.client.cache.CacheDatabaseHelper;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class YieldService extends Service {
    // This is necessary as mServiceRequestController can't be final.
    private final Object serviceControllerLock = new Object();
    private Binder mBinder;
    private NotifiableActivity mCurrentNotifiableActivity;
    private Group mCurrentGroup;
    private int mIdLastNotification;
    private ServiceRequestController mServiceRequestController;
    private ConnectControllerTask mConnectControllerTask;

    /**
     * Connects the service to the server when it is created and
     * creates the binder for the application Activities
     */
    @Override
    public void onCreate() {
        mBinder = new YieldServiceBinder(this);
        mIdLastNotification = 0;
        Log.d("Y:" + this.getClass().getName(), "create Yield Service");
        mConnectControllerTask = new ConnectControllerTask();
        mConnectControllerTask.execute();
    }

    /**
     * Starts the service and creates a User if necessary
     *
     * @param intent The Intent which states if we have to create a new User
     * @return The starting Mode which makes the service never stop or
     * at least restarts when it is closed by the system
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO : to be defined what to do when starting a connection
        /*if (intent != null && intent.getBooleanExtra("newUser", false)) {
            String email = intent.getStringExtra("email");
            ServerRequest connectReq = RequestBuilder.userConnectRequest(new Id(0), email);
            sendRequest(connectReq);
        }*/

        Log.d("Y:" + this.getClass().getName(), "Starting yield service");

        return START_STICKY;
    }

    /**
     * Responds to a connection status request.
     */
    public void connectionStatusResponse(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (serviceControllerLock) {
                    while (mServiceRequestController == null) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }

                if (mServiceRequestController.isConnected()) {
                    onServerConnected();
                } else {
                    onServerDisconnected();
                }
            }
        }).start();
    }

    /**
     * Returns the binder for the running application
     *
     * @param intent The intent of the binding
     * @return The Binder concerned
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Y:" + this.getClass().getName(), "Service binds to an activity");
        return mBinder;
    }

    /**
     * What's happen when we unbind from server
     *
     * @param intent The unbinding intent
     * @return true to authorize rebinding
     */
    @Override
    public boolean onUnbind(Intent intent){
        Log.d("Y:" + this.getClass().getName(), "unbind : " +
                (intent.getBooleanExtra("bindMessageActivity", false) ?
                        "messageActivity" : "groupActivity"));
        return true;
    }

    /**
     * Sends serverRequest to server
     *
     * @param serviceRequest The serviceRequest to send
     */
    public void sendRequest(ServiceRequest serviceRequest) {
        new SendRequestTask().execute(serviceRequest);
    }

    /**
     * Disconnects to server when the service is stopped
     */
    @Override
    public void onDestroy() {
        Log.d("Y:" + this.getClass().getName(), "Yield service has been disconnected");
        receiveError("Yield service has been disconnected");
        //TODO : disconnect from server
    }

    /**
     * Sets the message activity that will retrieve the messages
     *
     * @param notifiableActivity The concerned messageActivity
     */
    synchronized public void setNotifiableActivity(NotifiableActivity notifiableActivity) {
        Log.d("Y:" + this.getClass().getName(),"add activity");
        mCurrentNotifiableActivity = notifiableActivity;
        mCurrentGroup = YieldsApplication.getGroup();
    }

    /**
     * Unset the current messageActivity
     */
    synchronized public void unsetMessageActivity() {
        Log.d("Y:" + this.getClass().getName(),"remove activity");
        mCurrentNotifiableActivity = null;
    }

    /**
     * Called when the server has been disconnected
     */
    synchronized public void onServerDisconnected() {
        if (mCurrentNotifiableActivity != null) {
            mCurrentNotifiableActivity.notifyOnServerDisconnected();
        }
    }

    /**
     * Called when the server is connected
     */
    synchronized public void onServerConnected() {
        if (mCurrentNotifiableActivity != null) {
            mCurrentNotifiableActivity.notifyOnServerConnected();
        }
    }

    /**
     * Called when a message is received from the server
     *
     * @param group The group the message s from
     * @param message The message in question
     */
    synchronized public void receiveMessage(Group group, Message message) {
        if (mCurrentNotifiableActivity == null ||
                mCurrentGroup.getId() != group.getId()) {
            sendMessageNotification(group, message);
        } else {
            mCurrentGroup.addMessage(message);
            mCurrentNotifiableActivity.notifyChange();
        }
    }

    /**
     * Called when multiple message is received from the server
     *
     * @param groupId The group the messages are from
     * @param messages The message in question
     */
    synchronized public void receiveMessages(Id groupId, List<Message> messages) {
        //TODO: To be refactor !
        if (mCurrentNotifiableActivity != null &&
                mCurrentGroup.getId().getId().equals(groupId.getId())) {
            mCurrentGroup.addMessages(messages);

            mCurrentNotifiableActivity.notifyChange();
        }
    }

    /**
     * Called when an error is received from the server
     *
     * @param errorMsg The content of the error
     */
    public void receiveError(String errorMsg) {
        //YieldsApplication.showToast(this, errorMsg);
    }

    // TODO : receive a response from server (an error message)

    /**
     * Create notification for message.
     * @param message The message.
     */
    private void sendMessageNotification(Group group, Message message) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.send_icon)
                        .setContentTitle("Message from " + message.getSender().getName())
                        .setContentText(message.getContent().toString().substring(0, 50));

        // Creates an explicit intent for an Activity in your app
        YieldsApplication.setGroup(group);
        Intent resultIntent = new Intent(this, MessageActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(GroupActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mIdLastNotification++;
        mNotificationManager.notify(mIdLastNotification, notificationBuilder.build());
    }

    /**
     * Tries to reconnect the server.
     */
    public void reconnectServer() {
        mServiceRequestController.notifyConnector();
    }

    /**
     * AsncTask sending th requests.
     */
    private class SendRequestTask extends AsyncTask<ServiceRequest, Void, Void> {
        @Override
        protected Void doInBackground(ServiceRequest... params) {
            synchronized (serviceControllerLock) {
                while (mServiceRequestController == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Log.d("Y:" + this.getClass().getName(),"Stopped waiting for request controller" + e.getMessage());
                    }
                }
            }

            if (params.length > 0 && params[0] != null) {
                mServiceRequestController.handleServiceRequest(params[0]);
            } else {
                throw new IllegalArgumentException();
            }

            return null;
        }
    }

    /**
     * Connects to server // TODO: If not try again later
     */
    private class ConnectControllerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            synchronized (serviceControllerLock) {
                mServiceRequestController = new ServiceRequestController(
                        new CacheDatabaseHelper(getApplicationContext()),
                        YieldService.this);
            }

            Log.d("D:" + this.getClass().getName(), "done");

            return null;
        }
    }
}
