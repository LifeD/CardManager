package com.lifed.cardmanager.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lifed.cardmanager.R;
import com.lifed.cardmanager.controller.DatabaseHelper;
import com.lifed.cardmanager.model.CardType;

public class AddCardTypeActivity extends AppCompatActivity {

    private final static String TAG = "AddCardTypeActivity";

    private DatabaseHelper myDb;
    private EditText etName, etDiscount;
    private Button btnAdd;
    private CardType cardType;

    private enum Mode {
        ADD,
        EDIT
    }

    private Mode mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card_type_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etName = (EditText) findViewById(R.id.et_name);
        etDiscount = (EditText) findViewById(R.id.et_discount);
        btnAdd = (Button) findViewById(R.id.btn_add);

        myDb = new DatabaseHelper(this);
        cardType = new CardType();
        if(getIntent().hasExtra("id_type")){
            mode = Mode.EDIT;
            getSupportActionBar().setTitle(R.string.editing_card_type);
            loadCardType(getIntent().getExtras().getInt("id_type"));
        }else{
            mode = Mode.ADD;
            getSupportActionBar().setTitle(R.string.adding_card_type);
        }

        initBtnAdd();
    }

    private void initBtnAdd(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == Mode.ADD)
                    addCardType();
                else
                    editCardType();
            }
        });
    }

    private void loadCardType(Integer id){
        cardType = myDb.getCardType(id);
        etName.setText(cardType.getTypeName());
        etDiscount.setText(cardType.getDiscount().toString());
        btnAdd.setText(R.string.save);
    }

    private void showErrorDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error)
                .setMessage(message)
                .show();
    }

    private boolean inputCheck(){
        try {
            cardType.setTypeName(etName.getText().toString());
        }catch (Exception e) {
            Log.e(TAG, e.getMessage());
            showErrorDialog(getString(R.string.name_values));
            return false;
        }
        try{
            cardType.setDiscount(Integer.parseInt(etDiscount.getText().toString()));
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            showErrorDialog(getString(R.string.discount_values));
            return false;
        }
        return true;
    }

    private void addCardType(){
        if(inputCheck() == false)
            return;

        boolean result = myDb.addCardType(cardType.getTypeName(),
                cardType.getDiscount());
        if(result) {
            finish();
        } else{
            showErrorDialog(getString(R.string.error_while_insert_data));
        }
    }

    private void editCardType(){
        if(!inputCheck())
            return;

        boolean result = myDb.editCardType(cardType.getId(), cardType.getTypeName(),
                cardType.getDiscount());
        if(result) {
            finish();
        } else{
            showErrorDialog(getString(R.string.error_while_update_data));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
