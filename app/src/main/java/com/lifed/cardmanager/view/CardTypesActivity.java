package com.lifed.cardmanager.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lifed.cardmanager.controller.ClickListener;
import com.lifed.cardmanager.controller.TypeListAdapter;
import com.lifed.cardmanager.controller.DatabaseHelper;
import com.lifed.cardmanager.R;
import com.lifed.cardmanager.model.Card;
import com.lifed.cardmanager.model.CardType;

import java.util.ArrayList;
import java.util.List;

public class CardTypesActivity extends AppCompatActivity implements ClickListener {

    private Context context;

    public enum Mode {
        NORMAL,
        EDIT
    }

    private static Mode mode;
    private MenuItem addMenuItem, editMenuItem, removeMenuItem, dropDatabaseMenuItem;

    private DatabaseHelper myDb;

    private CardType selectedCardType;
    private TypeListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_types_activity);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        myDb = new DatabaseHelper(this);

        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<CardType> data = myDb.getAllCardTypes();
        if(data.size() == 0)
            findViewById(R.id.nothingToShow).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.nothingToShow).setVisibility(View.INVISIBLE);
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_card_types);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TypeListAdapter(this, myDb.getAllCardTypes());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        addMenuItem = menu.findItem(R.id.action_add);
        editMenuItem = menu.findItem(R.id.action_edit);
        removeMenuItem = menu.findItem(R.id.action_delete);
        dropDatabaseMenuItem = menu.findItem(R.id.action_drop_database);
        menu.findItem(R.id.action_card_types).setVisible(false);
        menu.findItem(R.id.action_delete_all_cards).setVisible(false);
        startMode(Mode.NORMAL);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(addMenuItem)) {
            goToAddCardTypeScreen();
        } else if(item.equals(editMenuItem)) {
            goToEditCardTypeScreen();
        } else if(item.equals(removeMenuItem)) {
            deleteCardType();
        } else if(item.equals(dropDatabaseMenuItem)){
            dropDatabase();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dropDatabase(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        myDb.dropDatabase();
                        Toast.makeText(CardTypesActivity.this, getString(R.string.database_was_dropped), Toast.LENGTH_SHORT).show();
                        startMode(Mode.NORMAL);
                        onResume();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.drop_database)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void goToAddCardTypeScreen(){
        Intent intent = new Intent(CardTypesActivity.this, AddCardTypeActivity.class);
        startActivity(intent);
    }

    private void goToEditCardTypeScreen(){
        if(selectedCardType == null)
            return;
        Intent intent = new Intent(CardTypesActivity.this, AddCardTypeActivity.class);
        intent.putExtra("id_type", selectedCardType.getId());
        startActivity(intent);
    }

    private void deleteCardType(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Integer res = myDb.deleteCardType(selectedCardType.getId());
                        if(res > 0) {
                            adapter.setData(myDb.getAllCardTypes());
                            adapter.notifyDataSetChanged();
                            startMode(Mode.NORMAL);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.error)
                                    .setMessage(R.string.item_not_deleted_because_cards_uses_this_type)
                                    .show();
                        }
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_item)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void startMode(Mode modeToStart){
        switch (modeToStart){
            case NORMAL:
                adapter.skipSelection();
                adapter.notifyDataSetChanged();
                selectedCardType = null;
                getSupportActionBar().setTitle(R.string.card_types);
                addMenuItem.setVisible(true);
                editMenuItem.setVisible(false);
                removeMenuItem.setVisible(false);
                dropDatabaseMenuItem.setVisible(true);
                break;

            case EDIT:
                addMenuItem.setVisible(false);
                editMenuItem.setVisible(true);
                removeMenuItem.setVisible(true);
                dropDatabaseMenuItem.setVisible(false);
                break;
        }
        mode = modeToStart;
    }

    @Override
    public void ItemClicked(View v, Integer id) {
        selectedCardType = myDb.getCardType(id);
        getSupportActionBar().setTitle(selectedCardType.getTypeName());
        startMode(Mode.EDIT);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        switch (mode){
            case EDIT:
                startMode(Mode.NORMAL);
                break;

            case NORMAL:
                finish();
                break;
        }
    }
}
