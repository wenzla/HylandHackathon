package io.github.wenzla.choremate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "Roomates";
    private EditText numroom;
    CallbackManager callbackManager = CallbackManager.Factory.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView tv = findViewById(R.id.textView);
        setSupportActionBar(toolbar);

        Button nextScreen = (Button) findViewById(R.id.button2);
        nextScreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int button = 2;

                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                EditText txtname = (EditText)findViewById(R.id.editText);
                String name =  txtname.getText().toString();

                button = Integer.parseInt(name);
                editor.putInt("roomnum", button);
                editor.apply();
                Intent i = new Intent(MainActivity.this, RoommateActivity.class);
                startActivity(i);
            }
        });

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String UserID = AccessToken.getCurrentAccessToken().getUserId();
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/"+UserID,
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                if (response != null && response.getJSONObject() != null && response.getJSONObject().has("name"))
                                {
                                    try {
                                        String s = (response.getJSONObject().getString("name"));
                                        setFBName(s);
                                    } catch (JSONException e) {
                                        Log.d("FB", "Could not contact Facebook server");
                                    }
                                }
                            }

                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }

        });
        if(isLoggedIn()){
            String UserID = AccessToken.getCurrentAccessToken().getUserId();
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+UserID,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            if (response != null && response.getJSONObject() != null && response.getJSONObject().has("name"))
                            {
                                try {
                                    String s = (response.getJSONObject().getString("name"));
                                    setFBName(s);
                                } catch (JSONException e) {
                                    Log.d("FB", "Could not contact Facebook server");
                                }
                            }
                        }

                    }
            ).executeAsync();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void setFBName(String name) {
        TextView b = findViewById(R.id.textView);
        b.setText(name);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


}
