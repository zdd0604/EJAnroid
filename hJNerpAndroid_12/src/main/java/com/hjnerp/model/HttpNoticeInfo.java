package com.hjnerp.model;

import java.io.Serializable;

/**
 * Created by zdd on 2017/12/11.
 * 公告界面实体类 解析
 */

public class HttpNoticeInfo implements Serializable {
    private static final long serialVersionUID = -3593089216207560041L;


    /**
     * extAddr : {"var_extaddr":"http://hjrd.hejia.cn:8086/hjnerp"}
     * extAddrFore : {"var_extaddr":"http://hjrd.hejia.cn:8086/hjFore"}
     */

    private ExtAddrBean extAddr;
    private ExtAddrForeBean extAddrFore;

    public ExtAddrBean getExtAddr() {
        return extAddr;
    }

    public void setExtAddr(ExtAddrBean extAddr) {
        this.extAddr = extAddr;
    }

    public ExtAddrForeBean getExtAddrFore() {
        return extAddrFore;
    }

    public void setExtAddrFore(ExtAddrForeBean extAddrFore) {
        this.extAddrFore = extAddrFore;
    }

    public static class ExtAddrBean {
        /**
         * var_extaddr : http://hjrd.hejia.cn:8086/hjnerp
         */

        private String var_extaddr;

        public String getVar_extaddr() {
            return var_extaddr;
        }

        public void setVar_extaddr(String var_extaddr) {
            this.var_extaddr = var_extaddr;
        }
    }

    public static class ExtAddrForeBean {
        /**
         * var_extaddr : http://hjrd.hejia.cn:8086/hjFore
         */

        private String var_extaddr;

        public String getVar_extaddr() {
            return var_extaddr;
        }

        public void setVar_extaddr(String var_extaddr) {
            this.var_extaddr = var_extaddr;
        }
    }
}
