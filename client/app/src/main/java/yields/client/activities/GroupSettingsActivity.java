package yields.client.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.listadapter.ListAdapterSettings;
import yields.client.yieldsapplication.YieldsApplication;


public class GroupSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Group Settings2");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        List<String> itemList = new ArrayList<>();

        itemList.add("Change group name");
        itemList.add("Change group type");
        itemList.add("Change group image");
        itemList.add("Add users");

        ListView listView = (ListView) findViewById(R.id.listViewSettings);

        ListAdapterSettings arrayAdapter = new ListAdapterSettings(getApplicationContext(), R.layout.group_settings_layout, itemList);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new CustomListener());
        listView.setItemsCanFocus(false);

    }

    private class CustomListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0: // change name
                    changeNameListener();
                    break;

                case 1: // change type
                    changeTypeListener();
                    break;

                default:


                    break;
            }

        }

        private void changeNameListener(){
            final EditText editTextName = new EditText(GroupSettingsActivity.this);
            editTextName.setText("Current Group");

            new AlertDialog.Builder(GroupSettingsActivity.this)
                    .setTitle("Change group name")
                    .setMessage("Type the new group's name !")
                    .setView(editTextName)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String url = editTextName.getText().toString();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        private void changeTypeListener(){
            final CharSequence[] types = {" Public "," Private "};
            AlertDialog groupTypeDialog;

            // Creating and Building the Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingsActivity.this)
                .setTitle("Change group type")
                .setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: // public

                                break;
                            default: // private

                                break;

                        }
                    }
                })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
            groupTypeDialog = builder.create();
            groupTypeDialog.show();
        }
    }

}
