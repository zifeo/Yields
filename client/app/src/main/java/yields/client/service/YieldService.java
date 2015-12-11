package yields.client.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.activities.GroupActivity;
import yields.client.activities.MessageActivity;
import yields.client.activities.NotifiableActivity;
import yields.client.cache.CacheDatabaseHelper;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserConnectRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class YieldService extends Service {

    public final static String GROUP_RECEIVING = "groupReceiving";
    public final static String NOTIFICATION = "notification";
    private final Object serviceControllerLock = new Object();
    private NotificationManager mNotificationManager;
    private YieldServiceBinder mBinder;
    private NotifiableActivity mCurrentNotifiableActivity;
    private Group mCurrentGroup;
    private int mIdLastNotification;
    private ServiceRequestController mServiceRequestController;
    private ConnectControllerTask mConnectControllerTask;
    private boolean mWasConnected;
    private final Map<Long, List<Integer>> mNotificationMap = new HashMap<>();

    /**
     * Connects the service to the server when it is created and
     * creates the binder for the application Activities
     */
    @Override
    public void onCreate() {
        mBinder = new YieldServiceBinder(this);
        mIdLastNotification = 0;
        Log.d("Y:" + this.getClass().getName(), "create Yield Service");
        mWasConnected = false;
        mConnectControllerTask = new ConnectControllerTask();
        mConnectControllerTask.execute();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        if (intent != null) {
            String email = intent.getStringExtra("email");
            if (email == null) {
                throw new IllegalArgumentException("Intent didn't start the service with an email");
            }

            ClientUser user = YieldsApplication.getUser();

            if (user == null || user.getEmail() != email) {
                YieldsApplication.setUser(new ClientUser(email));
            }

            mWasConnected = false;

            /*if (mServiceRequestController.isConnected()) {
                ServiceRequest connectReq = new UserConnectRequest(YieldsApplication.getUser());
                sendRequest(connectReq);
            }*/
        }

        Log.d("Y:" + this.getClass().getName(), "Starting yield service");

        return START_STICKY;
    }

    /**
     * Responds to a connection status request.
     */
    public void connectionStatusResponse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (serviceControllerLock) {
                    while (mServiceRequestController == null) {
                        try {
                            serviceControllerLock.wait();
                        } catch (InterruptedException e) {
                            Log.d("Y:" + this.getClass().getName(),
                                    "Stopped waiting for request controller" + e.getMessage());
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

    public void notifyChange(NotifiableActivity.Change change) {
        if (change == NotifiableActivity.Change.CONNECTED
                || change == NotifiableActivity.Change.NEW_USER) {
            mBinder.changeStatus(change);
        }

        if (mCurrentNotifiableActivity != null) {
            Log.d("Y:" + this.getClass().getName(), "notified activity");
            mCurrentNotifiableActivity.notifyChange(change);
        } else {
            Log.d("Y:" + this.getClass().getName(), "not notified activity");
        }
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
    public boolean onUnbind(Intent intent) {
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
        new SendRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serviceRequest);
    }

    /**
     * Disconnects to server when the service is stopped
     */
    @Override
    public void onDestroy() {
        Log.d("Y:" + this.getClass().getName(), "Yield service has been disconnected");
        receiveError("Yield service has been disconnected");
    }

    /**
     * Sets the message activity that will retrieve the messages
     *
     * @param notifiableActivity The concerned messageActivity
     */
    synchronized public void setNotifiableActivity(NotifiableActivity notifiableActivity) {
        Log.d("Y:" + this.getClass().getName(), "add activity");
        mCurrentNotifiableActivity = notifiableActivity;
        mCurrentGroup = YieldsApplication.getGroup();
    }

    /**
     * Unset the current messageActivity
     */
    synchronized public void unsetNotifiableActivity() {
        Log.d("Y:" + this.getClass().getName(), "remove activity");
        mCurrentNotifiableActivity = null;
        mCurrentGroup = null;
    }

    /**
     * Called when the server has been disconnected
     */
    synchronized public void onServerDisconnected() {
        if (mCurrentNotifiableActivity != null) {
            mCurrentNotifiableActivity.notifyOnServerDisconnected();
        }
        mWasConnected = false;
    }

    /**
     * Called when the server is connected
     */
    synchronized public void onServerConnected() {
        if (!mWasConnected) {

            ServiceRequest request = null;

            if (YieldsApplication.getUser() != null) {
                request = new UserConnectRequest(YieldsApplication.getUser());
                this.sendRequest(request);
            }

            mWasConnected = true;
        }
        if (mCurrentNotifiableActivity != null) {
            mCurrentNotifiableActivity.notifyOnServerConnected();
        }
    }

    /**
     * Called when a message is received from the server.
     *
     * @param groupId The id of the group the message is from.
     * @param message The message in question.
     */
    synchronized public void receiveMessage(Id groupId, Message message) {
        if (mCurrentNotifiableActivity == null || mCurrentGroup == null ||
                !mCurrentGroup.getId().getId().equals(groupId.getId())) {
            Group group = YieldsApplication.getUser().getGroup(groupId);
            group.addMessage(message);

            if (!message.getCommentGroupId().equals(new Id(-1))) {
                Group commentGroup = Group.createGroupForMessageComment(message, group);
                YieldsApplication.getUser().addCommentGroup(commentGroup);
            }

            sendMessageNotification(group, message);
            if (mCurrentNotifiableActivity != null) {
                mCurrentNotifiableActivity.notifyChange(NotifiableActivity.Change.GROUP_LIST);
            }
        } else {
            mCurrentGroup.addMessage(message);
            mCurrentNotifiableActivity.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);
        }
    }

    /**
     * Called when multiple message is received from the server
     *
     * @param groupId  The group the messages are from
     * @param messages The message in question
     */
    synchronized public void receiveMessages(Id groupId, List<Message> messages) {
        if (mCurrentNotifiableActivity != null && mCurrentGroup != null &&
                mCurrentGroup.getId().getId().equals(groupId.getId())) {
            mCurrentGroup.addMessages(messages);
            mCurrentNotifiableActivity.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);
        } else {
            if (mCurrentNotifiableActivity != null) {
                mCurrentNotifiableActivity.notifyChange(NotifiableActivity.Change.GROUP_LIST);
            }
            Log.d("Y:" + this.getClass().getName(), "nothing to notify");
            YieldsApplication.getUser().getGroup(groupId).addMessages(messages);
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

    public void cancelNotificationForGroupId(Id groupId) {
        List<Integer> list = mNotificationMap.get(groupId.getId());
        if (list != null) {
            for (Integer notificationId : list) {
                mNotificationManager.cancel(notificationId);
            }
            list.clear();
        }
    }

    /**
     * Create notification for message.
     *
     * @param message The message.
     */
    private void sendMessageNotification(Group group, Message message) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(group.getImage())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle("Message from " + YieldsApplication
                                .getNodeFromId(message.getSender()).getName())
                        .setContentText(message.getContent().getTextForRequest())
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine(message.getContent().getTextForRequest()))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setDefaults(Notification.DEFAULT_SOUND);

        YieldsApplication.setGroup(group);

        Intent resultIntent;
        if (YieldsApplication.getApplicationContext() == null) {
            resultIntent = new Intent(this, MessageActivity.class);
        } else {
            resultIntent = new Intent(YieldsApplication.getApplicationContext(), MessageActivity.class);
        }
        resultIntent.putExtra(NOTIFICATION, true);
        resultIntent.putExtra(GROUP_RECEIVING, group.getId().getId().longValue());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(GroupActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        mIdLastNotification++;
        List<Integer> list = mNotificationMap.get(group.getId().getId());

        if (list == null) {
            list = new LinkedList<>();
            list.add(mIdLastNotification);
            mNotificationMap.put(group.getId().getId(), list);
        } else {
            list.add(mIdLastNotification);
        }
        mNotificationManager.notify(mIdLastNotification, notificationBuilder.build());
    }

    /**
     * Tries to reconnect the server.
     */
    public void reconnectServer() {
        mServiceRequestController.notifyConnector();
    }

    public boolean hasPending(Id id) {
        List res = mNotificationMap.get(id.getId());

        if (res == null) {
            Log.d("LOSADSHFIAGSF", "bonjour : " + id.getId());
            return false;
        } else {
            Log.d("LOSADSHFIAGSF", "bonjour : " + id.getId() + " - " + res.toString());
            return !res.isEmpty();
        }
    }

    /**
     * AsyncTask sending th requests.
     */
    private class SendRequestTask extends AsyncTask<ServiceRequest, Void, Void> {
        @Override
        protected Void doInBackground(ServiceRequest... params) {
            synchronized (serviceControllerLock) {
                while (mServiceRequestController == null) {
                    try {
                        serviceControllerLock.wait();
                    } catch (InterruptedException e) {
                        Log.d("Y:" + this.getClass().getName(), "Stopped waiting for request controller" + e.getMessage());
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
     * Connects to server
     */
    private class ConnectControllerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            synchronized (serviceControllerLock) {
                mServiceRequestController = new ServiceRequestController(
                        new CacheDatabaseHelper(getApplicationContext()),
                        YieldService.this);
            }

            Log.d("Y:" + this.getClass().getName(), "done");

            return null;
        }
    }
}
