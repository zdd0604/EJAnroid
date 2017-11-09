package com.hjnerp.model;

import java.io.Serializable;

/**
 * Created by zdd in on 2017/11/8.
 * 考勤签到表 本地存储
 */

public class Ctlm1108 implements Serializable{

    private static final long serialVersionUID = -3113143101076832370L;
    /** 类型 */
    public static final String COL_ID_CLERK = "id_clerk";
    /** 打卡时间*/
    public static final String COL_SGIN_TIME = "sgin_time";
    /** 上班时间*/
    public static final String COL_SGIN_TIME_UP = "sgin_time_up";
    /** 下班班时间*/
    public static final String COL_SGIN_TIME_down = "sgin_time_down";

    private String id_clerk;
    private String sgin_time;
    private String sgin_time_up;
    private String sgin_time_down;

    public String getId_clerk() {
        return id_clerk;
    }

    public void setId_clerk(String id_clerk) {
        this.id_clerk = id_clerk;
    }

    public String getSgin_time() {
        return sgin_time;
    }

    public void setSgin_time(String sgin_time) {
        this.sgin_time = sgin_time;
    }

    public String getSgin_time_up() {
        return sgin_time_up;
    }

    public void setSgin_time_up(String sgin_time_up) {
        this.sgin_time_up = sgin_time_up;
    }

    public String getSgin_time_down() {
        return sgin_time_down;
    }

    public void setSgin_time_down(String sgin_time_down) {
        this.sgin_time_down = sgin_time_down;
    }
}
