package com.example.health_app_sof1021.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.MealDao;
import com.example.health_app_sof1021.model.Meal;
import com.example.health_app_sof1021.utils.DateUtils;
import com.example.health_app_sof1021.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealActivity extends AppCompatActivity {
    private MaterialToolbar toolbarMeal;
    private EditText edtNgayAn, edtSoLuong;
    private Spinner spnLoaiBua, spnMonAn;
    private TextView tvCaloMon, tvTongCalo;
    private ListView lvMealPlan;
    private MealDao mealDao;
    private ArrayAdapter<Meal> mealPlanAdapter;
    private List<Meal> mealPlans;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mealDao = new MealDao(this);
        SessionManager sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();
        initUi();
        initSpinner();
        initEvent();
        hienThiDanhSach();
    }

    private void initUi() {
        toolbarMeal = findViewById(R.id.toolbarMeal);
        edtNgayAn = findViewById(R.id.edtNgayAn);
        edtSoLuong = findViewById(R.id.edtSoLuong);
        spnLoaiBua = findViewById(R.id.spnLoaiBua);
        spnMonAn = findViewById(R.id.spnMonAn);
        tvCaloMon = findViewById(R.id.tvCaloMon);
        tvTongCalo = findViewById(R.id.tvTongCalo);
        lvMealPlan = findViewById(R.id.lvMealPlan);
        
        // Sử dụng định dạng dd-MM-yyyy đồng bộ với OpenDatePicker
        edtNgayAn.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        
        mealPlans = new ArrayList<>();
        mealPlanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mealPlans);
        lvMealPlan.setAdapter(mealPlanAdapter);
    }

    private void initSpinner() {
        String[] loaiBua = {"Bữa sáng", "Bữa trưa", "Bữa tối", "Bữa phụ"};
        ArrayAdapter<String> loaiBuaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loaiBua);
        loaiBuaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLoaiBua.setAdapter(loaiBuaAdapter);

        List<String> monAn = new ArrayList<>(mealDao.getDanhSachCaloMonAn().keySet());
        ArrayAdapter<String> monAnAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monAn);
        monAnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMonAn.setAdapter(monAnAdapter);
        capNhatCaloMon();
    }

    private void initEvent() {
        toolbarMeal.setNavigationOnClickListener(v -> finish());
        
        // Áp dụng OpenDatePicker cho edtNgayAn
        edtNgayAn.setOnClickListener(v -> DateUtils.openDatePicker(this, edtNgayAn));

        spnMonAn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                capNhatCaloMon();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.btnThemMon).setOnClickListener(v -> themMonAn());
        findViewById(R.id.btnXemTheoNgay).setOnClickListener(v -> hienThiDanhSach());
        lvMealPlan.setOnItemClickListener((parent, view, position, id) -> hienThiTuyChonMonAn(mealPlans.get(position)));
    }

    private void capNhatCaloMon() {
        if (spnMonAn.getSelectedItem() == null) {
            tvCaloMon.setText("Calo: 0");
            return;
        }
        String tenMon = spnMonAn.getSelectedItem().toString();
        tvCaloMon.setText("Calo: " + mealDao.getCaloTheoMon(tenMon) + " / khẩu phần");
    }

    private void themMonAn() {
        String ngayAn = edtNgayAn.getText().toString().trim();
        String soLuongText = edtSoLuong.getText().toString().trim();

        if (ngayAn.isEmpty() || soLuongText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int soLuong = Integer.parseInt(soLuongText);
        String tenMon = spnMonAn.getSelectedItem().toString();
        String loaiBua = spnLoaiBua.getSelectedItem().toString();
        boolean isSuccess = mealDao.themMonAn(currentUserId, tenMon, loaiBua, soLuong, ngayAn);

        if (isSuccess) {
            Toast.makeText(this, "Đã thêm món ăn", Toast.LENGTH_SHORT).show();
            hienThiDanhSach();
        } else {
            Toast.makeText(this, "Thêm món ăn thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void hienThiDanhSach() {
        String ngayAn = edtNgayAn.getText().toString().trim();
        mealPlans.clear();
        mealPlans.addAll(mealDao.getDanhSachTheoNgay(currentUserId, ngayAn));
        mealPlanAdapter.notifyDataSetChanged();
        tvTongCalo.setText("Tổng calo trong ngày: " + mealDao.getTongCaloTheoNgay(currentUserId, ngayAn));
    }

    private void hienThiTuyChonMonAn(Meal meal) {
        EditText edtSoLuongMoi = new EditText(this);
        edtSoLuongMoi.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        edtSoLuongMoi.setText(String.valueOf(meal.getSoLuong()));
        edtSoLuongMoi.setSelectAllOnFocus(true);

        new AlertDialog.Builder(this)
                .setTitle(meal.getTenMon())
                .setMessage("Nhập số lượng mới hoặc xóa món ăn này")
                .setView(edtSoLuongMoi)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String soLuongText = edtSoLuongMoi.getText().toString().trim();
                    if (soLuongText.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int soLuong = Integer.parseInt(soLuongText);
                    if (mealDao.capNhatSoLuong(meal.getId(), soLuong)) {
                        Toast.makeText(this, "Đã cập nhật món ăn", Toast.LENGTH_SHORT).show();
                        hienThiDanhSach();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Xóa", (dialog, which) -> {
                    if (mealDao.xoaMonAn(meal.getId())) {
                        Toast.makeText(this, "Đã xóa món ăn", Toast.LENGTH_SHORT).show();
                        hienThiDanhSach();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
