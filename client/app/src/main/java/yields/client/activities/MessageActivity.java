package yields.client.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import yields.client.R;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapter;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class MessageActivity extends AppCompatActivity {
    private static ClientUser mUser;
    private static Group mGroup;
    private static ArrayList<Message> mMessages;
    private static ListAdapter mAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap mImage; // Image taken from the gallery.
    private boolean mSendImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        YieldsApplication.setApplicationContext(getApplicationContext());
        YieldsApplication.setResources(getResources());

        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();
        mMessages = new ArrayList<>();
        mAdapter = new ListAdapter(YieldsApplication.getApplicationContext(), R.layout.messagelayout, mMessages);
        mImage = null;
        mSendImage = false;

        ListView lv = (ListView) findViewById(R.id.messageScrollLayout);
        lv.setAdapter(mAdapter);

        //setTitle(mGroup.getName()); TODO

        //retrieveGroupMessages();  TODO
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendTextMessage(View v){
        TextView inputField = (TextView) findViewById(R.id.inputMessageField);
        String inputMessage =  inputField.getText().toString();
        inputField.setText("");
        Content content = null;
        Log.i("DEBUG", "Entering if");
        if (!mSendImage){
            Log.i("DEBUG", "Creating image");
            mImage = BitmapFactory.decodeResource(getResources(), R.drawable.image_test);
            content = new ImageContent(mImage, inputMessage);
            mSendImage = false;
        }
        else {
            content = new TextContent(inputMessage);
        }
        Message message = new Message("message", new Id(1230), mUser, content);
                // TODO : take tight name and right id.
        mMessages.add(message);
       // mUser.sendMessage(mGroup, message); TODO : implement sendMessage for ClientUser.
        mAdapter.notifyDataSetChanged();
    }

    public void onClickAddImage(View v){
        mSendImage = true;
        pickImageFromGallery();
        Toast toast = new Toast(YieldsApplication.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText("Image added to message");
        toast.show();
    }

    /**
     * Called when new message(s) have been received.
     * @param newMessages The new message(s) received.
     */
    public void receiveNewMessage(List<Message> newMessages){
        mMessages.addAll(newMessages);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages(){
        mMessages = new ArrayList<>(mUser.getGroupMessages(mGroup));
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                mImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
