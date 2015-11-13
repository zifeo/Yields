package yields.client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import yields.client.R;
import yields.client.listadapter.ListAdapterMessages;

public class GroupMessageFragment extends Fragment{
    private static View mLayout;
    private static ListAdapterMessages mAdapter;
    private static ListView mMessageList;
    private static AdapterView.OnItemClickListener mListOnClickListener;

    public GroupMessageFragment(){

    }

    public void setAdapter(ListAdapterMessages adapterMessages){
        mAdapter = adapterMessages;
    }

    public void setMessageListOnClickListener(AdapterView.OnItemClickListener
                                                      ocl){
        mListOnClickListener = ocl;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.d("GroupMessageFragment", "onCreateView");
        mLayout = inflater.inflate(R.layout
                .group_message_fragment_layout, container, false);
        mMessageList = (ListView) mLayout.findViewById(R.id.messagesList);
        mMessageList.setAdapter(mAdapter);
        mMessageList.setOnItemClickListener(mListOnClickListener);
        return mLayout;
    }

    public ListView getMessageListView(){
        return mMessageList;
    }
}
