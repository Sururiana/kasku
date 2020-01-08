package com.sururiana.kasku;

import android.app.DatePickerDialog;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.andexert.library.RippleView;

import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    MainActivity M = new MainActivity();

    EditText edit_dari,edit_ke;
    RippleView rip_simpan;
    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        edit_dari = (EditText) findViewById(R.id.edit_dari);
        edit_ke = (EditText) findViewById(R.id.edit_ke);
        rip_simpan = (RippleView) findViewById(R.id.rip_simpan);


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        edit_dari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        //yang di simpan di sqlite
                        M.tanggal_dari =
                                numberFormat.format(year) + "-" + numberFormat.format((month + 1)) + "-" + numberFormat.format(dayOfMonth);

                        //yang di tampilkan
                        edit_dari.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format((month + 1)) + "/" + numberFormat.format(year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        edit_ke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        //yang di simpan di sqlite
                        M.tanggal_ke =
                                numberFormat.format(year) + "-" + numberFormat.format((month + 1)) + "-" + numberFormat.format(dayOfMonth);

                        //yang di tampilkan
                        edit_ke.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format((month + 1)) + "/" + numberFormat.format(year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (M.tanggal_ke.equals("") || M.tanggal_dari.equals("")){
                    Toast.makeText(FilterActivity.this,"Isi data dengan benar", Toast.LENGTH_LONG).show();
                } else {
                    M.filter = true;
                    M.text_filter.setText( edit_dari.getText().toString() + "-" +  edit_ke.getText().toString() );
                    finish();
                }
            }
        });

        getSupportActionBar().setTitle("FILTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp () {
        finish();
        return true;
    }
}
