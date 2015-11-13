package yields.client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Objects;

import yields.client.R;
import yields.client.listadapter.ListAdapterMessages;

/**
 * Class representing a fragment responsible to display the messages of a
 * given group.
 * The Fragment is a Simple ListView containing the messages.
 */
public class GroupMessageFragment extends Fragment{
    private static View mLayout;
    private static ListAdapterMessages mAdapter;
    private static ListView mMessageList;
    private static AdapterView.OnItemClickListener mListOnClickListener;

    /**
     * Setter for the adapter to be used in the list view containing the
     * messages.
     * @param adapterMessages The adpater.
     */
    public void setAdapter(ListAdapterMessages adapterMessages){
        Objects.requireNonNull(adapterMessages);
        mAdapter = adapterMessages;
    }

    /**
     * Setter for the OnItemClickListener of the List View containing the
     * messages.
     * @param ocl The listener.
     */
    public void setMessageListOnClickListener(AdapterView.OnItemClickListener
                                                      ocl){
        Objects.requireNonNull(ocl);
        mListOnClickListener = ocl;
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
        Objects.requireNonNull(inflater);
        Objects.requireNonNull(container);
        Objects.requireNonNull(savedInstanceState);
        Objects.requireNonNull(mAdapter, "Error adapter not set yet.");
        Objects.requireNonNull(mListOnClickListener, "Error, " +
                "OnClickItemListener not set yet.");

        Log.d("GroupMessageFragment", "onCreateView");
        mLayout = inflater.inflate(R.layout
                .group_message_fragment_layout, container, false);
        mMessageList = (ListView) mLayout.findViewById(R.id.messagesList);
        mMessageList.setAdapter(mAdapter);
        mMessageList.setOnItemClickListener(mListOnClickListener);
        return mLayout;
    }

    /**
     * Getter for the ListView containing the messages.
     * @return The list view containing the messages.
     */
    public ListView getMessageListView(){
        Objects.requireNonNull(mMessageList, "Error : MessageList not yet " +
                "created");
        return mMessageList;
    }
}
