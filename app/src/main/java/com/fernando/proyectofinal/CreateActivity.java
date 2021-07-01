package com.fernando.proyectofinal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.ui.forms.GardenForm;
import com.fernando.proyectofinal.ui.forms.ItemForm;
import com.fernando.proyectofinal.ui.forms.PersonForm;
import com.fernando.proyectofinal.ui.forms.WeatherForm;

import java.util.Objects;

public class CreateActivity extends AppCompatActivity {

    public final static String ACTION = "ACTION";
    public final static String RESOURCE = "RESOURCE";

    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable actionBarColor = new ColorDrawable(getColor(R.color.green));
        actionBar.setBackgroundDrawable(actionBarColor);

        Intent i = getIntent();
        ResourceType resource = (ResourceType) i.getSerializableExtra(RESOURCE);
        ActionType action = (ActionType) i.getSerializableExtra(ACTION);
        String returnActivity = "";
        long id = i.getLongExtra(DbHelper._ID, -1);

        String resourceName = "";
        String actionName = action == ActionType.CREATE ? getString(R.string.title_action_create) : getString(R.string.title_action_edit);
        Fragment formFragment = new PersonForm();

        switch (Objects.requireNonNull(resource)) {
            case PERSON:
                formFragment = new PersonForm();
                resourceName = getString(R.string.resource_name_person);
                break;
            case GARDEN:
                formFragment = new GardenForm();
                resourceName = getString(R.string.resource_name_garden);
                returnActivity = i.getStringExtra(GardenForm.RETURN_TO);
                break;
            case WEATHER:
                formFragment = new WeatherForm();
                resourceName = getString(R.string.resource_name_weather);
                break;
            case ARTICLE:
                formFragment = new ItemForm();
                resourceName = "articulo";
                break;
            case PLANT:
                formFragment = new ItemForm();
                resourceName = "planta";
                break;
        }

        actionBar.setTitle(actionName + " " + resourceName);

        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTION, action);
        bundle.putSerializable(RESOURCE, resource);
        bundle.putLong(DbHelper._ID, id);
        bundle.putString(GardenForm.RETURN_TO, returnActivity);
        formFragment.setArguments(bundle);

        if(savedInstanceState == null && findViewById(R.id.create_form_fragment) != null) {
            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.create_form_fragment, formFragment, null)
                .commit();
        }
    }
}