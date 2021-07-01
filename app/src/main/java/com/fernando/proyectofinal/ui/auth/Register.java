package com.fernando.proyectofinal.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.CreateActivity;
import com.fernando.proyectofinal.CustomUtil;
import com.fernando.proyectofinal.LoginActivity;
import com.fernando.proyectofinal.MainActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Person;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.entities.User;
import com.fernando.proyectofinal.ui.dialogs.AlertDialogResult;
import com.fernando.proyectofinal.ui.dialogs.TagDialogFragment;

import java.util.List;

import static android.content.ContentValues.TAG;

public class Register extends Fragment{

    public static final String TAG = "SignUpForm";

    private User mUser;
    private DatabaseManager mDatabaseManager;

    // Views
    private EditText mEditUsername;
    private EditText mEditPassword;
    private Switch mAdminUser;
    private TextView mGoLoginTxt;
    private Button mBtnSubmit;

    public Register() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mDatabaseManager = DatabaseManager.getInstance(getActivity());

        mEditUsername = view.findViewById(R.id.editRegisterUsername);
        mEditPassword = view.findViewById(R.id.editRegisterPassword);
        mAdminUser = view.findViewById(R.id.adminSwitch);
        mGoLoginTxt = view.findViewById(R.id.loginLink);
        mBtnSubmit = view.findViewById(R.id.btnRegister);

        mBtnSubmit.setOnClickListener(button -> createAccount());
        mGoLoginTxt.setOnClickListener(button -> startLogin());

        return view;
    }

    private void createAccount() {
        String username = mEditUsername.getText().toString();
        String password = mEditPassword.getText().toString();
        int level = mAdminUser.isChecked() ? User.USER_ADMIN : User.USER_NORMAL;

        mUser = new User(username, password, level);

        if (mUser.validar() && mUser.authenticate(mDatabaseManager, LoginActivity.REGISTER)) {
            Toast.makeText(getActivity(), "Cuenta creada", Toast.LENGTH_SHORT).show();
            CustomUtil.storeUserInPrefs(mUser, getActivity());
            DatabaseManager.setUserId(mUser.getId());

            startActivity(new Intent(getActivity(), MainActivity.class));

        } else {
            for (String errorMsg : mUser.getErrors()) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLogin() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.putExtra(LoginActivity.AUTH_ACTION, LoginActivity.LOGIN);
        startActivity(i);
    }
}