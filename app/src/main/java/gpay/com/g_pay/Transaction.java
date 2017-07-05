package gpay.com.g_pay;

import java.io.Serializable;

/**
 * Created by adetunji on 5/17/17.
 */

public class Transaction implements Serializable
{
    private String transRef,customerName,transDate,descoRef,account,address,custCategory,date,discoRef,eReceipt,gPayRef,meter,outstanding,phone,previousOutstanding,status,token,unit;

    private double amount;
    public String getTransRef() {
        return transRef;
    }


    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getDescoRef() {
        return descoRef;
    }

    public void setDescoRef(String descoRef) {
        this.descoRef = descoRef;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCustCategory() {
        return custCategory;
    }

    public void setCustCategory(String custCategory) {
        this.custCategory = custCategory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiscoRef() {
        return discoRef;
    }

    public void setDiscoRef(String discoRef) {
        this.discoRef = discoRef;
    }

    public String geteReceipt() {
        return eReceipt;
    }

    public void seteReceipt(String eReceipt) {
        this.eReceipt = eReceipt;
    }

    public String getgPayRef() {
        return gPayRef;
    }

    public void setgPayRef(String gPayRef) {
        this.gPayRef = gPayRef;
    }

    public String getMeter() {
        return meter;
    }

    public void setMeter(String meter) {
        this.meter = meter;
    }

    public String getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(String outstanding) {
        this.outstanding = outstanding;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPreviousOutstanding() {
        return previousOutstanding;
    }

    public void setPreviousOutstanding(String previousOutstanding) {
        this.previousOutstanding = previousOutstanding;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
