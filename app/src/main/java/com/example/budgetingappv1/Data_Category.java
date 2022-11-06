package com.example.budgetingappv1;

public class Data_Category {
    private String name;
    private int color;
    private byte[] iconBytes;
    private boolean essential;
    private float budget;

    public Data_Category(String name, float budget, int color, byte[] iconBytes, boolean essential) {
        this.name = name;
        this.budget = budget;
        this.color = color;
        this.iconBytes = iconBytes;
        this.essential = essential;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isEssential() {
        return essential;
    }

    public void setEssential(boolean essential) {
        this.essential = essential;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public byte[] getIconBytes() {
        return iconBytes;
    }

    public void setIconBytes(byte[] iconBytes) {
        this.iconBytes = iconBytes;
    }
}
