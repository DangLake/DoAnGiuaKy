package vn.edu.stu.doangiuaky;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import vn.edu.stu.doangiuaky.adapter.SanphamAdapter;
import vn.edu.stu.doangiuaky.model.LoaiSp;
import vn.edu.stu.doangiuaky.model.Sanpham;


public class Trangchu extends AppCompatActivity {

    FloatingActionButton fabThem;
    int requestCode = 113, resultCode = 115;
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;
    String DATABASE_NAME = "dbbannhanong.sqlite";
    ListView lvSanpham;
    ArrayList<Sanpham> dsSanpham;
    SanphamAdapter adapter;
    Sanpham sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.menu_loai));
            return insets;
        });
        addControls();
        AddEvents();
        processCopy();
        loadDataFromCSDL();

    }

    private void loadDataFromCSDL() {
        database = openOrCreateDatabase("dbbannhanong.sqlite", MODE_PRIVATE, null);

        // Truy vấn dữ liệu từ bảng SANPHAM
        Cursor cursor = database.rawQuery("SELECT * FROM sanpham", null);

        // Dọn dẹp danh sách sản phẩm cũ trước khi tải mới
        dsSanpham.clear();

        // Kiểm tra nếu Cursor có dữ liệu
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int masp = cursor.getInt(cursor.getColumnIndex("masp"));
                @SuppressLint("Range") String tensp = cursor.getString(cursor.getColumnIndex("tensp"));
                @SuppressLint("Range") double gia = cursor.getDouble(cursor.getColumnIndex("gia"));
                @SuppressLint("Range") byte[] hinhanh = cursor.getBlob(cursor.getColumnIndex("hinhanh"));
                @SuppressLint("Range") String mota = cursor.getString(cursor.getColumnIndex("mota"));
                @SuppressLint("Range") int maloai = cursor.getInt(cursor.getColumnIndex("maloai"));

                // Lấy thông tin loại sản phẩm
                LoaiSp loaiSp = getLoaiSpById(maloai);

                // Tạo đối tượng Sanpham và thêm vào danh sách
                Sanpham sanpham = new Sanpham(masp, tensp, hinhanh, gia, mota, loaiSp);
                dsSanpham.add(sanpham);

            } while (cursor.moveToNext());

            // Đóng Cursor sau khi sử dụng
            cursor.close();
        }

        // Cập nhật lại adapter để hiển thị danh sách sản phẩm mới trên UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();  // Gọi notifyDataSetChanged() để thông báo thay đổi
            }
        });
    }

    private LoaiSp getLoaiSpById(int loaiSpId) {
        LoaiSp loaiSp = null;

        // Truy vấn để lấy thông tin LoaiSp từ bảng LOAISANPHAM
        Cursor cursor = database.rawQuery("SELECT * FROM loaisp WHERE maloai = ?", new String[]{String.valueOf(loaiSpId)});

        // Kiểm tra nếu Cursor có dữ liệu
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy tên loại sản phẩm
            @SuppressLint("Range") String tenLoai = cursor.getString(cursor.getColumnIndex("tenloai"));
            loaiSp = new LoaiSp(loaiSpId, tenLoai);
            cursor.close();
        } else {
            Log.e("LOAISP_ERROR", "LoaiSp not found for ID: " + loaiSpId);
        }

        return loaiSp;
    }


    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    public void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
            OutputStream myOutput = new FileOutputStream(outFileName);
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void AddEvents() {
        fabThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Trangchu.this, ThemSanPham.class);
                startActivityForResult(intent, requestCode);
            }
        });
        lvSanpham.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0 && i < adapter.getCount()) {
                    chiTietSanPham(i);
                }
            }
        });
        lvSanpham.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                xulyXoa(i);
                return true;
            }
        });
    }

    private void chiTietSanPham(int i) {
        sp=adapter.getItem(i);
        Intent intent = new Intent(Trangchu.this, ChiTietSanPham.class);
        intent.putExtra("MASP", sp.getMasp());
        startActivityForResult(intent, requestCode);
    }

    private void xulyXoa(int i) {
        Sanpham sp = adapter.getItem(i);
        SQLiteDatabase database=openOrCreateDatabase(
                DATABASE_NAME,MODE_PRIVATE,null
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(Trangchu.this);
        builder.setTitle(getString(R.string.dialog_xoa));
        builder.setMessage(getString(R.string.dialog_xoamg));
        builder.setPositiveButton(R.string.dialog_xoa, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (sp != null) {
                    int count=database.delete(
                            "sanpham","masp=?",new String[]{sp.getMasp()+""});
                    Toast.makeText(Trangchu.this, getString(R.string.xoa_thanhcong), Toast.LENGTH_SHORT).show();
                    loadDataFromCSDL();
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_xoahuy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == this.resultCode) {
            loadDataFromCSDL();
            adapter.notifyDataSetChanged();
        }
    }

    private void addControls() {
        lvSanpham = findViewById(R.id.lvSanpham);
        dsSanpham = new ArrayList<>();
        adapter = new SanphamAdapter(
                Trangchu.this, R.layout.listview_layout, dsSanpham
        );
        lvSanpham.setAdapter(adapter);
        fabThem = findViewById(R.id.fabThem);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.trangchu) {
            finish();
        } else if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(Trangchu.this, About.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromCSDL();
    }
}