package com.swhittier.accountmanagement.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.swhittier.accountmanagement.UserLogin;
import com.swhittier.accountmanagement.UserRegistration;

/**
 * Class Name: AccountAuthenticator
 * Description: Class that extends AbstractAccountAuthenticator
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

    //region Private Variables

    private Context _context;

    //endregion

    //region Constants

    static final String CLASS_TAG = "AccountAuthenticator";

    //endregion


    //region Constructor

    public AccountAuthenticator(Context context) {

        super(context);
        _context = context;
    }

    //endregion

    //region Overrides

    /**
     * Called from the system to add an account of the specified accountType.
     * Invokes the Application's registration UI, which will create the account explicitly.
     * @param response The response to send the result back to the AccountManager, will never be null.
     * @param accountType The type of account to add, will never be null (defined in authenticator.xml).
     * @param authTokenType The type of auth token to retrieve after adding the account, may be null.
     * @param requiredFeatures A String array of authenticator-specific features that the added account must support, may be null.
     * @param options A Bundle of authenticator-specific options, may be null.
     * @return a Bundle result or null if the result is to be returned via the response. The result will contain either:
     *   KEY_INTENT, or
     *   KEY_ACCOUNT_NAME and KEY_ACCOUNT_TYPE of the account that was added, or
     *   KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error
     * @throws NetworkErrorException
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".addAccount()";

        Log.d(METHOD_TAG, "Adding account.");

        final Intent intent;
        final Bundle bundle;

        intent = new Intent(_context, UserRegistration.class);
        intent.putExtra(AccountHelper.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountHelper.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountHelper.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    /**
     * Checks that the user knows the credentials of an account.
     * @param response The response to send the result back to the AccountManager, will never be null.
     * @param account The account whose credentials are to be checked, will never be null.
     * @param options A Bundle of authenticator-specific options, may be null.  If verifying a password, must contain AccountManager.KEY_PASSWORD.
     * @return A Bundle result or null if the result is to be returned via the response. The result will contain either:
     *   KEY_INTENT, or
     *   KEY_BOOLEAN_RESULT, true if the check succeeded, false otherwise
     *   KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error
     * @throws NetworkErrorException
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".confirmCredentials()";

        if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {

            Log.d(METHOD_TAG, "Password sent to authenticator. Validating Password.");

            final String passwordEntered;
            passwordEntered = options.getString(AccountManager.KEY_PASSWORD);

            final String accountPassword;
            accountPassword = AccountHelper.getInstance(_context).getPassword(account);

            final boolean bValid;
            bValid = passwordEntered.equals(accountPassword);

            Log.d(METHOD_TAG, "Password validated. Result: " + bValid);

            final Bundle bundle;
            bundle = new Bundle();

            bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, bValid);

            return bundle;
        }

        // Launch UserLogin to confirm credentials. This will be the case if confirmCredentials is called from elsewhere besides the UserLogin Activity
        final Intent intent;
        intent = new Intent(_context, UserLogin.class);

        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountHelper.ARG_ACCOUNT_NAME, account.name);
        intent.putExtra(AccountHelper.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AccountHelper.ARG_CONFIRMCREDENTIALS, true);

        final Bundle bundle;
        bundle = new Bundle();

        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".getAuthToken()";

        Log.d(METHOD_TAG, "Getting Auth Token for Account: " + account.name);

        String authToken = AccountHelper.getInstance(_context).peekAuthToken(account, authTokenType);

        // If we get an authToken - we return it
        if (authToken != null && authToken.length() != 0) {

            Log.d(METHOD_TAG, "Auth Token found for Account: " + account.name);

            final Bundle result;
            result = new Bundle();

            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);

            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our UserLogin Activity.
        Log.d(METHOD_TAG, "Auth Token not found for Account: " + account.name + ". Authentication required.");

        // TODO: Take the getAuthToken method call out of the AsyncTask so the system can start this Activity on the main thread.
//        final Intent intent;
//        intent = new Intent(_context, UserLogin.class);
//
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
//        intent.putExtra(AccountHelper.ARG_ACCOUNT_NAME, account.name);
//        intent.putExtra(AccountHelper.ARG_ACCOUNT_TYPE, account.type);
//        intent.putExtra(AccountHelper.ARG_AUTH_TYPE, authTokenType);

        final Bundle bundle;
        bundle = new Bundle();

//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".editProperties()";

        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".getAuthTokenLabel()";

        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".updateCredentials()";

        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".hasFeatures()";

        return null;
    }

    //endregion
}
