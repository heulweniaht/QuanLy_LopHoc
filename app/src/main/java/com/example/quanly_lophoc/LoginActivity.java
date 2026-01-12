package com.example.quanly_lophoc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtUser, edtPass;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = edtUser.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(username, password);

        ApiClient.getService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginRes = response.body();

                    if (loginRes.isSuccess()) {
                        // Đăng nhập thành công, kiểm tra quyền Admin
                        User user = loginRes.getData();
                        if ("Admin".equalsIgnoreCase(user.getType())) {

                            // Lưu cờ IS_ADMIN vào SharedPreferences để dùng ở các màn hình khác
                            saveLoginState(true, user.getFullName());

                            Toast.makeText(LoginActivity.this, "Xin chào Admin: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                            goToMain();
                        } else {
                            // Nếu là User thường (tùy bạn xử lý, ở đây mình vẫn cho vào nhưng sẽ ẩn nút thêm)
                            saveLoginState(false, user.getFullName());
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công (User)", Toast.LENGTH_SHORT).show();
                            goToMain();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + loginRes.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi server hoặc sai tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginState(boolean isAdmin, String name) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("IS_ADMIN", isAdmin);
        editor.putString("FULL_NAME", name);
        editor.apply();
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Đóng màn hình login để không quay lại được bằng nút Back
    }
}