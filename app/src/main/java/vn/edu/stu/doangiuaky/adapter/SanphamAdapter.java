package vn.edu.stu.doangiuaky.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

import vn.edu.stu.doangiuaky.R;
import vn.edu.stu.doangiuaky.model.Sanpham;

public class SanphamAdapter extends ArrayAdapter<Sanpham> {
    private Context context;
    private int resource;
    private List<Sanpham> sanphamList;
    public SanphamAdapter(@NonNull Context context, int resource, @NonNull List<Sanpham> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.sanphamList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(resource, parent, false);
        }

        ImageView imgSanpham = rowView.findViewById(R.id.imgSanpham);
        TextView tvMa = rowView.findViewById(R.id.tvMa);
        TextView tvTen = rowView.findViewById(R.id.tvTen);
        TextView tvGia = rowView.findViewById(R.id.tvGia);
        TextView tvTenloai = rowView.findViewById(R.id.tvTenloai);

        Sanpham sanpham = sanphamList.get(position);

        if (tvMa != null) {
            tvMa.setText("Mã: " + sanpham.getMasp());
        }

        if (tvTen != null) {
            tvTen.setText("Tên: " + sanpham.getTensp());
        }

        if (tvGia != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedPrice = decimalFormat.format(sanpham.getGia());
            tvGia.setText("Giá: " + formattedPrice + " VND /KG");
        }

        if (tvTenloai != null) {
            tvTenloai.setText(sanpham.getLoaiSp() != null ? "Loại: "+ sanpham.getLoaiSp().getTenloai() : "Loại không xác định");
        }

        byte[] imageBlob = sanpham.getHinhanh();
        if (imageBlob != null && imageBlob.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
            if (imgSanpham != null) {
                imgSanpham.setImageBitmap(bitmap);
            }
        }

        return rowView;
    }
}
