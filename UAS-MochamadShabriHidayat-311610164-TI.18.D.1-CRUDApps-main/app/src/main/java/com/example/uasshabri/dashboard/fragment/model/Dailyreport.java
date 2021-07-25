package com.example.uasshabri.dashboard.fragment.model;

public class Dailyreport {
    private String id;
    private String date;
    private String create_by;
    private int plan_qty;
    private int min_qty;
    private int today_qty;
    private String id_material;
    private String id_machine;

    public Dailyreport() {

    }

    public Dailyreport(String id, String date, String create_by, int plan_qty, int min_qty, int today_qty, String id_material, String id_machine) {
        this.id = id;
        this.date = date;
        this.create_by = create_by;
        this.plan_qty = plan_qty;
        this.min_qty = min_qty;
        this.today_qty = today_qty;
        this.id_material = id_material;
        this.id_machine = id_machine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public int getPlan_qty() {
        return plan_qty;
    }

    public void setPlan_qty(int plan_qty) {
        this.plan_qty = plan_qty;
    }

    public int getMin_qty() {
        return min_qty;
    }

    public void setMin_qty(int min_qty) {
        this.min_qty = min_qty;
    }

    public int getToday_qty() {
        return today_qty;
    }

    public void setToday_qty(int today_qty) {
        this.today_qty = today_qty;
    }

    public String getId_material() {
        return id_material;
    }

    public void setId_material(String id_material) {
        this.id_material = id_material;
    }

    public String getId_machine() {
        return id_machine;
    }

    public void setId_machine(String id_machine) {
        this.id_machine = id_machine;
    }
}
