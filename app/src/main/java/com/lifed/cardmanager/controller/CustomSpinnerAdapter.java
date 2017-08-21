package com.lifed.cardmanager.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lifed.cardmanager.R;
import com.lifed.cardmanager.model.CardType;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<CardType> cardTypes;

    public CustomSpinnerAdapter(Context context, List<CardType> cardTypes) {
        this.context = context;
        this.cardTypes = cardTypes;
    }

    @Override
    public int getCount() {
        return cardTypes.size();
    }

    @Override
    public CardType getItem(int position) {
        return cardTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cardTypes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardType cardType = cardTypes.get(position);

        convertView = (LayoutInflater.from(context)).inflate(R.layout.type_item, parent, false);
        TextView tvTypeName = (TextView) convertView.findViewById(R.id.tv_type_name);
        TextView tvDiscount = (TextView) convertView.findViewById(R.id.tv_discount);
        tvTypeName.setText(cardType.getTypeName());
        tvDiscount.setText(cardType.getDiscount() + "%");

        return convertView;
    }
}
