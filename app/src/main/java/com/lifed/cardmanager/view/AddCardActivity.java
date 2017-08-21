package com.lifed.cardmanager.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.lifed.cardmanager.R;
import com.lifed.cardmanager.controller.CustomSpinnerAdapter;
import com.lifed.cardmanager.controller.DatabaseHelper;
import com.lifed.cardmanager.model.Card;
import com.lifed.cardmanager.model.CardType;

public class AddCardActivity extends AppCompatActivity {

    private final static String TAG = "AddCardActivity";

    private DatabaseHelper myDb;
    private EditText etNumber, etHolder;
    private Button btnAdd;
    private Spinner spinnerCardTypes;
    private Card card;

    private enum Mode {
        ADD,
        EDIT
    }

    private Mode mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etNumber = (EditText) findViewById(R.id.et_number);
        etHolder = (EditText) findViewById(R.id.et_holder);
        btnAdd = (Button) findViewById(R.id.btn_add);
        spinnerCardTypes = (Spinner) findViewById(R.id.spinner_card_types);

        myDb = new DatabaseHelper(this);
        card = new Card();

        initSpinnerCardTypes();

        if(getIntent().hasExtra("id_card")){
            mode = Mode.EDIT;
            getSupportActionBar().setTitle(R.string.editing_card);
            loadCard(getIntent().getExtras().getInt("id_card"));
        }else{
            mode = Mode.ADD;
            getSupportActionBar().setTitle(R.string.adding_card);
        }

        initBtnAdd();

    }

    private void initSpinnerCardTypes(){
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), myDb.getAllCardTypes());
        spinnerCardTypes.setAdapter(customSpinnerAdapter);

        spinnerCardTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initBtnAdd(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == Mode.ADD)
                    addCard();
                else
                    editCard();
            }
        });
    }

    private void loadCard(Integer id){
        card = myDb.getCard(id);
        etNumber.setText(card.getNumber());
        etHolder.setText(card.getUsername());
        int pos = 0;
        for (CardType cardType:myDb.getAllCardTypes()) {
            if(cardType.getId() == card.getCardType().getId())
                break;
            pos++;
        }
        spinnerCardTypes.setSelection(pos);
        btnAdd.setText(R.string.save);
    }

    private void addCard(){
        if(!inputCheck())
            return;

        boolean result = myDb.addCard(card.getNumber(),
                card.getUsername(),
                card.getCardType().getId());
        if(result) {
            finish();
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error)
                    .setMessage(R.string.error_while_insert_data)
                    .show();
        }
    }

    private void editCard(){
        if(!inputCheck())
            return;

        boolean result = myDb.editCard(card.getId(),
                card.getNumber(),
                card.getUsername(),
                card.getCardType().getId());
        if(result) {
            finish();
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error)
                    .setMessage(R.string.error_while_update_data)
                    .show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showErrorDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error)
                .setMessage(message)
                .show();
    }

    private boolean inputCheck(){
        try {
            card.setNumber(etNumber.getText().toString());
        }catch (Exception e) {
            Log.e(TAG, e.getMessage());
            showErrorDialog(getString(R.string.number_values));
            return false;
        }
        card.setCardType((CardType)spinnerCardTypes.getSelectedItem());
        card.setUsername(etHolder.getText().toString());
        return true;
    }
}
