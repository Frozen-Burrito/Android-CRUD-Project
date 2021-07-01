package com.fernando.proyectofinal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.fernando.proyectofinal.ui.auth.Login;
import com.fernando.proyectofinal.ui.auth.Register;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String AUTH_ACTION = "AUTH_ACTION";
    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String authAction = getIntent().getStringExtra(AUTH_ACTION);
        Fragment authForm;

        switch (authAction){
            case REGISTER:
                authForm = new Register();
                break;
            default:
                authForm = new Login();
                break;
        }

        if(savedInstanceState == null && findViewById(R.id.auth_form_fragment) != null) {
            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.auth_form_fragment, authForm, null)
                .commit();
        }
    }
}