package com.example;

/**
 * Created by apple on 2017/2/16.
 */
public class Item {

    private String itemName;
    private Double itemValue;

    public Item(String itemName, Double itemValue){
        this.itemName = itemName;
        this.itemValue = itemValue;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    @Override
    public String toString() {
        return this.itemName + ":" + this.itemValue;
    }
}
