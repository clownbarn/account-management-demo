package com.swhittier.accountmanagement;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.swhittier.accountmanagement.authentication.AccountAuthenticator;
import com.swhittier.accountmanagement.authentication.AccountHelper;
import com.swhittier.accountmanagement.tools.PasswordValidator;
import com.swhittier.accountmanagement.tools.UserNameValidator;

/**
 * Class Name: UserRegistration
 * Description: Code for the UserRegistration Activity
 */
public class UserRegistration extends AccountAuthenticatorActivity {

    //region Private Variables

    private Context _context = null;

    //endregion

    //region Constants

    static final String CLASS_TAG = "UserRegistration";

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        _context = getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_registration, menu);
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
     * Handler for the click event for the "Create Account" button.
     * @param v
     */
    public void onButtonCreateAccountClick(View v) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onButtonCreateAccountClick()";

        // Check to see if an Account has already been created.  If so, notify the user and return.
        // This check is here in the event the UI is presented via the Settings applet.
        if(AccountHelper.getInstance(this).accountExists()) {

            Toast toast = Toast.makeText(_context, _context.getString(R.string.msg_account_already_exists), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //
        // Validate the user name.
        //
        EditText newUserNameEditText;
        newUserNameEditText = (EditText)findViewById(R.id.editTextNewUserName);

        String newUserName;
        newUserName = newUserNameEditText.getText().toString();

        Log.d(METHOD_TAG, "Validating User Name.");

        UserNameValidator unv = new UserNameValidator(this.getApplicationContext());

        if(!unv.validate(newUserName))
            return;

        Log.d(METHOD_TAG, "User Name Validated.");

        //
        // Validate the password.
        //
        EditText passwordEditText;
        passwordEditText = (EditText)findViewById(R.id.editTextNewPassword);

        String password;
        password = passwordEditText.getText().toString();

        EditText confirmPasswordEditText;
        confirmPasswordEditText = (EditText)findViewById(R.id.editTextNewPasswordConfirm);

        String confirmPassword;
        confirmPassword = confirmPasswordEditText.getText().toString();

        Log.d(METHOD_TAG, "Validating Password.");

        PasswordValidator pv = new PasswordValidator(this.getApplicationContext());

        if(!pv.validate(password, confirmPassword))
            return;

        Log.d(METHOD_TAG, "Password Validated.");

        // User Name and Password validation was successful.
        // Create Account.

        Log.d(METHOD_TAG, "Creating Account.");

        Intent intent;
        intent = createAccount(newUserName, password);

        if(intent != null) {

            Log.d(METHOD_TAG, "Account Created.");
            this.finish();
            this.startActivity(intent);

            // TODO: If this Activity was launched via the Settings applet via the AuthenticationService,
            // Then we should call finish() here and exit the app UI.  Otherwise, get the token, and go to the
            // Application Home screen.
            // this.finish();
        }

        return;
    }

    //endregion

    //region Private Methods

    /**
     * Creates a new Account in the Android Account Manager
     * @param userName The User Name.
     * @param password The Password.
     * @return A new Intent that can be used to start the App Home Activity.
     */
    private Intent createAccount(String userName, String password) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".createAccount()";

        // Set the Account Type string.
        String accountType;
        accountType = this.getIntent().getStringExtra(AccountHelper.ARG_ACCOUNT_TYPE);

        if (accountType == null || accountType.length() == 0) {
            accountType = AccountHelper.ACCOUNT_TYPE;
        }

        // Add the Account to the Android Account Manager.

        Log.d(METHOD_TAG, "Adding Account " + userName + " (Explicitly) to Android Account Manager.");

        final Account account;
        account = new Account(userName, accountType);

        if(AccountHelper.getInstance(_context).addAccountExplicitly(account, password, null)) {

            Log.d(METHOD_TAG, "Account: " + userName + " added to Android Account Manager.");

            // Set the Auth Token
            String authTokenType;
            authTokenType = getIntent().getStringExtra(AccountHelper.ARG_AUTH_TYPE);
            if (authTokenType == null || authTokenType.length() == 0)
                authTokenType = AccountHelper.AUTHTOKEN_TYPE_FULL_ACCESS;

            AccountHelper.getInstance(_context).setAuthToken(account, authTokenType, AccountHelper.AUTH_TOKEN);

            // Create the Intent to start the Application Home Activity
            final Intent intent;
            intent = new Intent(this, ApplicationHome.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);

            this.setAccountAuthenticatorResult(intent.getExtras());
            this.setResult(RESULT_OK, intent);

            return intent;
        }
        else {

            // Failed to add account.
            Log.e(METHOD_TAG, "Account " + userName + " was not added to Android Account Manager.");

            return null;
        }
    }

    //endregion
}
