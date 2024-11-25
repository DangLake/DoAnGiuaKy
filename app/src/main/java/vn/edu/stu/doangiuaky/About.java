package vn.edu.stu.doangiuaky;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class About extends AppCompatActivity implements OnMapReadyCallback {

    Button btnSDT;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.menu_about));
            return insets;
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnSDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDial();
            }
        });
    }

    private void doDial() {
        String phoneNumber = btnSDT.getText().toString().trim();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void addControls() {
        btnSDT=findViewById(R.id.btnSDT);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng stu = new LatLng(10.738171162912108, 106.67790851066873);
        mMap.addMarker(new MarkerOptions()
                .position(stu)
                .title("Marker in STU")
                .snippet(getString(R.string.map_ct))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stu,17));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.trangchu){
            Intent intent=new Intent(About.this, DSPhanLoai.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.type){
            Intent intent=new Intent(About.this, Trangchu.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}