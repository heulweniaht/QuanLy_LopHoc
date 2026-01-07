package com.example.quanly_lophoc;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditActivity extends AppCompatActivity {
    EditText edtMaLop, edtTenLop, edtNienKhoa;
    Spinner spnNganh;
    Button btnLuu, btnHuy;
    TextView tvTitle;
    List<Nganh> listNganh = new ArrayList<>();
    Lop lopHienTai = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        edtMaLop = findViewById(R.id.edtMaLop);
        edtTenLop = findViewById(R.id.edtTenLop);
        edtNienKhoa = findViewById(R.id.edtNienKhoa);
        spnNganh = findViewById(R.id.spnNganh);
        btnLuu = findViewById(R.id.btnLuu);
        btnHuy = findViewById(R.id.btnHuy);
        tvTitle = findViewById(R.id.tvTitle);
        //Lấy dữ liệu intent
        if(getIntent().getExtras() != null){
            lopHienTai = (Lop) getIntent().getSerializableExtra("object_lop");
        }
        //Load danh sách Ngành cho spinner
        loadDataSpinner();
        
        btnHuy.setOnClickListener(v -> finish());
        
        btnLuu.setOnClickListener(v -> handleSave());
    }
    
    private void loadDataSpinner() {
        ApiClient.getService().getAllNganh().enqueue(new Callback<List<Nganh>>() {
            @Override
            public void onResponse(Call<List<Nganh>> call, Response<List<Nganh>> response) {
                if(response.isSuccessful() && response.body() != null){
                    listNganh = response.body();
                    //setup adater cho spinner
                    ArrayAdapter<Nganh> adapter = new ArrayAdapter<>(AddEditActivity.this, android.R.layout.simple_spinner_dropdown_item,listNganh);
                    spnNganh.setAdapter(adapter);
                    if(lopHienTai != null){
                        setupEditMode();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Nganh>> call, Throwable t) {
                Toast.makeText(AddEditActivity.this, "Lỗi load ngành", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEditMode() {
        tvTitle.setText("CẬP NHẬT LỚP");
        edtMaLop.setText(lopHienTai.getMaLop());
        edtMaLop.setEnabled(false);

        edtTenLop.setText(lopHienTai.getTenLop());
        edtNienKhoa.setText(lopHienTai.getNienKhoa());

        //tìm chọn đúng ngành trong spinner
        for(int i = 0; i<listNganh.size();i++) {
            if (listNganh.get(i).getMaNganh().equals(lopHienTai.getMaNganh())) {
                spnNganh.setSelection(i);
                break;
            }
        }
    }

    private void handleSave() {
        if(edtMaLop.getText().toString().isEmpty()){
            Toast.makeText(this, "Vui lòng nhập mã lớp", Toast.LENGTH_SHORT).show();
            return;
        }
        Lop lop = new Lop();
        lop.setMaLop(edtMaLop.getText().toString());
        lop.setTenLop(edtTenLop.getText().toString());
        lop.setNienKhoa(edtNienKhoa.getText().toString());

        Nganh selectedNganh = (Nganh) spnNganh.getSelectedItem();
        if (selectedNganh != null) {
            // 1. Gửi Mã Ngành
            lop.setMaNganh(selectedNganh.getMaNganh());

            // 2. Gửi Tên Ngành (Server bắt buộc)
            lop.setTenNganh(selectedNganh.getTenNganh());

            // 3. Gửi Tên Khoa (Server bắt buộc)
            // Nếu API Nganh có trả về tenKhoa thì lấy từ đó
            if (selectedNganh.getTenKhoa() != null) {
                lop.setTenKhoa(selectedNganh.getTenKhoa());
            } else {
                // Nếu API Nganh không có tenKhoa, ta phải "chống cháy" bằng cách gửi chuỗi mặc định
                // để Server không báo lỗi. Bạn có thể sửa chuỗi này sau.
                lop.setTenKhoa("Công nghệ thông tin");
            }
        }

        if (lopHienTai == null) {
            // == THÊM MỚI (CREATE) ==
            ApiClient.getService().createLop(lop).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // QUAN TRỌNG: In lỗi ra Logcat để xem
                        int statusCode = response.code();
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) { e.printStackTrace(); }

                        // Hiển thị lỗi lên màn hình để bạn dễ thấy
                        Toast.makeText(AddEditActivity.this, "Lỗi Server: " + statusCode, Toast.LENGTH_LONG).show();

                        // Ghi log chi tiết
                        android.util.Log.e("LOI_THEM_MOI", "Code: " + statusCode + " - Body: " + errorBody);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddEditActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    android.util.Log.e("LOI_THEM_MOI", "Lỗi: " + t.getMessage());
                }
            });
        }else{
            //Cập nhật
            ApiClient.getService().updateLop(lop).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(AddEditActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(AddEditActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}