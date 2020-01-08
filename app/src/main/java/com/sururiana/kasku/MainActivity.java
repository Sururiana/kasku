package com.sururiana.kasku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sururiana.kasku.helper.SqliteHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView text_masuk, text_keluar, text_saldo;
    ListView listkas;
    SwipeRefreshLayout swipe_refresh;

    String query_kas, query_total;
    SqliteHelper sqliteHelper;
    Cursor cursor;

    ArrayList<HashMap<String, String>> aruskas = new ArrayList<>();

    public static TextView text_filter;
    public static String transaksi_id, tanggal_dari, tanggal_ke;
    public static boolean filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transaksi_id = ""; tanggal_dari=""; tanggal_ke="";
        filter = false;

        text_masuk  = (TextView) findViewById(R.id.text_masuk);
        text_keluar = (TextView) findViewById(R.id.text_keluar);
        text_saldo  = (TextView) findViewById(R.id.text_saldo);

        listkas     = (ListView) findViewById(R.id.list_kas);
        swipe_refresh     = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        text_filter = (TextView) findViewById(R.id.text_filter);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query_kas = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";
                query_total =
                        "SELECT SUM(jumlah) AS total, (SELECT SUM(jumlah) FROM transaksi WHERE status='Masuk') as masuk," +
                                "(SELECT SUM (jumlah) FROM transaksi WHERE status='Keluar') as keluar FROM transaksi";
                Kas_adapter();
            }
        });

        sqliteHelper = new SqliteHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        query_kas = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";

        query_total =
                "SELECT SUM(jumlah) AS total, (SELECT SUM(jumlah) FROM transaksi WHERE status='Masuk') as masuk," +
                        "(SELECT SUM (jumlah) FROM transaksi WHERE status='Keluar') as keluar FROM transaksi";

        if (filter){
            query_kas = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi " +
                    "WHERE (tanggal >= '" + tanggal_dari +"') AND (tanggal <= '" + tanggal_ke + "') ORDER BY transaksi_id ASC";

            query_total =
                    "SELECT SUM(jumlah) AS total, " +
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status='Masuk' AND (tanggal >= '" + tanggal_dari +"') AND (tanggal <= '" + tanggal_ke + "'))," +
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status='Keluar' AND (tanggal >= '" + tanggal_dari +"') AND (tanggal <= '" + tanggal_ke + "')) "+
                    "FROM transaksi WHERE (tanggal >= '" + tanggal_dari + "') AND (tanggal <= '" + tanggal_ke + "') ";
        }
        Kas_adapter();
    }

    public void Kas_adapter(){
        swipe_refresh.setRefreshing(false);

        aruskas.clear();listkas.setAdapter(null);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(query_kas, null);
        cursor.moveToFirst();

        NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);

        for (int i=0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Log.d("status",cursor.getString(1));

            HashMap<String, String> map = new HashMap<>();
            map.put("transaksi_id", cursor.getString(0));
            map.put("status", cursor.getString(1));
            map.put("jumlah", "Rp "+ rupiah.format(cursor.getDouble(2)));
            //map.put("jumlah", cursor.getString(2));
            map.put("keterangan", cursor.getString(3));
            //map.put("tanggal", cursor.getString(4));
            map.put("tanggal", cursor.getString(5));

            aruskas.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, aruskas, R.layout.list_kas,
                new String[]{"transaksi_id", "status","jumlah","keterangan","tanggal"},
                new int [] {R.id.text_transaksi_id, R.id.text_status, R.id.text_jumlah, R.id.text_keterangan, R.id.text_tanggal} );
        listkas.setAdapter(simpleAdapter);
        listkas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transaksi_id = ((TextView) view.findViewById(R.id.text_transaksi_id)).getText().toString();
                listMenu();
            }
        });

        kasTotal();
    }

    private void kasTotal(){
        NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(query_total, null);
        cursor.moveToFirst();

        text_masuk.setText("Rp "+rupiah.format(cursor.getDouble(1)));
        text_keluar.setText("Rp "+rupiah.format(cursor.getDouble(2)));
        text_saldo.setText("Rp "+ rupiah.format(cursor.getDouble(1) - cursor.getDouble(2))
        );

        if (!filter) { text_filter.setText("SEMUA"); }
        filter = false;
    }

    private void listMenu(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.list_menu);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView text_edit = (TextView)dialog.findViewById(R.id.text_edit);
        TextView text_hapus = (TextView)dialog.findViewById(R.id.text_hapus);

        text_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,EditActivity.class));
            }
        });

        text_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Hapus();
            }
        });
    }

    private void Hapus(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Yakin untuk menghapus?");
        builder.setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                        database.execSQL(
                                "DELETE FROM transaksi WHERE transaksi_id='" + transaksi_id + "'"
                        );
                        Toast.makeText(MainActivity.this,"Transaksi berhasil di hapus",Toast.LENGTH_LONG).show();
                        Kas_adapter();
                    }
                }
        );
        builder.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            startActivity(new Intent(MainActivity.this, FilterActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
