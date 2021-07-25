package com.example.uasshabri.dashboard.fragment.model;

public class Material {
    private String id;
    private String name;
    private int weight;
    private int qty;

    public Material() {

    }

    public Material(String id, String name, int weight, int qty) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
