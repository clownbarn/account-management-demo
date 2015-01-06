package com.swhittier.accountmanagement.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.swhittier.accountmanagement.R;

import java.io.IOException;

/**
 * Class Name: AccountHelper.
 * Description: Singleton class containing common methods for working with Accounts.
 */
public class AccountHelper {

    //region Private Variables

    private AccountManager _accountManager = null;
    private Context _context = null;

    //endregion

    //region Constants

    static final String CLASS_TAG = "AccountHelper";

    public static final String ARG_CONFIRMCREDENTIALS = "CONFIRMCREDENTIALS";

    /**
     * Account name
     */
    public static final String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "com.swhittier.accountmanagement";
    public static final String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";

    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    /**
     * Auth Token
     */
    public static final String AUTH_TOKEN = "swhittier_auth_token";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to a com.swhittier.accountmanagement account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to a com.swhittier.accountmanagement account";

    //endregion

    //region Singleton Implementation (not thread safe)

    private static AccountHelper _instance = null;

    // Private constructor to prevent instantiation.

    /**
     * Constructor: Private to prevent instantiation.
     * @param c Application Context
     */
    private AccountHelper(Context c) {

        _accountManager = AccountManager.get(c);
        _context = c;
    }

    /**
     * Singleton accessor.
     * @param c Application Context
     * @return Single instance of AccountHelper
     */
    public static AccountHelper getInstance(Context c) {

        if(_instance == null) {
            _instance = new AccountHelper(c);
        }

        return _instance;
    }

    //endregion

    //region Public Methods

    /**
     * Adds an account directly to the AccountManager.
     * This method requires the caller to hold the permission AUTHENTICATE_ACCOUNTS.
     * @param account The Account to add.
     * @param password The password to associate with the account.
     * @param userdata String values to use for the account's userdata, null for none.
     * @return true if the Account was added, otherwise false.
     */
    public boolean addAccountExplicitly (Account account, String password, Bundle userdata){

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".accountExists()";

        Log.d(METHOD_TAG, "Adding Account: " + account.name);

        boolean bAdded;
        bAdded = _accountManager.addAccountExplicitly(account, password, userdata);

        if(bAdded) {

            Log.d(METHOD_TAG, "Account added: " + account.name);

            Toast toast = Toast.makeText(_context, _context.getString(R.string.msg_account_created), Toast.LENGTH_LONG);
            toast.show();
        }
        else {

            Log.e(METHOD_TAG, "Failed to add Account: " + account.name);

            Toast toast = Toast.makeText(_context, _context.getString(R.string.err_account_not_created), Toast.LENGTH_LONG);
            toast.show();
        }

        return bAdded;
    }

    /**
     * Determines if an Account has already been created on the device.
     * This method requires the caller to hold the permission GET_ACCOUNTS.
     * @return true if an Account exists, otherwise false.
     */
    public boolean accountExists() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".accountExists()";

        Log.d(METHOD_TAG, "Determining if Account exists.");

        boolean bAccountExists;

        final Account[] availableAccounts;
        availableAccounts = _accountManager.getAccountsByType(ACCOUNT_TYPE);

        bAccountExists = availableAccounts.length != 0;

        if(bAccountExists)
            Log.d(METHOD_TAG, "Account exists.");
        else
            Log.d(METHOD_TAG, "Account does not exist.");

        return bAccountExists;
    }

    /**
     * Gets the Account if one has been created.
     * This method requires the caller to hold the permission GET_ACCOUNTS.
     * @return An Account instance if an Account is found, otherwise null.
     */
    public Account getAccount() {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".getAccount()";

        Log.d(METHOD_TAG, "Getting Account.");

        final Account[] availableAccounts;
        availableAccounts = _accountManager.getAccountsByType(ACCOUNT_TYPE);

        if(availableAccounts.length != 0){

            Account account;
            account = availableAccounts[0];

            Log.d(METHOD_TAG, "Account found. Name: " + account.name);

            return account;
        }
        else {

            Log.d(METHOD_TAG, "Account not found.");
            return null;
        }
    }

    /**
     * Gets the password for a given Account.
     * This method requires the caller to hold the permission AUTHENTICATE_ACCOUNTS.
     * @param account The Account to get the password from.
     * @return The password for the Account if it is found, otherwise null.
     */
    public String getPassword(Account account) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".getPassword()";

        Log.d(METHOD_TAG, "Getting Password for Account: " + account.name);

        String password;
        password = _accountManager.getPassword(account);

        if(password != null && password.length() != 0)
            Log.d(METHOD_TAG, "Password found for Account: " + account.name);
        else
            Log.d(METHOD_TAG, "Password not found for Account: " + account.name);

        return password;
    }

    /**
     * Determines if a given user name is valid.
     * This method requires the caller to hold the permission GET_ACCOUNTS.
     * @param userName The user name to validate.
     * @return true if the user name is valid, otherwise false.
     */
    public boolean isValidUser(String userName) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".isValidUser()";

        Log.d(METHOD_TAG, "Validating User Name: " + userName);

        Account account;
        account = this.getAccount();

        if(account == null) {

            // No Account.  Could have been deleted.  Should not happen, but if it does,
            // App will show UserRegistration Activity.
            Log.d(METHOD_TAG, "No Accounts found.");

            Toast toast = Toast.makeText(_context, _context.getString(R.string.err_msg_no_accounts), Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }

        if(!account.name.equals(userName)) {

            // No Account for the user name provided.
            Log.d(METHOD_TAG, "No Account found for user: " + userName);

            Toast toast = Toast.makeText(_context, String.format(_context.getString(R.string.err_msg_no_account_for_user), userName), Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }

        Log.d(METHOD_TAG, "User name: " + userName + " is valid.");

        return true;
    }

    /**
     * Gets an auth token of a specified type for an Account.
     * This method requires the caller to hold the permission USE_CREDENTIALS.
     * Note: This method cannot be called from the main thread.
     * @param account The account to fetch an auth token for.
     * @param authTokenType The auth token type, an authenticator-dependent string token, must not be null.
     * @param options Authenticator-specific options for the request, may be null or empty.
     * @param activity The Activity context to use for launching a new authenticator-defined sub-Activity to
     *                 prompt the user for a password if necessary; used only to call startActivity(); must not be null.
     * @param callback Callback to invoke when the request completes, null for no callback.
     * @param handler Handler identifying the callback thread, null for the main thread.     *
     * @return Bundle containing the results from the call. The Auth Token, if present is in the KEY_AUTHTOKEN field.
     */
    public Bundle getAuthToken (Account account, String authTokenType, Bundle options, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".getAuthToken()";

        Log.d(METHOD_TAG, "Getting Auth Token.");

        //android.os.Debug.waitForDebugger();

        final AccountManagerFuture<Bundle> future;
        future = _accountManager.getAuthToken(account, authTokenType, options, activity, callback, handler);

        Bundle result = null;

        try {

            result = future.getResult();

        } catch (OperationCanceledException e) {

            Log.e(METHOD_TAG, e.toString());

        } catch (IOException e) {

            Log.e(METHOD_TAG, e.toString());

        } catch (AuthenticatorException e) {

            Log.e(METHOD_TAG, e.toString());
        }

        return result;
    }

    /**
     * Gets an Auth Token from the AccountManager's cache for a given Account type
     * and given Auth Token type.
     * This method requires the caller to hold the permission AUTHENTICATE_ACCOUNTS.
     * Note: This method is intended to be used by the Authenticator only. The app should call getAuthToken().
     * @param account The Account to get the Auth Token from.
     * @param authTokenType The type of Auth Token to get.
     * @return The cached Auth Token for this Account and type, or null if no Auth Token is cached or the Account does not exist.
     */
    public String peekAuthToken(Account account, String authTokenType) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".peekAuthToken()";

        Log.d(METHOD_TAG, "Attempting to get Auth Token type: " + authTokenType + " for Account: " + account.name);

        String authToken;
        authToken = _accountManager.peekAuthToken(account, authTokenType);

        if(authToken != null && authToken.length() != 0)
            Log.d(METHOD_TAG, "Auth Token type: " + authTokenType + " found for Account: " + account.name);
        else
            Log.d(METHOD_TAG, "Auth Token type: " + authTokenType + " not found for Account: " + account.name);

        return authToken;
    }

    /**
     * Adds an auth token to the AccountManager cache for an account.
     * @param account The account to set an auth token for.
     * @param authTokenType The type of the auth token.
     * @param authToken The auth token to add to the cache.
     */
    public void setAuthToken (Account account, String authTokenType, String authToken) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".setAuthToken()";

        Log.d(METHOD_TAG, "Attempting to set Auth Token for Account: " + account.name);

        _accountManager.setAuthToken(account, authTokenType, authToken);

        Log.d(METHOD_TAG, "Auth Token set for Account: " + account.name);
    }

    /**
     * This method requires the caller to hold the permission MANAGE_ACCOUNTS or USE_CREDENTIALS.
     * @param accountType The Account type of the Auth Token to invalidate, must not be null.
     * @param authToken The Auth Token to invalidate, may be null.
     */
    public void invalidateAuthToken (String accountType, String authToken) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".invalidateAuthToken()";

        Log.d(METHOD_TAG, "Invalidating Auth Token.");

        _accountManager.invalidateAuthToken(accountType, authToken);

        Log.d(METHOD_TAG, "Auth Token invalidated.");
    }

    /**
     * Confirms that the user knows the password for an account to make extra sure they are the owner
     * of the account.
     * This method requires the caller to hold the permission MANAGE_ACCOUNTS and GET_ACCOUNTS.
     * @param password The password to validate.
     * @return An AccountManagerFuture which resolves to a Bundle.  The result will be in the KEY_BOOLEAN_RESULT field.
     */
    public AccountManagerFuture<Bundle> validatePassword (String password) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".confirmCredentials()";

        Log.d(METHOD_TAG, "Confirming credentials with Account Manager.");

        Account account;
        account = this.getAccount();

        Bundle options;
        options = new Bundle();
        options.putString(AccountManager.KEY_PASSWORD, password);

        return _accountManager.confirmCredentials(account, options, null, null, null);
    }

    //endregion
}
