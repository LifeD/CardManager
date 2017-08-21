package com.lifed.cardmanager.controller;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lifed.cardmanager.model.Card;
import com.lifed.cardmanager.R;

import java.util.List;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.CardHolder>{

    private Context context;
    private List<Card> cards;
    private Integer selectedItem;

    private ClickListener clickListener;

    public CardsListAdapter(Context context, List<Card> cards){
        this.context = context;
        this.cards = cards;
    }

    public void setData(List<Card> cards){
        this.cards = cards;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(CardHolder holder, final int position) {
        final Card card = cards.get(position);

        holder.tvTypeName.setText(card.getCardType().getTypeName());
        holder.tvDiscount.setText(card.getCardType().getDiscount() + "%");
        holder.tvNumber.setText(card.getNumber());
        holder.tvUsername.setText(card.getUsername());

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
                    clickListener.ItemClicked(v, card.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{

        TextView tvNumber, tvUsername, tvTypeName, tvDiscount;
        CardView cardView;

        public CardHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvTypeName = (TextView) itemView.findViewById(R.id.tv_type_name);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);

            cardView = (CardView) itemView.findViewById(R.id.cv_status_item);
        }
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public void skipSelection(){
        selectedItem = null;
    }
}
