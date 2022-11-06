package com.example.budgetingappv1;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Data_Transaction {

    private int id;
    private int groceries_id;
    private String title;
    private String recipient;
    private float amount;
    private String date;
    private String category;
    private boolean recurring;
    private boolean expanded;
    private boolean expandable;
    private List<Data_Grocery> groceries_list = new ArrayList<Data_Grocery>();

    // constructors

    public Data_Transaction(int id, int groceries_id, String title, String recipient, float amount, String date, String category, boolean recurring) {
        this.id = id;
        this.groceries_id = groceries_id;
        this.title = title;
        this.recipient = recipient;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.recurring = recurring;
        this.expandable = category.equals("Groceries");
        this.expanded = false;
    }

    public Data_Transaction() {
    }

    // toString

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", recurring=" + recurring +
                '}';
    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getFormattedDate(){
        return date.substring(8) + "/" + date.substring(5, 7) + "/" + date.substring(0, 4);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public byte[] getCategoryIcon(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Data_Category category = databaseHelper.selectCategory(this.category);
        return category.getIconBytes();
    }

    public int getCategoryColour(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Data_Category category = databaseHelper.selectCategory(this.category);
        return category.getColor();
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public List<Data_Grocery> getGroceries_list(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<Data_Grocery> groceryList = databaseHelper.selectGroceries(groceries_id);
        return groceryList;
    }

    public void setGroceries_list(List<Data_Grocery> groceries_list) {
        this.groceries_list = groceries_list;
    }
}
