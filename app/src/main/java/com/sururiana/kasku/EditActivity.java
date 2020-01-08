package com.sururiana.kasku;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.sururiana.kasku.helper.SqliteHelper;

import java.util.Calendar;
import java.util.Locale;

import static com.sururiana.kasku.MainActivity.transaksi_id;

public class EditActivity extends AppCompatActivity {

    MainActivity M = new MainActivity();

    EditText edit_tanggal;

    RadioGroup radio_status;
    RadioButton radio_masuk,radio_keluar;
    EditText edit_jumlah, edit_keterangan;
    Button btn_simpan;
    RippleView rip_simpan;
    Cursor cursor;

    String tanggal, status;
    SqliteHelper sqliteHelper;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        status = "";
        tanggal = ""; //untuk menyimpan yyyy-mm-dd ke sqlite

        radio_status = (RadioGroup) findViewById(R.id.radio_status);
        radio_masuk = (RadioButton) findViewById(R.id.radio_masuk);
        radio_keluar = (RadioButton) findViewById(R.id.radio_keluar);

        edit_jumlah = (EditText) findViewById(R.id.edit_jumlah);
        edit_keterangan = (EditText) findViewById(R.id.edit_keterangan);
        rip_simpan = (RippleView) findViewById(R.id.rip_simpan);

        edit_tanggal = (EditText) findViewById(R.id.edit_tanggal);

        sqliteHelper = new SqliteHelper(this);
        java.text.NumberFormat rupiah = java.text.NumberFormat.getInstance(Locale.GERMANY);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(
                "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi WHERE transaksi_id='" + M.transaksi_id + "'",
                null);
        cursor.moveToFirst();

        status = cursor.getString(1);
        switch (status) {
            case "Masuk":
                radio_masuk.setChecked(true);
                break;
            case "Keluar":
                radio_keluar.setChecked(true);
                break;
        }

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

        //edit_jumlah.setText(rupiah.format(cursor.getDouble(2)));
        edit_jumlah.setText(cursor.getString(2));
        edit_keterangan.setText(cursor.getString(3));

        tanggal = cursor.getString(4);
        edit_tanggal.setText(cursor.getString(5));

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        edit_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        //yang di simpan di sqlite
                        tanggal =
                                numberFormat.format(year) + "-" + numberFormat.format((month + 1)) + "-" + numberFormat.format(dayOfMonth);

                        //yang di tampilkan
                        edit_tanggal.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format((month + 1)) + "/" +
                                numberFormat.format(year));
                        }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(status.equals("") || edit_jumlah.getText().toString().equals("") || edit_keterangan.getText().toString().equals("")){

                    Toast.makeText(EditActivity.this,"isi data dengan benar",Toast.LENGTH_LONG).show();
                }else {
                    SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                    database.execSQL(
                            "UPDATE transaksi SET status='"+ status +"', jumlah='"+ edit_jumlah.getText().toString() +"', keterangan='" + edit_keterangan.getText().toString() + "', " +
                                    "tanggal='" + tanggal+ "' WHERE transaksi_id='"+ M.transaksi_id +"'"
                    );
                    Toast.makeText(EditActivity.this,"Perubahan Transaksi berhasil di simpan",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        getSupportActionBar().setTitle("Edit Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
    @Override
    public boolean onSupportNavigateUp () {
        finish();
        return true;
        }
}



