package com.hjnerp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by zy on 2016/12/9.
 */

public class AbnormalDetailModel {
    private String id_schtype;
    private String var_on;
    private String var_date;
    private String var_off;
    private Boolean isSelect = false;

    public AbnormalDetailModel(String id_schtype, String var_on, String var_date, String var_off) {
        this.id_schtype = id_schtype;
        this.var_on = var_on;
        this.var_date = var_date;
        this.var_off = var_off;
    }

    public String getId_schtype() {
        return id_schtype;
    }

    public void setId_schtype(String id_schtype) {
        this.id_schtype = id_schtype;
    }

    public String getVar_on() {
        return var_on;
    }

    public void setVar_on(String var_on) {
        this.var_on = var_on;
    }

    public String getVar_date() {
        return var_date;
    }

    public void setVar_date(String var_date) {
        this.var_date = var_date;
    }

    public String getVar_off() {
        return var_off;
    }

    public void setVar_off(String var_off) {
        this.var_off = var_off;
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }
}
