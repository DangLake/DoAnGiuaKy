package vn.edu.stu.doangiuaky.model;

import java.io.Serializable;

public class Sanpham implements Serializable {
    private int masp;
    private String tensp;
    private byte[] hinhanh;
    private double gia;
    private String mota;
    private LoaiSp loaiSp;  // Thay đổi từ int maloai sang LoaiSp


    public Sanpham(int masp, String tensp, byte[] hinhanh, double gia, String mota, LoaiSp loaiSp) {
        this.masp = masp;
        this.tensp = tensp;
        this.hinhanh = hinhanh;
        this.gia = gia;
        this.mota = mota;
        this.loaiSp = loaiSp;
    }

    public Sanpham() {
    }

    public Sanpham(String tensp, byte[] hinhanh, double gia, String mota, LoaiSp loaiSp) {
        this.tensp = tensp;
        this.hinhanh = hinhanh;
        this.gia = gia;
        this.mota = mota;
        this.loaiSp = loaiSp;
    }

    public LoaiSp getLoaiSp() {
        return loaiSp;
    }

    public void setLoaiSp(LoaiSp loaiSp) {
        this.loaiSp = loaiSp;
    }

    public int getMasp() {
        return masp;
    }

    public void setMasp(int masp) {
        this.masp = masp;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public byte[] getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(byte[] hinhanh) {
        this.hinhanh = hinhanh;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }
}
