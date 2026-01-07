package com.example.quanly_lophoc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView rcvLop;
    FloatingActionButton fabAdd;
    LopAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcvLop = findViewById(R.id.rcvLop);
        fabAdd = findViewById(R.id.fabAdd);

        rcvLop.setLayoutManager(new LinearLayoutManager(this));

        // Sự kiện click nút Thêm (+)
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load lại danh sách mỗi khi quay lại màn hình này
        loadListLop();
    }

    private void loadListLop() {
        ApiClient.getService().getAllLop().enqueue(new Callback<List<Lop>>() {
            @Override
            public void onResponse(Call<List<Lop>> call, Response<List<Lop>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Lop> list = response.body();
                    Log.d("API", "Số lượng lớp = " + list.size());

                    adapter = new LopAdapter(MainActivity.this, list, new LopAdapter.IClickListener() {
                        @Override
                        public void onClickUpdate(Lop lop) {
                            // Chuyển sang màn hình Edit và gửi object đi
                            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                            intent.putExtra("object_lop", lop);
                            startActivity(intent);
                        }

                        @Override
                        public void onLongClickDelete(Lop lop) {
                            showDeleteConfirmation(lop);
                        }
                    });
                    rcvLop.setAdapter(adapter);
                }else{
                    Log.d("API_LOP", "Response null hoặc không thành công");
                }
            }

            @Override
            public void onFailure(Call<List<Lop>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi load danh sách: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation(Lop lop) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa lớp " + lop.getTenLop() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteLop(lop.getMaLop());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteLop(String maLop) {
        ApiClient.getService().deleteLop(maLop).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Đã xóa lớp!", Toast.LENGTH_SHORT).show();
                    loadListLop(); // Load lại danh sách sau khi xóa
                } else {
                    Toast.makeText(MainActivity.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}