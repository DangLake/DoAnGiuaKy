package vn.edu.stu.doangiuaky;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;

import vn.edu.stu.doangiuaky.model.LoaiSp;
import vn.edu.stu.doangiuaky.model.Sanpham;

public class ChiTietSanPham extends AppCompatActivity {

    TextView tvTen,tvGia,tvMota,tvLoai;
    ImageView imgView;
    int resultCode=115,requestCode=113;
    Sanpham sp = null;
    SQLiteDatabase database = null;
    FloatingActionButton fabEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet_san_pham);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.chitietsp));
            return insets;
        });
        addControls();
        addEvents();
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("MASP")) {
            int masp = intent.getIntExtra("MASP", -1);
            if (masp != -1) {
                loadSanPhamFromDatabase(masp);
            }
        }
    }

    private void loadSanPhamFromDatabase(int masp) {
        database = openOrCreateDatabase("dbbannhanong.sqlite", MODE_PRIVATE, null);
        // Truy vấn với phép nối INNER JOIN để lấy thông tin sản phẩm và tên loại từ bảng loai_sp
        Cursor cursor = database.rawQuery(
                "SELECT sanpham.*, loaisp.tenloai FROM sanpham INNER JOIN loaisp ON sanpham.maloai = loaisp.maloai WHERE sanpham.masp = ?",
                new String[]{String.valueOf(masp)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Lấy chỉ số các cột trong cursor
                int colIndexMaSp = cursor.getColumnIndex("masp");
                int colIndexTenSp = cursor.getColumnIndex("tensp");
                int colIndexGia = cursor.getColumnIndex("gia");
                int colIndexMota = cursor.getColumnIndex("mota");
                int colIndexHinhanh = cursor.getColumnIndex("hinhanh");
                int colIndexTenLoai = cursor.getColumnIndex("tenloai");

                if (colIndexTenSp != -1 && colIndexGia != -1 && colIndexMota != -1 && colIndexTenLoai != -1&&colIndexMaSp!=-1) {
                    String tensp = cursor.getString(colIndexTenSp);
                    int ma=cursor.getInt(colIndexMaSp);
                    double gia = cursor.getDouble(colIndexGia);
                    String mota = cursor.getString(colIndexMota);
                    byte[] hinhanh = cursor.getBlob(colIndexHinhanh);
                    String tenLoaiSp = cursor.getString(colIndexTenLoai);
                    LoaiSp loaiSp=new LoaiSp(tenLoaiSp);
                    sp= new Sanpham(masp,tensp,hinhanh,gia,mota,loaiSp);

                    DecimalFormat decimalFormat = new DecimalFormat("#,###");
                    String formattedPrice = decimalFormat.format(gia);

                    tvTen.setText("Tên: "+tensp);
                    tvGia.setText("Giá: "+formattedPrice + " VND /KG");
                    tvMota.setText("Mô tả: \n"+mota);

                    if (hinhanh != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhanh, 0, hinhanh.length);
                        imgView.setImageBitmap(bitmap);
                    }

                    if (tenLoaiSp != null) {
                        tvLoai.setText("Loại sản phẩm: "+tenLoaiSp);
                    }
                } else {
                    Log.e("Database", getString(R.string.db_cotfailed));
                }
            } catch (Exception e) {
                Log.e("Database", getString(R.string.db_doc_error) + e.getMessage());
            } finally {
                cursor.close();
            }
        } else {
            Log.e("Database", getString(R.string.db_sp_null) + masp);
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addEvents() {
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulySua();
            }
        });
    }

    private void xulySua() {
        if (sp != null) {
            Intent intent = new Intent(ChiTietSanPham.this, ThemSanPham.class);
            intent.putExtra("MASP", sp.getMasp());
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == RESULT_OK) {
            if (sp != null) {
                loadSanPhamFromDatabase(sp.getMasp());
            }
        }
    }

    private void addControls() {
        tvTen=findViewById(R.id.tvTen);
        tvGia=findViewById(R.id.tvGia);
        tvMota=findViewById(R.id.tvMota);
        tvLoai=findViewById(R.id.tvLoai);
        imgView=findViewById(R.id.imgView);
        fabEdit=findViewById(R.id.fabEdit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_type, menu);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(ChiTietSanPham.this, About.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.trangchu) {
            Intent intent = new Intent(ChiTietSanPham.this, DSPhanLoai.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}