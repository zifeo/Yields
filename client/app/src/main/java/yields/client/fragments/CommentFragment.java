package yields.client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import yields.client.R;
import yields.client.listadapter.ListAdapterMessages;
import yields.client.messages.CommentView;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Class representing a fragment for comments displaying.
 * The View of this fragment contains the parent message of the comments and
 * underneath lies a ListView containing the comments.
 */
public class CommentFragment extends Fragment{
    private static View mLayout;
    private static ListView mCommentList;
    private static Message mMessage;
    private static View mMessageView;
    private static ListAdapterMessages mAdapter;

    /**
     * Default constructor for the comment fragment.
     */
    public CommentFragment(){
        mLayout = new LinearLayout(YieldsApplication.getApplicationContext());
        mCommentList = new ListView(YieldsApplication.getApplicationContext());
        mMessage = null;
        mMessageView = null;
        mAdapter = null;
    }

    /**
     * Setter for the message displayed in the comment fragment. And
     * alternatively the message we want to comment.
     * @param m The message.
     */
    public void setMessage(Message m){
        mMessage = m;
        mMessageView = new MessageView(YieldsApplication
                .getApplicationContext(), mMessage);
    }

    /**
     * Setter for the adapter to be used in the list view containing the
     * comments.
     * @param adapter The adapter.
     */
    public void setAdapter(ListAdapterMessages adapter){
        mAdapter = adapter;
    }

    /**
     * Override of the onCreateView method, called every time the fragment is
     * created and put into a fragment container.
     * @param inflater The inflater used to create the layout of this fragment.
     * @param container The container if this fragment.
     * @param savedInstanceState The bundle to be used during the construction.
     * @return The View of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mLayout = inflater.inflate(R.layout
                        .comment_fragment_layout, container, false);
        LinearLayout messageContainer = (LinearLayout) mLayout.findViewById(R
                .id.messageContainer);
        messageContainer.removeAllViews();
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), mMessage);
        messageContainer.addView(commentView);
        ListView listView = (ListView) mLayout.findViewById(R.id.commentList);
        listView.setAdapter(mAdapter);
        return mLayout;
    }

    /**
     * Getter for the ListView containing the comments.
     * @return The list view containing the comments.
     */
    public ListView getCommentListView(){
        return mCommentList;
    }
}
