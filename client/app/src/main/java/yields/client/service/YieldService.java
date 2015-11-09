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
import android.widget.Toast;

import java.util.List;

import yields.client.R;
import yields.client.activities.GroupActivity;
import yields.client.activities.MessageActivity;
import yields.client.activities.NotifiableActivity;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;
import yields.client.yieldsapplication.YieldsApplication;

public class YieldService extends Service {
    private Binder mMessageBinder;
    private Binder mGroupBinder;
    private NotifiableActivity mCurrentNotifiableActivity;
    private Group mCurrentGroup;
    private List<Group> mWaitingUpdateGroup;
    private int mIdLastNotification;

    /**
     * Connects the service to the server when it is created and
     * creates the binder for the application Activities
     */
    @Override
    public void onCreate() {
        mMessageBinder = new MessageBinder(this);
        mGroupBinder = new GroupBinder(this);
        mIdLastNotification = 0;
        //TODO connect to server
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
        if (intent != null && intent.getBooleanExtra("newUser", false)) {
            String email = intent.getStringExtra("email");
            Request connectReq = RequestBuilder.userConnectRequest(new Id(0), email);
            sendRequest(connectReq);
        }

        Log.d("DEBUG", "Starting yield service");

        return START_STICKY;
    }

    /**
     * Returns the correct binder depending on the activity binding
     *
     * @param intent The intent of the binding which contains a boolean that states
     *               which binder to send
     * @return The Binder concerned null if no information on the binding is given
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DEBUG", "Service binds to an activity");
        // A client is binding to the service with bindService()
        if (intent.getBooleanExtra("bindMessageActivity", false)) {
            return mMessageBinder;
        } else if (intent.getBooleanExtra("bindGroupActivity", false)) {
            return mGroupBinder;
        } else {
            return null;
        }
    }

    /**
     * Sends request to server
     *
     * @param request The request to send
     */
    public void sendRequest(Request request) {
        new SendRequestTask().execute(request);
    }

    /**
     * Disconnects to server when the service is stopped
     */
    @Override
    public void onDestroy() {
        Log.d("DEBUG", "Yield service has been disconnected");
        receiveError("Yield service has been disconnected");
        //TODO : disconnect from server
    }

    /**
     * Sets the message activity that will retrieve the messages
     *
     * @param notifiableActivity The concerned messageActivity
     */
    synchronized public void setNotifiableActivity(NotifiableActivity notifiableActivity) {
        mCurrentNotifiableActivity = notifiableActivity;
        mCurrentGroup = YieldsApplication.getGroup();
    }

    /**
     * Unset the current messageActivity
     */
    synchronized public void unsetMessageActivity() {
        mCurrentNotifiableActivity = null;
    }

    /**
     * Called when a message is received from the server
     *
     * @param groupId The group the message ud from
     * @param message The message in question
     */
    synchronized public void receiveMessage(Id groupId, Message message) {
        if (mCurrentNotifiableActivity == null ||
                mCurrentGroup.getId() != groupId) {
            sendMessageNotification(message);
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
        if (mCurrentNotifiableActivity != null &&
                mCurrentGroup.getId() == groupId) {

            mCurrentGroup.addMessages(messages);
            mCurrentNotifiableActivity.notifyChange();
        }
    }

    /**
     * Called when an error is received from the server
     *
     * @param errorMsg The content of the error
     */
    public void receiveError(String errorMsg, int time) {
        if (time != Toast.LENGTH_SHORT && time != Toast.LENGTH_LONG) {
            throw new IllegalArgumentException("not a valid toast length");
        }

        Toast toast = Toast.makeText(this, errorMsg, time);
        toast.show();
    }

    public void receiveError(String errorMsg) {
        receiveError(errorMsg, Toast.LENGTH_SHORT);
    }

    // TODO : receive a response from server (an error message)

    /**
     * Create notification for message.
     * @param message The message.
     */
    private void sendMessageNotification(Message message) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.send_icon)
                        .setContentTitle("Message from " + message.getSender().getName())
                        .setContentText(message.getContent().toString().substring(0, 50));

        // Creates an explicit intent for an Activity in your app
        YieldsApplication.setGroup(message.getReceivingGroup());
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
     * AsncTask sending th requests.
     */
    private static class SendRequestTask extends AsyncTask<Request, Void, Void> {
        @Override
        protected Void doInBackground(Request... params) {
            //TODO : send Request to Server
            return null;
        }
    }
}
