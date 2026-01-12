package com.example.quanly_lophoc;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {
    private int userID;
    private String userName;
    private String type; // Quan trọng: "Admin" hoặc "User"
    private String fullName;
    private String maGV;

    public String getType() {
        return type;
    }

    public String getFullName() {
        return fullName;
    }
}