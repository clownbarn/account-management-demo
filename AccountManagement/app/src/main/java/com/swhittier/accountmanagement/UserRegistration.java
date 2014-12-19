package com.swhittier.accountmanagement;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Class Name: UserRegistation
 * Description: Code for the UserRegistion Activity
 */
public class UserRegistration extends Activity {

    //region Constants

    static final String CLASS_TAG = "UserRegistration";

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
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

        Log.d(METHOD_TAG, "Validating User Name.");

        validateUserName();
    }

    //endregion

    //region Private Methods

    /**
     * Validates the User Name.
     */
    private void validateUserName(){

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".validateUserName()";

        EditText newUserNameEditText;
        newUserNameEditText = (EditText)findViewById(R.id.editTextNewUserName);

        String newUserName;
        newUserName = newUserNameEditText.getText().toString();

        if(null == newUserName || newUserName.isEmpty())
            Log.d(METHOD_TAG, "Invalid User Name.  Reason: blank.");
    }

    //endregion
}
