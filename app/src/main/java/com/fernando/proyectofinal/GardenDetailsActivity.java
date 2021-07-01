package com.fernando.proyectofinal;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Inventory;
import com.fernando.proyectofinal.entities.Item;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.ui.forms.GardenForm;
import com.fernando.proyectofinal.ui.home.FragmentAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class GardenDetailsActivity extends AppCompatActivity {

    public static final String TAG = "GardenDetailsActivity";

    public static final String GARDEN_ID = "GARDEN_ID";
    public static final String SELECTED_TAB = "SELECTED_TAB";
    public static final int DETAILS_TAB_INDEX = 0;
    public static final int ITEM_TAB_INDEX = 1;

    public static DatabaseManager sDatabaseManager;
    public static List<Inventory> sGardenInventory;
    public static List<Item> sGardenItems;
    public static List<Tag> sTags;

    public static long sGardenId;
    private int mDefaultTab;

    private TabLayout mPageTabs;
    private ViewPager2 mViewPager;
    private FragmentAdapter mFragmentAdapter;
    private FloatingActionButton mEditGardenFab;
    private FloatingActionButton mAddItemFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_view);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable actionBarColor = new ColorDrawable(getColor(R.color.green));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(actionBarColor);
        actionBar.setTitle(R.string.title_garden_view);

        sDatabaseManager = DatabaseManager.getInstance(this);
        Intent i = getIntent();
        sGardenId = i.getLongExtra(DbHelper._ID, -1);
        mDefaultTab = i.getIntExtra(SELECTED_TAB, DETAILS_TAB_INDEX);

        setupTabs();

        Cursor tagCursor = sDatabaseManager.findMany(
            Tag.TABLE_NAME,
            Tag.ALL_COLUMNS,
            Tag.COLUMN_CATEGORY + " = ?",
            String.valueOf(ResourceType.PLANT.getValue()));

        sTags = Tag.manyFromCursor(tagCursor);

        mEditGardenFab = findViewById(R.id.fabEditGarden);
        mEditGardenFab.setOnClickListener(view -> gardenFabOnClick(0));

        mAddItemFab = findViewById(R.id.fabAddItem);
        mAddItemFab.setOnClickListener(view -> gardenFabOnClick(2));
        mAddItemFab.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupTabs() {
        mPageTabs = findViewById(R.id.gardenTabLayout);
        mViewPager = findViewById(R.id.gardenViewPager);

        FragmentManager mFManager= getSupportFragmentManager();
        mFragmentAdapter = new FragmentAdapter(mFManager, getLifecycle());
        mViewPager.setAdapter(mFragmentAdapter);

        mPageTabs.addTab(mPageTabs.newTab().setText("Detalles"));
        mPageTabs.addTab(mPageTabs.newTab().setText("Plantas"));

        mViewPager.setCurrentItem(mDefaultTab);
        mPageTabs.selectTab(mPageTabs.getTabAt(mDefaultTab));

        mPageTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                mViewPager.setCurrentItem(tabPosition);
                mEditGardenFab.setImageResource(tabPosition == 0 ? R.drawable.ic_edit : R.drawable.ic_add);
                mEditGardenFab.setOnClickListener(view -> gardenFabOnClick(tabPosition));

                mAddItemFab.setVisibility(tabPosition == 1 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
            mPageTabs.selectTab(mPageTabs.getTabAt(position));
            mEditGardenFab.setImageResource(position == 0 ? R.drawable.ic_edit : R.drawable.ic_add);
            mEditGardenFab.setOnClickListener(view -> gardenFabOnClick(position));
            }
        });
    }

    private void gardenFabOnClick(int tabPosition) {
        Intent i = new Intent(GardenDetailsActivity.this, CreateActivity.class);
        ResourceType resource;
        ActionType action;

        switch (tabPosition) {
            case 1:
                resource = ResourceType.PLANT;
                action = ActionType.CREATE;
                break;
            case 2:
                resource = ResourceType.ARTICLE;
                action = ActionType.CREATE;
                break;
            default:
                resource = ResourceType.GARDEN;
                action = ActionType.EDIT;
                i.putExtra(DbHelper._ID, sGardenId);
                i.putExtra(GardenForm.RETURN_TO, TAG);
                break;
        }

        i.putExtra(CreateActivity.ACTION, action);
        i.putExtra(CreateActivity.RESOURCE, resource);
        startActivity(i);
    }

    public static List<Inventory> getGardenInventory() {
        Cursor inventoryCursor = sDatabaseManager.findMany(
            Inventory.TABLE_NAME, Inventory.ALL_COLUMNS,
            Inventory.COLUMN_STORAGE + " = ?",
            String.valueOf(sGardenId)
        );

        GardenDetailsActivity.sGardenInventory = Inventory.manyFromCursor(inventoryCursor);

        sGardenItems = getInventoryItems();
        return GardenDetailsActivity.sGardenInventory;
    }

    public static List<Item> getInventoryItems() {
        Cursor itemCursor = sDatabaseManager.findMany(Item.TABLE_NAME, Item.ALL_COLUMNS);

        GardenDetailsActivity.sGardenItems = Item.manyFromCursor(itemCursor);

        return GardenDetailsActivity.sGardenItems;
    }
}
