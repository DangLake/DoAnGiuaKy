package vn.edu.stu.doangiuaky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    Button btnDangnhap;
    EditText edtSDT,edtMatkhau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtSDT.getText().toString().equals("0706827751")){
                    if(edtMatkhau.getText().toString().equals("123456")){
                        Intent intent=new Intent(Login.this, DSPhanLoai.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(Login.this,getString(R.string.login_saipass), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Login.this, getString(R.string.login_saisdt), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addControls() {
        btnDangnhap=findViewById(R.id.btnDangnhap);
        edtSDT=findViewById(R.id.edtSDT);
        edtMatkhau=findViewById(R.id.edtMatkhau);
    }
}