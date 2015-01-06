package com.swhittier.accountmanagement;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.swhittier.accountmanagement.authentication.AccountHelper;

import java.io.IOException;

/**
 * Class Name: UserLogin
 * Description: Code for the UserLogin Activity
 */
public class UserLogin extends Activity {

    //region Private Variables

    private Context _context = null;
    private PasswordValidationTask _passwordValidatorTask = null;
    private String _password = null;
    AccountManagerFuture<Bundle> _validatePasswordFuture = null;

    //endregion

    //region Constants

    static final String CLASS_TAG = "UserLogin";

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onCreate()";

        Log.d(METHOD_TAG, "Creating UserLogin Activity.");

        _context = getApplicationContext();
    }

    @Override
    protected void onRestart() {

        super.onRestart();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onRestart()";

        Log.d(METHOD_TAG, "Restarting UserLogin Activity.");
    }

    @Override
    protected void onResume() {

        super.onResume();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onResume()";

        Log.d(METHOD_TAG, "Resuming UserLogin Activity.");

        // It is possible that the app has been resumed here after the user deleted their account.
        // Check here to make sure they still have an account.  If not, go back to the Welcome Activity
        // and start over.

        if(!AccountHelper.getInstance(_context).accountExists()) {

            Toast toast = Toast.makeText(_context, _context.getString(R.string.err_msg_no_accounts), Toast.LENGTH_SHORT);
            toast.show();

            startWelcomeActivity();

            return;
        }

        setContentView(R.layout.activity_user_login);
    }

    @Override
    protected void onStart() {

        super.onStart();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onStart()";

        Log.d(METHOD_TAG, "Starting UserLogin Activity.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_login, menu);
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
     * Handler for the click event for the "Log In" button.
     * @param v
     */
    public void onButtonLogInClick(View v) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onButtonLogInClick()";

        //
        // Validate the user name.
        //
        EditText userNameEditText;
        userNameEditText = (EditText)findViewById(R.id.editTextLoginUserName);

        String userName;
        userName = userNameEditText.getText().toString();

        Log.d(METHOD_TAG, "Validating User Name.");

        if(!AccountHelper.getInstance(_context).isValidUser(userName))
            return;

        //
        // Validate the password.
        //
        EditText passwordEditText;
        passwordEditText = (EditText)findViewById(R.id.editTextLoginPassword);

        _password = passwordEditText.getText().toString();

        Log.d(METHOD_TAG, "Validating Password.");

        _validatePasswordFuture = AccountHelper.getInstance(_context).validatePassword(_password);

        _passwordValidatorTask = new PasswordValidationTask();
        _passwordValidatorTask.execute();

        return;
    }

    //endregion

    //region Private Methods

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

    /**
     * Starts the Welcome Activity.
     */
    private void startWelcomeActivity() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".startWelcomeActivity()";

        Log.d(METHOD_TAG, "Starting Welcome Activity.");

        this.finish();

        Intent myIntent = new Intent(this, WelcomeActivity.class);
        this.startActivity(myIntent);
    }

    //endregion


    //region Password Validation AsyncTask

    /**
     * Callback for onPostExecute() of PasswordValidationTask
     * @param result The result of the password validation.
     */
    public void onPasswordValidationResult(Boolean result) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onPasswordValidationResult()";

        Log.d(METHOD_TAG, "Password validation task is complete. Result: " + result);

        // Our task is complete, so clear it out.
        _passwordValidatorTask = null;

        if(result) {

            // User Name and Password validation was successful. Set Auth Token and start Application Home Activity.
            Log.d(METHOD_TAG, "Password validation was successful. Setting Auth Token and starting application.");

            Account account = AccountHelper.getInstance(_context).getAccount();

            AccountHelper.getInstance(_context).setAuthToken(account, AccountHelper.AUTHTOKEN_TYPE_FULL_ACCESS, AccountHelper.AUTH_TOKEN);

            startApplicationHomeActivity();
        }
        else
        {
            Toast toast = Toast.makeText(_context, _context.getString(R.string.err_msg_invalid_password), Toast.LENGTH_SHORT);
            toast.show();
        }

        return;
    }

    /**
     * Callback for onCancelled() of PasswordValidationTask
     */
    public void onPasswordValidationCancel() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onPasswordValidationCancel()";

        Log.d(METHOD_TAG, "Password validation task was cancelled.");

        // Our task is complete, so clear it out.
        _passwordValidatorTask = null;

        return;
    }

    /**
     * Class Name: PasswordValidationTask
     * Description: Represents an asynchronous task used to validate a user's password.
     */
    private class PasswordValidationTask extends AsyncTask<Void, Void, Boolean> {

        //region Constants

        static final String CLASS_TAG = "PasswordValidationTask";

        //endregion

        //region Overrides

        @Override
        protected Boolean doInBackground(Void... params) {

            String METHOD_TAG;
            METHOD_TAG = CLASS_TAG + ".doInBackground()";

            boolean bValid;
            bValid = false;

            try {

                Log.d(METHOD_TAG, "Getting result from call to AccountManager.confirmCredentials()");

                Bundle result;
                result = _validatePasswordFuture.getResult();
                bValid = result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT);

            } catch (OperationCanceledException e) {

                Log.e(METHOD_TAG, e.toString());

            } catch (IOException e) {

                Log.e(METHOD_TAG, e.toString());

            } catch (AuthenticatorException e) {

                Log.e(METHOD_TAG, e.toString());
            }

            if(bValid)
                Log.d(METHOD_TAG, "Password is valid.");
            else
                Log.d(METHOD_TAG, "Password is invalid.");

            return bValid;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            String METHOD_TAG;
            METHOD_TAG = CLASS_TAG + ".onPostExecute()";

            Log.d(METHOD_TAG, "Call to validate password successful. Returning result to UI thread.");

            // Return the password validation result to the Activity.
            onPasswordValidationResult(result);
        }

        @Override
        protected void onCancelled() {

            // If the action was canceled , then call back into the
            // Activity to let it know.
            onPasswordValidationCancel();
        }

        //endregion
    }

    //endregion
}
