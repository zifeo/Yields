package yields.client.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.Date;

import yields.client.R;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;

public class SaveImageToGalleryFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.saveImageToGalleryDialog)
                .setPositiveButton(R.string.saveImageAcceptButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MediaStore.Images.Media.insertImage(YieldsApplication.getApplicationContext()
                                        .getContentResolver(), YieldsApplication
                                        .getShownImage(),
                                DateSerialization.dateSerializer.toStringForCache(new Date()),
                                "Image downloaded from Yields");
                    }
                })
                .setNegativeButton(R.string.saveImageDeclineButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

