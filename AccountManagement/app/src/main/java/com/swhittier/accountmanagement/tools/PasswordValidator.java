package com.swhittier.accountmanagement.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Name: PasswordValidator
 * Description: The PasswordValidator class provides methods for validating passwords
 * using Regular Expressions.
 */
public class PasswordValidator {

    //region Private Variables

    private Pattern pattern;
    private Matcher matcher;

    //endregion


    //region Private Constants

    // Default pattern: Alphanumeric with at least 1 digit, 1 uppercase, and a length between 6 and 20
    private static final String DEFAULT_PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

    //endregion

    //region Constructor

    public PasswordValidator(){

        // TODO: Make this class more flexible to allow for different patterns and lengths?
        pattern = Pattern.compile(DEFAULT_PASSWORD_PATTERN);
    }

    //endregion

    //region Public Methods

    /**
     * Validates a password with regular expression
     * @param password The password to validate
     * @return true if the password is valid, otherwise false
     */
    public boolean validate(final String password){

        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    //endregion
}
