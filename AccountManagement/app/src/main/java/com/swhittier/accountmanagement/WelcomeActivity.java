package com.swhittier.accountmanagement;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.swhittier.accountmanagement.authentication.AccountHelper;

// TODO: Remove UI elements and event handlers for this Activity, as it just acts as a gateway to the other Activites

/**
 * Class Name: WelcomeActivity
 * Description: Code for the WelcomeActivity Activity
 */
public class WelcomeActivity extends Activity {

    //region Private Variables

    private Context _context = null;
    private Account _account = null;
    private GetAuthTokenTask _getAuthTokenTask = null;

    //endregion

    //region Constants

    static final String CLASS_TAG = "WelcomeActivity";

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onCreate()";

        Log.d(METHOD_TAG, "Creating Welcome Activity.");

        _context = getApplicationContext();
    }

    @Override
    protected void onRestart() {

        super.onRestart();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onRestart()";

        Log.d(METHOD_TAG, "Restarting Welcome Activity.");
    }

    @Override
    protected void onResume() {

        super.onResume();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onResume()";

        Log.d(METHOD_TAG, "Resuming Welcome Activity.");

        if(AccountHelper.getInstance(this).accountExists()) {

            _account = AccountHelper.getInstance(_context).getAccount();

            // Uncomment this line to test invalidated token.
            // AccountHelper.getInstance(_context).invalidateAuthToken(AccountHelper.ACCOUNT_TYPE, AccountHelper.AUTH_TOKEN);

            // Get Auth Token.  This will either get a token if the current token is not invalidated
            // or present the UserLogin Activity to re-authenticate the user and set a new Auth Token.
            _getAuthTokenTask = new GetAuthTokenTask(this);
            _getAuthTokenTask.execute();

            return;
        }

        startUserRegistrationActivity();
    }

    @Override
    protected void onStart() {

        super.onStart();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onStart()";

        Log.d(METHOD_TAG, "Starting Welcome Activity.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region Event Handlers

    /**
     * Handler for the click event for the "Register" button.
     * Presents the UserRegistration Activity.
     * @param v
     */
    public void onButtonGoToRegisterUserRegistrationClick(View v) {

        startUserRegistrationActivity();
    }

    /**
     * Handler for the click event for the "Log In" button.
     * Presents the UserLogIn Activity.
     * @param v
     */
    public void onButtonGoToLoginClick(View v) {

        startUserLoginActivity();
    }

    //endregion

    //region Private Methods

    /**
     * Starts the UserRegistration Activity.
     */
    private void startUserRegistrationActivity() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".startUserRegistrationActivity()";

        Log.d(METHOD_TAG, "Starting UserRegistration Activity.");

        this.finish();

        Intent myIntent = new Intent(this, UserRegistration.class);
        this.startActivity(myIntent);
    }

    /**
     * Starts the UserLogin Activity.
     */
    private void startUserLoginActivity() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".startUserLoginActivity()";

        Log.d(METHOD_TAG, "Starting UserLogin Activity.");

        this.finish();

        Intent myIntent = new Intent(this, UserLogin.class);
        this.startActivity(myIntent);
    }

    /**
     * Starts Application Home Activity.
     */
    private void startApplicationHomeActivity() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".startApplicationHomeActivity()";

        Log.d(METHOD_TAG, "Starting Application Home Activity.");

        this.finish();

        Intent myIntent = new Intent(this, ApplicationHome.class);
        this.startActivity(myIntent);
    }

    //endregion

    //region Get Auth Token AsyncTask

    /**
     * Callback for onPostExecute() of GetAuthTokenTask
     * @param result The result of the call to get the Auth Token.
     */
    public void onGetAuthTokenResult(Bundle result) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onGetAuthTokenResult()";

        Log.d(METHOD_TAG, "Get Auth Token task is complete.");

        // Our task is complete, so clear it out.
        _getAuthTokenTask = null;

        // Check the bundle for the token.  If one is found, go to app home.
        if(result != null && result.getString(AccountManager.KEY_AUTHTOKEN) != null ) {

            Log.d(METHOD_TAG, "Retrieval of Auth Token was successful. Starting ApplicationHome Activity.");

            this.finish();

            startApplicationHomeActivity();
        }
        else {

            // TODO: remove this when the UserLogin Activity can be started by the system in getAuthToken in the Authenticator.
            Log.d(METHOD_TAG, "Retrieval of Auth Token was unsuccessful. Starting UserLogin Activity.");

            this.finish();

            startUserLoginActivity();
        }

        return;
    }

    /**
     * Callback for onCancelled() of GetAuthTokenTask
     */
    public void onGetAuthTokenCancel() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onGetAuthTokenCancel()";

        Log.d(METHOD_TAG, "Get Auth Token task was cancelled.");

        // Our task is complete, so clear it out.
        _getAuthTokenTask = null;

        return;
    }

    /**
     * Class Name: GetAuthTokenTask
     * Description: Represents an asynchronous task used to get an auth token.
     */
    private class GetAuthTokenTask extends AsyncTask<Void, Void, Bundle> {

        //region Private Variables

        private Activity _activity = null;

        //endregion

        //region Constants

        static final String CLASS_TAG = "GetAuthTokenTask";

        //endregion

        public GetAuthTokenTask(Activity a) {

            _activity = a;
        }

        //region Overrides

        @Override
        protected Bundle doInBackground(Void... params) {

            String METHOD_TAG;
            METHOD_TAG = CLASS_TAG + ".doInBackground()";

            // Get Auth Token using the AccountHelper class.
            try {

                return AccountHelper.getInstance(_context).getAuthToken(_account, AccountHelper.AUTHTOKEN_TYPE_FULL_ACCESS, null, _activity, null, null);

            } catch (Exception e) {

                Log.e(METHOD_TAG, "Failed to get Auth Token.");
                Log.e(METHOD_TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bundle result) {

            String METHOD_TAG;
            METHOD_TAG = CLASS_TAG + ".onPostExecute()";

            Log.d(METHOD_TAG, "Call to get Auth Token successful. Returning result to UI thread.");

            // Return the get Auth Token result to the Activity.
            onGetAuthTokenResult(result);
        }

        @Override
        protected void onCancelled() {

            // If the action was canceled , then call back into the
            // Activity to let it know.
            onGetAuthTokenCancel();
        }

        //endregion
    }

    //endregion
}
