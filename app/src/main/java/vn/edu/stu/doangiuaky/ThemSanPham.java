package vn.edu.stu.doangiuaky;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.doangiuaky.model.LoaiSp;
import vn.edu.stu.doangiuaky.model.Sanpham;

public class ThemSanPham extends AppCompatActivity {

    Button btnThoat, btnLuu, btnChonanh;
    EditText edtTen, edtGia, edtMota;
    ImageView imgAnh;
    SQLiteDatabase database = null;
    Sanpham sp = null;
    ArrayList<LoaiSp> dsLoai;
    MaterialAutoCompleteTextView inputLoai;
    TextInputLayout inputLayout;
    ArrayAdapter<String> adapter;
    int resultCode=115,requestCode=113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_san_pham);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.menu_themsp));
            return insets;
        });
        addControls();
        addEvents();
        loadLoaiSp();
        getDataFromIntent();
    }

    private void loadLoaiSp() {
        database = openOrCreateDatabase("dbbannhanong.sqlite", MODE_PRIVATE, null);

        // Truy vấn dữ liệu từ bảng loaisp
        Cursor cursor = database.rawQuery("SELECT * FROM loaisp", null);

        if (cursor.moveToFirst()) {
            do {
                int maloai = cursor.getInt(cursor.getColumnIndexOrThrow("maloai"));
                String tenloai = cursor.getString(cursor.getColumnIndexOrThrow("tenloai"));
                dsLoai.add(new LoaiSp(maloai, tenloai));
            } while (cursor.moveToNext());
        }
        cursor.close();

        List<String> tenLoaiList = new ArrayList<>();
        for (LoaiSp loaiSp : dsLoai) {
            tenLoaiList.add(loaiSp.getTenloai());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tenLoaiList);
        inputLoai.setAdapter(adapter);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        int masp = intent.getIntExtra("MASP", -1);
        if (masp != -1 && masp != 0) {
            loadSanPhamFromDatabase(masp);
        } else {
            Log.e("ThemSanPham", "Không nhận được mã sản phẩm hợp lệ.");
        }
    }

    private void loadSanPhamFromDatabase(int masp) {
        Cursor cursor = database.rawQuery(
                "SELECT sanpham.*, loaisp.tenloai FROM sanpham INNER JOIN loaisp ON sanpham.maloai = loaisp.maloai WHERE sanpham.masp = ?",
                new String[]{String.valueOf(masp)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            try {

                int colIndexTenSp = cursor.getColumnIndex("tensp");
                int colIndexGia = cursor.getColumnIndex("gia");
                int colIndexMota = cursor.getColumnIndex("mota");
                int colIndexHinhanh = cursor.getColumnIndex("hinhanh");
                int colIndexTenLoai = cursor.getColumnIndex("tenloai");

                if (colIndexTenSp != -1 && colIndexGia != -1 && colIndexMota != -1 && colIndexTenLoai != -1) {
                    String tensp = cursor.getString(colIndexTenSp);
                    double gia = cursor.getDouble(colIndexGia);
                    String mota = cursor.getString(colIndexMota);
                    byte[] hinhanh = cursor.getBlob(colIndexHinhanh);
                    String tenLoaiSp = cursor.getString(colIndexTenLoai);


                    edtTen.setText(tensp);
                    edtGia.setText(String.valueOf(gia));
                    edtMota.setText(mota);

                    if (hinhanh != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhanh, 0, hinhanh.length);
                        imgAnh.setImageBitmap(bitmap);
                    }

                    if (tenLoaiSp != null) {
                        inputLoai.setText(tenLoaiSp, false);
                    }

                    // Lưu lại thông tin sản phẩm để sau khi nhấn Lưu, có thể cập nhật
                    sp = new Sanpham(masp, tensp, hinhanh, gia, mota, new LoaiSp(tenLoaiSp));
                } else {
                    Log.e("Database", "Không tìm thấy sản phẩm trong cơ sở dữ liệu");
                }
            } catch (Exception e) {
                Log.e("Database", "Lỗi khi đọc dữ liệu: " + e.getMessage());
            } finally {
                cursor.close();
            }
        } else {
            Log.e("Database", "Không tìm thấy sản phẩm với mã " + masp);
            if (cursor != null) {
                cursor.close();
            }
        }
    }





    private void addEvents() {
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyLuu();
            }
        });
        btnChonanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                openImagePicker();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 100);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        1);
            } else {
                openImagePicker();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, getString(R.string.quyentuchoi), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    private void xulyLuu() {
        String ten = edtTen.getText().toString().trim();
        String giaText = edtGia.getText().toString().trim();
        String mota = edtMota.getText().toString().trim();
        if (ten.isEmpty() || giaText.isEmpty() || mota.isEmpty()) {
            Toast.makeText(this, getString(R.string.them_null), Toast.LENGTH_LONG).show();
            return;
        }
        double gia;
        try {
            gia = Double.parseDouble(giaText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.them_giasai), Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put("tensp", ten);
        values.put("gia", gia);
        values.put("mota", mota);
        String tenLoai = inputLoai.getText().toString();
        for (LoaiSp loaiSp : dsLoai) {
            if (loaiSp.getTenloai().equals(tenLoai)) {
                values.put("maloai", loaiSp.getMaloai());
                break;
            }
        }
        if (imgAnh.getDrawable() != null) {
            BitmapDrawable drawable = (BitmapDrawable) imgAnh.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            values.put("hinhanh", imageBytes);  // Lưu ảnh dưới dạng byte[]
        }

        if (sp == null) {
            long newRowId = database.insert("sanpham", null, values);
            if (newRowId != -1) {
                Toast.makeText(this, getString(R.string.btn_luu_tc), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, getString(R.string.btn_luu_tc), Toast.LENGTH_SHORT).show();
            }
        } else {
            int affectedRows = database.update("sanpham", values, "masp = ?", new String[]{String.valueOf(sp.getMasp())});
            if (affectedRows > 0) {
                Toast.makeText(this, getString(R.string.btn_update_tc), Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("MASP", sp.getMasp()); // Truyền mã sản phẩm
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.btn_update_tb), Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                try {
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    imgAnh.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi khi tải ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void addControls() {
        btnThoat = findViewById(R.id.btnThoat);
        btnLuu = findViewById(R.id.btnLuu);
        btnChonanh = findViewById(R.id.btnChonanh);
        edtTen = findViewById(R.id.edtTen);
        edtGia = findViewById(R.id.edtGia);
        edtMota = findViewById(R.id.edtMota);
        imgAnh = findViewById(R.id.imgAnh);
        inputLayout = findViewById(R.id.inputLayout);
        inputLoai = findViewById(R.id.inputLoai);
        dsLoai = new ArrayList<>();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(ThemSanPham.this, About.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.trangchu) {
            Intent intent = new Intent(ThemSanPham.this, DSPhanLoai.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dsLoai.clear();
        loadLoaiSp();
    }
}