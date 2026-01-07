package com.example.quanly_lophoc;

import com.google.gson.annotations.SerializedName;

public class Nganh {
    @SerializedName(value = "maNganh", alternate = {"MaNganh"})
    private String maNganh;

    @SerializedName(value = "tenNganh", alternate = {"TenNganh"})
    private String tenNganh;

    // Thêm trường này để lấy tên khoa từ Spinner
    @SerializedName(value = "tenKhoa", alternate = {"TenKhoa"})
    private String tenKhoa;

    // Getter, Setter
    public String getMaNganh() { return maNganh; }
    public void setMaNganh(String maNganh) { this.maNganh = maNganh; }

    public String getTenNganh() { return tenNganh; }
    public void setTenNganh(String tenNganh) { this.tenNganh = tenNganh; }

    public String getTenKhoa() { return tenKhoa; }
    public void setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return tenNganh;
    }
}
