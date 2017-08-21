package com.lifed.cardmanager.controller;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lifed.cardmanager.model.CardType;
import com.lifed.cardmanager.R;

import java.util.List;

public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.CardTypeHolder>{

    private Context context;
    ClickListener clickListener;
    private List<CardType> cardTypes;
    private Integer selectedItem;

    public TypeListAdapter(Context context, List<CardType> cardTypes){
        this.context = context;
        this.cardTypes = cardTypes;
    }

    public void setData(List<CardType> cardTypes){
        this.cardTypes = cardTypes;
    }

    @Override
    public CardTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_item, parent, false);
        return new CardTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardTypeHolder holder, final int position) {
        final CardType cardType = cardTypes.get(position);

        holder.tvTypeName.setText(cardType.getTypeName());
        holder.tvDiscount.setText(cardType.getDiscount() + "%");
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
        if(selectedItem != null){
            if(selectedItem == position){
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = position;
                notifyDataSetChanged();
                if(clickListener != null)
                    clickListener.ItemClicked(v, cardType.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardTypes.size();
    }

    class CardTypeHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView tvTypeName, tvDiscount;

        public CardTypeHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv_status_item);
            tvTypeName = (TextView) itemView.findViewById(R.id.tv_type_name);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);
        }
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public void skipSelection(){
        selectedItem = null;
    }
}
