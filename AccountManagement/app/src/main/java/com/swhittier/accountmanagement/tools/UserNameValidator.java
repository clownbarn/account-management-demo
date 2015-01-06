package com.swhittier.accountmanagement.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.swhittier.accountmanagement.R;

/**
 * Class Name: UserNameValidator
 * Description: The UserNameValidator class provides methods for validating user names.
 */
public class UserNameValidator {

    //region Private Variables

    private Context _context = null;

    //endregion

    //region Constants

    static final String CLASS_TAG = "UserNameValidator";
    static final int MAX_LENGTH = 20;
    static final int MIN_LENGTH = 6;

    //endregion

    //region Constructor

    public UserNameValidator( Context c){

        _context = c;
    }

    //endregion

    //region Public Methods

    /**
     * Validates a user name
     * @param username The user name to validate
     * @return true if the user name is valid, otherwise false
     */
    public boolean validate(final String username){

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".validate()";

        if(null == username || username.isEmpty()) {

            Log.d(METHOD_TAG, "Invalid User Name. Reason: blank.");

            Toast toast = Toast.makeText(_context, _context.getString(R.string.err_msg_username_blank), Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }

        if(username.length() > MAX_LENGTH) {

            Log.d(METHOD_TAG, "Invalid User Name. Reason: exceeds maximum length of: " + MAX_LENGTH);

            Toast toast = Toast.makeText(_context, String.format(_context.getString(R.string.err_msg_username_exceeds_max_len), MAX_LENGTH), Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }

        if(username.length() < MIN_LENGTH) {

            Log.d(METHOD_TAG, "Invalid User Name. Reason: does not meet minimum length of: " + MIN_LENGTH);

            Toast toast = Toast.makeText(_context, String.format(_context.getString(R.string.err_msg_username_under_min_len), MIN_LENGTH), Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }

        return true;
    }

    //endregion
}
