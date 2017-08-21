package com.lifed.cardmanager.model;

import com.lifed.cardmanager.R;

public class CardType {

    private Integer id;
    private String typeName;
    private Integer discount;

    public CardType() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) throws Exception {
        if(typeName.length() < 2)
            throw new Exception("Type name must contains more than 2 characters");
        this.typeName = typeName;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) throws Exception{
        if(discount >= 0 && discount <= 100)
            this.discount = discount;
        else
            throw new Exception("Discount must be from 0 to 100 percent");
    }
}
