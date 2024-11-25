package vn.edu.stu.doangiuaky;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import vn.edu.stu.doangiuaky.model.LoaiSp;

public class DSPhanLoai extends AppCompatActivity {

    FloatingActionButton fabThem;
    ListView lvLoai;
    ArrayList<LoaiSp> dsLoai;
    ArrayAdapter<LoaiSp> adapter;
    int requestCode = 113, resultCode = 115;
    SQLiteDatabase database = null;
    LoaiSp loaiSp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dsphan_loai);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.menu_dsloai));
            return insets;
        });
        addControls();
        addEvents();
        loadLoaiFromDB();
    }

    private void loadLoaiFromDB() {
        database = openOrCreateDatabase("dbbannhanong.sqlite", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM loaisp", null);
        dsLoai.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int maloai = cursor.getInt(cursor.getColumnIndex("maloai"));
                @SuppressLint("Range") String tenloai = cursor.getString(cursor.getColumnIndex("tenloai"));
                loaiSp = new LoaiSp(maloai, tenloai);
                dsLoai.add(loaiSp);
            } while (cursor.moveToNext());
            cursor.close();

        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addEvents() {
        lvLoai.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                xoaLoai(dsLoai.get(i));
                return true;
            }
        });
        fabThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoaiDialog(null);
            }
        });
        lvLoai.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showLoaiDialog(dsLoai.get(i));
            }
        });
    }

    private void showLoaiDialog(LoaiSp loai) {
        View dialogView = getLayoutInflater().inflate(R.layout.activity_them_loai, null);
        EditText edtTenLoai = dialogView.findViewById(R.id.edtTenloai);
        if (loai != null) edtTenLoai.setText(loai.getTenloai());

        new AlertDialog.Builder(this)
                .setTitle(loai == null ? R.string.them_loai_sp : R.string.sua_loai_sp)
                .setView(dialogView)
                .setPositiveButton(R.string.dongy, (dialog, which) -> {
                    String tenLoai = edtTenLoai.getText().toString().trim();
                    if (tenLoai.isEmpty()) {
                        Toast.makeText(this, R.string.ten_loai_khong_duoc_trong, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (loai == null) {
                        themLoai(tenLoai);
                    } else {
                        suaLoai(loai.getMaloai(), tenLoai);
                    }
                })
                .setNegativeButton(R.string.huy, null)
                .setCancelable(false)
                .show();
    }

    private void themLoai(String tenLoai) {
        ContentValues values = new ContentValues();
        values.put("tenloai", tenLoai);
        long newId = database.insert("loaisp", null, values);
        if (newId != -1) {
            dsLoai.add(new LoaiSp((int) newId, tenLoai));
            adapter.notifyDataSetChanged();
            Toast.makeText(this, R.string.them_thanh_cong, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.them_that_bai, Toast.LENGTH_SHORT).show();
        }
    }

    private void suaLoai(int maloai, String tenLoai) {
        ContentValues values = new ContentValues();
        values.put("tenloai", tenLoai);
        int affectedRows = database.update("loaisp", values, "maloai = ?", new String[]{String.valueOf(maloai)});
        if (affectedRows > 0) {
            for (LoaiSp loai : dsLoai) {
                if (loai.getMaloai() == maloai) {
                    loai.setTenloai(tenLoai);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(this, R.string.sua_thanh_cong, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.sua_that_bai, Toast.LENGTH_SHORT).show();
        }
    }

    private void xoaLoai(LoaiSp loai) {
        // Kiểm tra xem loại sản phẩm này có sản phẩm nào không
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM sanpham WHERE maloai = ?",
                new String[]{String.valueOf(loai.getMaloai())});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.xoa_loai_sp)
                    .setMessage(getString(R.string.loai_sp_da_co_san_pham) + " " +
                            getString(R.string.xacnhan_xoa_ca_san_pham))
                    .setPositiveButton(R.string.dialog_xoa, (dialog, which) -> {
                        database.delete("sanpham", "maloai = ?", new String[]{String.valueOf(loai.getMaloai())});
                        int affectedRows = database.delete("loaisp", "maloai = ?", new String[]{String.valueOf(loai.getMaloai())});
                        if (affectedRows > 0) {
                            dsLoai.remove(loai);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, R.string.xoa_thanhcong, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.xoa_thatbai, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.huy, null)
                    .setCancelable(false)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.xoa_loai_sp)
                    .setMessage(getString(R.string.xacnhan_xoa))
                    .setPositiveButton(R.string.dialog_xoa, (dialog, which) -> {
                        int affectedRows = database.delete("loaisp", "maloai = ?", new String[]{String.valueOf(loai.getMaloai())});
                        if (affectedRows > 0) {
                            dsLoai.remove(loai);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, R.string.xoa_thanhcong, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.xoa_thatbai, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.huy, null)
                    .setCancelable(false)
                    .show();
        }
    }



    private void addControls() {
        lvLoai = findViewById(R.id.lvLoai);
        dsLoai = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                DSPhanLoai.this, android.R.layout.simple_list_item_1, dsLoai
        );
        lvLoai.setAdapter(adapter);
        fabThem = findViewById(R.id.fabThem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == RESULT_OK && data != null) {
            LoaiSp loaiSp = (LoaiSp) data.getSerializableExtra("LOAI");
            boolean isUpdated = false;
            for (int i = 0; i < dsLoai.size(); i++) {
                if (dsLoai.get(i).getMaloai() == loaiSp.getMaloai()) {
                    dsLoai.set(i, loaiSp);
                    isUpdated = true;
                    break;
                }
            }

            if (!isUpdated) {
                dsLoai.add(loaiSp);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.loaisp) {
            Intent intent = new Intent(DSPhanLoai.this, Trangchu.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(DSPhanLoai.this, About.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLoaiFromDB();
    }
}