package vn.edu.stu.doangiuaky.model;

import java.io.Serializable;

public class LoaiSp implements Serializable {
    private int maloai;
    private String tenloai;

    public LoaiSp() {
    }

    public LoaiSp(int maloai, String tenloai) {
        this.maloai = maloai;
        this.tenloai = tenloai;
    }

    public LoaiSp(String tenloai) {
        this.tenloai = tenloai;
    }

    public int getMaloai() {
        return maloai;
    }

    public void setMaloai(int maloai) {
        this.maloai = maloai;
    }

    public String getTenloai() {
        return tenloai;
    }

    public void setTenloai(String tenloai) {
        this.tenloai = tenloai;
    }

    @Override
    public String toString() {
        return  tenloai;
    }
}
