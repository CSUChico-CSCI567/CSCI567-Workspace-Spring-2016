package csci567.loginactivity;

import android.accounts.Account;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.GET_ACCOUNTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "LoginActivity:" ;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_GET_ACCOUNTS = 0;
    private static final int RC_SIGN_IN = 1 ;


    /* Data from the authenticated user */
    public static AuthData mAuthData;



    /* The login button for Google */
    private SignInButton mGoogleLoginButton;

    //Make it a static reference so I can refer to it from another activity once authenticated
    public static Firebase mFirebaseRef;
    private GoogleApiClient mGoogleApiClient;


    // UI references.
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://csci567.firebaseio.com/");

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);



        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void login() {
        //Will check if can access contacts,
        //if not will launch event to get permissions and call this method again on success
        //This is a nice way around Version M or newer
        if (!mayRequestAccounts()) {
            return;
        }
        signIn();
    }

    /**
     * Method to request/check permissions for Accounts. If permissions are not already granted, will
     * launch flow to get permissions.
     * @return boolean value representing if permissions are already granted
     */
    private boolean mayRequestAccounts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(GET_ACCOUNTS)) {
            Snackbar.make(mLoginFormView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{GET_ACCOUNTS}, REQUEST_GET_ACCOUNTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{GET_ACCOUNTS}, REQUEST_GET_ACCOUNTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GET_ACCOUNTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Now that we have permissions, try to login again
                login();
            }
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }








    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }







    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(final GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            try {
                assert acct != null;
                //Log.d(TAG, acct.getIdToken());

                //Embedded AsyncTask to get Google OAuth Token
                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                    String errorMessage = null;

                    @Override
                    protected String doInBackground(Void... params) {
                        String token = "";

                        try {
                            String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                            // With the account name acquired, go get the auth token
                            Account account = new Account(acct.getEmail(),"com.google");
                            token = GoogleAuthUtil.getToken(LoginActivity.this, account, scope);
                        } catch (IOException transientEx) {
                    /* Network or server error */
                            Log.e(TAG, "Error authenticating with Google: " + transientEx);
                            errorMessage = "Network error: " + transientEx.getMessage();
                        } catch (UserRecoverableAuthException e) {
                            Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                        } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                            Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
                            errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                        }

                        return token;
                    }

                    @Override
                    protected void onPostExecute(String token) {
                        try {
                            Log.d(TAG, "OAUTH TOKEN: " + token);
                            mFirebaseRef.authWithOAuthToken("google",token , new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(AuthData authData) {
                                    Log.d(TAG, "FIREBASE AUTH");
                                    Log.d(TAG, authData.toString());

                                    /*
                                    * Add the user to the userlist on Firebase, necessary for permissions.
                                    * */
                                    mAuthData = authData;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("provider", mAuthData.getProvider());
                                    if(authData.getProviderData().containsKey("displayName")) {
                                        map.put("displayName", mAuthData.getProviderData().get("displayName").toString());
                                    }
                                    mFirebaseRef.child("users").child(mAuthData.getUid()).setValue(map);
                                    // the Google user is now authenticated with your Firebase app

                                    //Start New Activity upon login success
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);


                                }

                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {
                                    Log.d(TAG, firebaseError.toString());
                                    // there was an error
                                }
                            });
                        }
                        catch (Exception e){
                            Log.e(TAG, e.toString());
                        }
                    }
                };
                task.execute();
                /**/
            }
            catch (Exception e){
                Log.e(TAG, e.toString());
            }


            //updateUI(true);
        } else {
            showErrorDialog("Login Failed");
            // Signed out, show unauthenticated UI.
           // updateUI(false);
        }
        showProgress(false);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                showProgress(true);
                login();
                break;
            // ...
        }
    }
}

