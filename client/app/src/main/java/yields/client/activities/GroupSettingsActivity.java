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
    public enum Settings {NAME, TYPE, IMAGE, USERS}

    private static final int REQUEST_IMAGE= 1;

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

        List<String> itemList = new ArrayList<>(4);

        itemList.add(Settings.NAME.ordinal(), "Change group name");
        itemList.add(Settings.TYPE.ordinal(), "Change group type");
        itemList.add(Settings.IMAGE.ordinal(), "Change group image");
        itemList.add(Settings.USERS.ordinal(), "Add users");

        ListView listView = (ListView) findViewById(R.id.listViewSettings);

        ListAdapterSettings arrayAdapter = new ListAdapterSettings(getApplicationContext(), R.layout.group_settings_layout, itemList);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new CustomListener());
        listView.setItemsCanFocus(false);

    }

    private class CustomListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (position == Settings.NAME.ordinal()){
                changeNameListener();
            }
            else if (position == Settings.TYPE.ordinal()){
                changeTypeListener();
            }
            else if (position == Settings.IMAGE.ordinal()){
                changeImageListener();
            }
            else {

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

        void changeImageListener(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
        }
    }

}
