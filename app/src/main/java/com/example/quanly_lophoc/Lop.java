package com.example.quanly_lophoc;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Lop implements Serializable {
    @SerializedName(value = "maLop", alternate = {"MaLop"})
    String maLop;
    @SerializedName(value = "tenKhoa", alternate = {"TenKhoa"})
    String tenKhoa;



    @SerializedName(value = "maNganh", alternate = {"MaNganh"})
    String maNganh;
    String tenLop;

    String nienKhoa;
    String tenNganh;

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getNienKhoa() {
        return nienKhoa;
    }

    public void setNienKhoa(String nienKhoa) {
        this.nienKhoa = nienKhoa;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getTenNganh() {
        return tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }
    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }
}
