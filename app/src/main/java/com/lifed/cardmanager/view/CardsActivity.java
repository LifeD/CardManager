package com.lifed.cardmanager.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.lifed.cardmanager.controller.CardsListAdapter;
import com.lifed.cardmanager.controller.ClickListener;
import com.lifed.cardmanager.controller.DatabaseHelper;
import com.lifed.cardmanager.R;
import com.lifed.cardmanager.model.Card;

import java.util.List;

public class CardsActivity extends AppCompatActivity implements ClickListener {

    private Context context;

    public enum Mode {
        NORMAL,
        EDIT
    }

    private static Mode mode;
    private MenuItem addMenuItem, editMenuItem, removeMenuItem,
            cardTypesMenuItem, dropDatabaseMenuItem, deleteAllCardsMenuItem;

    private DatabaseHelper myDb;

    private Card selectedCard;
    private CardsListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_activity);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDb = new DatabaseHelper(this);
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Card> data = myDb.getAllCards();
        if(data.size() == 0)
            findViewById(R.id.nothingToShow).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.nothingToShow).setVisibility(View.INVISIBLE);
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_cards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardsListAdapter(this, myDb.getAllCards());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        addMenuItem = menu.findItem(R.id.action_add);
        editMenuItem = menu.findItem(R.id.action_edit);
        removeMenuItem = menu.findItem(R.id.action_delete);
        cardTypesMenuItem = menu.findItem(R.id.action_card_types);
        dropDatabaseMenuItem = menu.findItem(R.id.action_drop_database);
        dropDatabaseMenuItem.setVisible(false);
        deleteAllCardsMenuItem = menu.findItem(R.id.action_delete_all_cards);
        startMode(Mode.NORMAL);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(addMenuItem)) {
            goToAddCardScreen();
        } else if(item.equals(editMenuItem)) {
            goToEditCardScreen();
        } else if(item.equals(removeMenuItem)) {
            deleteCard();
        } else if(item.equals(cardTypesMenuItem)){
            goToCardTypesScreen();
        } else if(item.equals(deleteAllCardsMenuItem)){
            deleteAllCards();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllCards(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        myDb.deleteAllCards();
                        Toast.makeText(CardsActivity.this, getString(R.string.cards_was_deleted), Toast.LENGTH_SHORT).show();
                        startMode(Mode.NORMAL);
                        onResume();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_cards_really)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void goToAddCardScreen(){
        if(myDb.getAllCardTypes().size() == 0){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(CardsActivity.this, AddCardTypeActivity.class);
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.first_add_card_type))
                    .setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener)
                    .show();
            return;
        }

        Intent intent = new Intent(CardsActivity.this, AddCardActivity.class);
        startActivity(intent);
    }

    private void goToEditCardScreen(){
        if(selectedCard == null)
            return;

        Intent intent = new Intent(CardsActivity.this, AddCardActivity.class);
        intent.putExtra("id_card", selectedCard.getId());
        startActivity(intent);
    }

    private void goToCardTypesScreen(){
        Intent intent = new Intent(CardsActivity.this, CardTypesActivity.class);
        startActivity(intent);
    }

    private void deleteCard(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Integer res = myDb.deleteCard(selectedCard.getId());
                        if(res > 0) {
                            adapter.setData(myDb.getAllCards());
                            adapter.notifyDataSetChanged();
                            startMode(Mode.NORMAL);
                        } else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.error)
                                    .setMessage(R.string.item_not_deleted)
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
                selectedCard = null;
                getSupportActionBar().setTitle(R.string.cards);
                addMenuItem.setVisible(true);
                editMenuItem.setVisible(false);
                removeMenuItem.setVisible(false);
                cardTypesMenuItem.setVisible(true);
                deleteAllCardsMenuItem.setVisible(true);
                if(myDb.getAllCards().size() == 0)
                    deleteAllCardsMenuItem.setEnabled(false);
                else
                    deleteAllCardsMenuItem.setEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                //getSupportActionBar().setDisplayShowHomeEnabled(false);
                break;

            case EDIT:
                getSupportActionBar().setTitle(selectedCard.getNumber());
                addMenuItem.setVisible(false);
                editMenuItem.setVisible(true);
                removeMenuItem.setVisible(true);
                deleteAllCardsMenuItem.setVisible(false);
                cardTypesMenuItem.setVisible(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                //getSupportActionBar().setDisplayShowHomeEnabled(true);
                break;
        }
        mode = modeToStart;
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

    @Override
    public void ItemClicked(View v, Integer id) {
        selectedCard = myDb.getCard(id);
        startMode(Mode.EDIT);
    }
}
