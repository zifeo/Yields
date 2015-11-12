package yields.client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import yields.client.listadapter.ListAdapterMessages;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.yieldsapplication.YieldsApplication;

public class CommentFragment extends Fragment{
    private static LinearLayout mLayout;
    private static ListView mCommentList;
    private static Message mMessage;
    private static View mMessageView;
    private static ListAdapterMessages mAdapter;

    public CommentFragment(){
        mLayout = new LinearLayout(YieldsApplication.getApplicationContext());
        mCommentList = new ListView(YieldsApplication.getApplicationContext());
        mMessage = null;
        mMessageView = null;
        mAdapter = null;
    }

    public void setMessage(Message m){
        mMessage = m;
        mMessageView = new MessageView(YieldsApplication
                .getApplicationContext(), mMessage);
    }

    public void setAdapter(ListAdapterMessages adapter){
        mAdapter = adapter;
        mCommentList.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mLayout.setOrientation(LinearLayout.VERTICAL);
        mLayout.addView(mMessageView);
        mLayout.addView(mCommentList);
        return mLayout;
    }
}
