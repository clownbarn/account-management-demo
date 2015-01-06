package com.swhittier.accountmanagement.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.swhittier.accountmanagement.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Name: PasswordValidator
 * Description: The PasswordValidator class provides methods for validating passwords
 * using Regular Expressions.
 */
public class PasswordValidator {

    //region Private Variables

    private Context _context = null;
    private Pattern pattern;
    private Matcher matcher;

    //endregion


    //region Private Constants

    static final String CLASS_TAG = "PasswordValidator";
    static final int MAX_LENGTH = 20;
    static final int MIN_LENGTH = 6;

    // Default pattern: Alphanumeric with at least 1 digit, 1 uppercase, and a length between 6 and 20
    private static final String DEFAULT_PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{" + MIN_LENGTH + "," + MAX_LENGTH + "})";

    //endregion

    //region Constructor

    public PasswordValidator(Context c){

        _context = c;

        // TODO: Make this class more flexible to allow for different patterns and lengths?
        pattern = Pattern.compile(DEFAULT_PASSWORD_PATTERN);
    }

    //endregion

    //region Public Methods

    /**
     * Validates a password with regular expression and
     * checks whether confirmation password is equal to password.
     * @param password The password to validate
     * @param confirmPassword The confirmation password to validate.
     * @return true if the password is valid, otherwise false
     */
    public boolean validate(final String password, final String confirmPassword){

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".validate(String, String)";

        if(!validate(password)) {

            return false;
        }

        if(!password.equals(confirmPassword)) {

            Log.d(METHOD_TAG, "Invalid Password. Reason: password and confirmation password are not the same.");

            Toast toast = Toast.makeText(_context, _context.getString(R.string.err_msg_password_confirm_mismatch), Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }

        return true;
    }

    /**
     * Validates a password with regular expression
     * @param password
     * @return
     */
    public boolean validate(final String password){

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".validate(String)";

        matcher = pattern.matcher(password);

        if(!matcher.matches()) {

            Log.d(METHOD_TAG, "Invalid Password. Reason: failed to meet strength requirement.");

            Toast toast = Toast.makeText(_context, String.format(_context.getString(R.string.err_msg_password_strength), MIN_LENGTH, MAX_LENGTH), Toast.LENGTH_LONG);
            toast.show();

            return false;
        }

        return true;
    }

    //endregion
}
