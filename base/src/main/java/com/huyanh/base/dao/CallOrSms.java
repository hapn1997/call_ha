package com.huyanh.base.dao;

/**
 * Created by nguye on 10/12/2017.
 */

public class CallOrSms {
    private String phoneNumber = "";
    private boolean isCall = true;

    public CallOrSms(String phoneNumber, boolean isCall) {
        this.phoneNumber = phoneNumber;
        this.isCall = isCall;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isCall() {
        return isCall;
    }
}
