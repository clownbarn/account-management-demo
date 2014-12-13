package com.swhittier.accountmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class WelcomeActivity extends Activity {

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
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
    public void onButtonGoToRegisterClick(View v) {

        //setContentView(R.layout.activity_user_registration);
        Intent myIntent = new Intent(this, UserRegistration.class);
        this.startActivity(myIntent);
    }

    /**
     * Handler for the click event for the "Log In" button.
     * Presents the UserLogIn Activity.
     * @param v
     */
    public void onButtonGoToLoginClick(View v) {

    }

    //endregion
}
