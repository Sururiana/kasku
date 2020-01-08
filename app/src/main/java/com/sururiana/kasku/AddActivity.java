package com.sururiana.kasku;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.sururiana.kasku.helper.SqliteHelper;

import java.text.NumberFormat;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    RadioGroup radio_status;
    EditText edit_jumlah, edit_keterangan;
    Button btn_simpan;
    RippleView rip_simpan;

    String status;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        status = "";
        sqliteHelper = new SqliteHelper(this);

        radio_status = (RadioGroup) findViewById(R.id.radio_status);
        edit_jumlah = (EditText) findViewById(R.id.edit_jumlah);
        edit_keterangan = (EditText) findViewById(R.id.edit_keterangan);
        btn_simpan = (Button) findViewById(R.id.btn_simpan);
        rip_simpan = (RippleView) findViewById(R.id.rip_simpan);

        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_masuk:
                        status = "Masuk";
                        break;
                    case R.id.radio_keluar:
                        status = "Keluar";
                        break;
                }
                Log.d("Log Status", status);
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(status.equals("") || edit_jumlah.getText().toString().equals("") || edit_keterangan.getText().toString().equals("")){

                    Toast.makeText(AddActivity.this,"isi data dengan benar",Toast.LENGTH_LONG).show();
                }else {
                    SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                    database.execSQL(
                            "INSERT INTO transaksi (status, jumlah, keterangan) VALUES ('" +
                                    status + "','" + edit_jumlah.getText().toString() + "','" + edit_keterangan.getText().toString() + "')"
                    );
                    Toast.makeText(AddActivity.this,"Transaksi berhasil di simpan",Toast.LENGTH_LONG).show();
                    finish();
                }

//                String jumlah = edit_jumlah.getText().toString();
//                String keterangan = edit_keterangan.getText().toString();
//
//                //Contoh Toast
//                Toast.makeText(AddActivity.this,"Jumlah : "+ jumlah + " Keterangan : " + keterangan,
//                        Toast.LENGTH_LONG).show();
            }
        });
//        btn_simpan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                String jumlah = edit_jumlah.getText().toString();
////                String keterangan = edit_keterangan.getText().toString();
//
//
//                //Contoh Toast
////                Toast.makeText(AddActivity.this,"Jumlah : "+ jumlah + " Keterangan : " + keterangan,
////                        Toast.LENGTH_LONG).show();
//                //Contoh Log
//                //Log.d("Log pesan","Testing Log");
//
//                //Snackbar.make(v, "Testing Snackbar", //view, conten
//                //        Snackbar.LENGTH_LONG).show();
//
//            }
//        });

        //set title
        getSupportActionBar().setTitle("Tambah Data");
        //tanda back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    // click tanda back
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
