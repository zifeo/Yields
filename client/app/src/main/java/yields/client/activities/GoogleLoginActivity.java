package yields.client.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import yields.client.R;
import yields.client.yieldsapplication.YieldsApplication;

public class GoogleLoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* String used for debug log */
    private static final String TAG = "GoogleLoginActivity";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private SignInButton mGoogleSingInButton;
    private Button mButtonCancelGoogleConnection;
    private ProgressBar mProgressBarGoogleConnection;
    private TextView mTextViewGoogleConnecting;


    /**
     * On click Listener.
     * @param v The View instance.
     */
    @Override
    public void onClick(View v) {
        onSignInClicked();
    }

    /**
     * onCreate method for the GoogleLoginActivity.
     * @param savedInstanceState The bundle.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        YieldsApplication.setGoogleApiClient(mGoogleApiClient);

        mGoogleSingInButton = (SignInButton) findViewById(R.id.googleSignInButton);
        mButtonCancelGoogleConnection = (Button) findViewById(R.id.buttonCancelGoogleConnection);
        mProgressBarGoogleConnection = (ProgressBar) findViewById(R.id.progressBarGoogleConnection);
        mTextViewGoogleConnecting = (TextView) findViewById(R.id.textViewGoogleConnecting);

        // Cannot use the xml file :
        // https://developers.google.com/android/reference/com/google/android/gms/common/SignInButton
        mGoogleSingInButton.setOnClickListener(this);
    }

    /**
     * onStart method for the activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        connect();
    }

    /**
     * onStop method for the activity.
     */
    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }

    /**
     * User clicked the sign-in button, so begin the sign-in process and automatically
     * attempt to resolve any errors that occur.
     */
    private void onSignInClicked() {
        connect();
    }

    /**
     * onConnected indicates that an account was selected on the device, that the selected
     * account has granted any requested permissions to our app and that we
     * were able to
     * establish a service connection to Google Play services.
     * @param bundle The bundle.
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        Intent intent = new Intent(this, LoggingInActivity.class);
        startActivity(intent);
    }

    /**
     * The connection to Google Play services was lost. The GoogleApiClient will automatically
     * attempt to re-connect. Any UI elements that depend on connection t
     * Google APIs should
     * be hidden or disabled until onConnected is called again.
     * @param unused
     */
    @Override
    public void onConnectionSuspended(int unused) {
        connect();
    }

    /**
     * Could not connect to Google Play Services.  The user needs to select an account,
     * grant permissions or resolve an error in order to sign in. Refer to the
     * javadoc for ConnectionResult to see possible error codes.
     * @param connectionResult result of the connection.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                disconnect();
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    /**
     * Method called when the connection has ended, successful or not
     * @param requestCode The code for the request
     * @param resultCode Code indicating if the operation was successful or not
     * @param data The data containing the information on the connection
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            connect();
        }
    }


    /**
     * Connect to the google servers
     */
    private void connect(){
        mGoogleSingInButton.setVisibility(View.INVISIBLE);
        mTextViewGoogleConnecting.setVisibility(View.VISIBLE);
        mButtonCancelGoogleConnection.setVisibility(View.VISIBLE);
        mProgressBarGoogleConnection.setVisibility(View.VISIBLE);

        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    /**
     * Disconnect from the google servers
     */
    private void disconnect(){
        mGoogleSingInButton.setVisibility(View.VISIBLE);
        mTextViewGoogleConnecting.setVisibility(View.INVISIBLE);
        mButtonCancelGoogleConnection.setVisibility(View.INVISIBLE);
        mProgressBarGoogleConnection.setVisibility(View.INVISIBLE);

        mGoogleApiClient.disconnect();
    }

    /** Called when the user clicks the "Cancel" button */
    public void cancelConnection(View view) {
        disconnect();
    }
}
