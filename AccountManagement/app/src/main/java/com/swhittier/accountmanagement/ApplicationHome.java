package com.swhittier.accountmanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.swhittier.accountmanagement.authentication.AccountHelper;

/**
 * Class Name: ApplicationHome
 * Description: Code for the ApplicationHome Activity
 */
public class ApplicationHome extends Activity {

    //region Private Variables

    private Context _context = null;

    //endregion

    //region Constants

    static final String CLASS_TAG = "ApplicationHome";

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onCreate()";

        Log.d(METHOD_TAG, "Creating ApplicationHome Activity.");

        _context = getApplicationContext();
    }

    @Override
    protected void onRestart() {

        super.onRestart();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onRestart()";

        Log.d(METHOD_TAG, "Restarting ApplicationHome Activity.");
    }

    @Override
    protected void onResume() {

        super.onResume();

        String METHOD_TAG;
        METHOD_TAG = CLASS_TAG + ".onResume()";

        Log.d(METHOD_TAG, "Resuming ApplicationHome Activity.");

        // It is possible that the app has been resumed here after the user deleted their account.
        // Check here to make sure they still have an account.  If not, go back to the Welcome Activity
        // and start over.

        if(!AccountHelper.getInstance(_context).accountExists()) {

            Toast toast = Toast.makeText(_context, _context.getString(R.string.err_msg_no_accounts), Toast.LENGTH_SHORT);
            toast.show();

            startWelcomeActivity();

            return;
        }

        setContentView(R.layout.activity_application_home);
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
        getMenuInflater().inflate(R.menu.menu_application_home, menu);
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

    //region Private Methods

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
}
