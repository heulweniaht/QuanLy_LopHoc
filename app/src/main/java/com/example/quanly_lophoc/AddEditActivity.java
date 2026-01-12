package com.example.quanly_lophoc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 ************************************************************
 *  Như 1 vở kịch buồn , anh diễn trọn cả nhiều vai           *
 *                                                          *
 *                                  Thái đẹp trai vcl       *
 ************************************************************
 */

public class AddEditActivity extends AppCompatActivity {
    EditText edtMaLop, edtTenLop, edtNienKhoa;
    Spinner spnNganh;
    Button btnLuu, btnHuy, btnXoa;
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
        btnXoa = findViewById(R.id.btnXoa);
        tvTitle = findViewById(R.id.tvTitle);
        //Lấy dữ liệu intent
        if(getIntent().getExtras() != null){
            lopHienTai = (Lop) getIntent().getSerializableExtra("object_lop");
        }
        //Load danh sách Ngành cho spinner
        loadDataSpinner();
        
        btnHuy.setOnClickListener(v -> finish());
        
        btnLuu.setOnClickListener(v -> handleSave());
        btnXoa.setOnClickListener(v -> showDeleteConfirmation());
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
        btnXoa.setVisibility(View.VISIBLE);
        //tìm chọn đúng ngành trong spinner
        for(int i = 0; i<listNganh.size();i++) {
            if (listNganh.get(i).getMaNganh().equals(lopHienTai.getMaNganh())) {
                spnNganh.setSelection(i);
                break;
            }
        }
    }
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa lớp " + lopHienTai.getTenLop() + " không?")
                .setPositiveButton("Có, Xóa", (dialog, which) -> {
                    // Người dùng chọn OK -> Gọi API
                    requestDeleteApi();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    // 5. Hàm gọi API xóa
    private void requestDeleteApi() {
        if (lopHienTai == null) return;

        ApiClient.getService().deleteLop(lopHienTai.getMaLop()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddEditActivity.this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình này để quay về danh sách
                } else {
                    Toast.makeText(AddEditActivity.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddEditActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleSave() {
        if(edtMaLop.getText().toString().isEmpty()){
            Toast.makeText(this, "Vui lòng nhập mã lớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Tạo đối tượng Lop mới để gửi đi
        Lop lopToSend = new Lop();

        // 2. Chỉ set những trường thuộc bảng LOP
        lopToSend.setMaLop(edtMaLop.getText().toString());
        lopToSend.setTenLop(edtTenLop.getText().toString());
        lopToSend.setNienKhoa(edtNienKhoa.getText().toString());

        // 3. Lấy MaNganh từ Spinner
        Nganh selectedNganh = (Nganh) spnNganh.getSelectedItem();
        if (selectedNganh != null) {
            // 1. Set Mã Ngành
            lopToSend.setMaNganh(selectedNganh.getMaNganh());

            // 2. Set Tên Ngành (BẮT BUỘC - theo log lỗi)
            // Lấy từ đối tượng Ngành đã chọn trong Spinner
            lopToSend.setTenNganh(selectedNganh.getTenNganh());

            // 3. Set Tên Khoa (BẮT BUỘC - theo log lỗi)
            if (selectedNganh.getTenKhoa() != null && !selectedNganh.getTenKhoa().isEmpty()) {
                lopToSend.setTenKhoa(selectedNganh.getTenKhoa());
            } else {
                // Nếu API lấy danh sách Ngành không trả về Tên Khoa,
                // ta buộc phải gửi một chuỗi mặc định để vượt qua validation "Required" của Server.
                lopToSend.setTenKhoa("Công nghệ thông tin");
            }
        }

        if (lopHienTai == null) {
            // == THÊM MỚI (POST) ==
            // Dùng lopToSend thay vì lop cũ
            Log.d("DEBUG_DATA", "MaNganh: " + lopToSend.getMaNganh());
            Log.d("DEBUG_DATA", "TenNganh: " + lopToSend.getTenNganh());
            Log.d("DEBUG_DATA", "TenKhoa: " + lopToSend.getTenKhoa());
            ApiClient.getService().createLop(lopToSend).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // In lỗi chi tiết ra để debug nếu vẫn lỗi
                        try {
                            String errorBody = response.errorBody().string(); // Đọc nội dung lỗi
                            Log.e("LOI_API", "Code: " + response.code() + " - Body: " + errorBody);

                            // Hiện lỗi lên Toast để dễ nhìn
                            Toast.makeText(AddEditActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // == CẬP NHẬT (PUT) ==
            // Quan trọng: Tham số đầu tiên phải là Mã Lớp gốc (để điền vào link /api/Lop/{id})
            // Tham số thứ 2 là body (lopToSend) chứa dữ liệu cần sửa
            Log.d("DEBUG_DATA", "MaNganh: " + lopToSend.getMaNganh());
            Log.d("DEBUG_DATA", "TenNganh: " + lopToSend.getTenNganh());
            Log.d("DEBUG_DATA", "TenKhoa: " + lopToSend.getTenKhoa());
            // Lưu ý: lopHienTai.getMaLop() là ID lấy từ Intent ban đầu (ID đúng)
            ApiClient.getService().updateLop(lopHienTai.getMaLop(), lopToSend).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String errorBody = response.errorBody().string(); // Đọc nội dung lỗi
                            Log.e("LOI_API", "Code: " + response.code() + " - Body: " + errorBody);

                            // Hiện lỗi lên Toast để dễ nhìn
                            Toast.makeText(AddEditActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}