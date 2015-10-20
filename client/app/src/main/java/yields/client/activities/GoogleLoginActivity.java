package yields.client.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import yields.client.R;

public class GoogleLoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
    }

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

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d("Login log", "onConnected:" + bundle);
        mShouldResolve = false;

        Intent intent = new Intent(this, SelectUsernameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost. The GoogleApiClient will automatically
        // attempt to re-connect. Any UI elements that depend on connection to Google APIs should
        // be hidden or disabled until onConnected is called again.

        connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d("Login error", "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e("Login error", "Could not resolve ConnectionResult.", e);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Login log", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            connect();
        }
    }


    private void connect(){
        findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.textViewGoogleConnecting).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonCancelGoogleConnection).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBarGoogleConnection).setVisibility(View.VISIBLE);

        mGoogleApiClient.connect();
    }

    private void disconnect(){
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.textViewGoogleConnecting).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonCancelGoogleConnection).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressBarGoogleConnection).setVisibility(View.INVISIBLE);

        mGoogleApiClient.disconnect();
    }

    /** Called when the user clicks the "Cancel" button */
    public void cancelConnection(View view) {
        disconnect();
    }
}