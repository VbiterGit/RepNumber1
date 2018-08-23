package mypackage.taskjson;

/**
 * Основной активити
 *
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    private final String FILENAME_SD = "/storage/71C0-171B/allStations.json";
    public static final String TAG = "TaskJsonLog";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private CitiesFrom cf;
    private CitiesTo ct;
    private TextView tvSl;
    private TextView tvDBl;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private Handler h;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //устанавливаем обработчики на кнопки
        Button btnDBLoad = (Button) findViewById(R.id.btnDBLoad);
        Log.d(TAG, "Добавляем обработчик на кнопку btnDBLoad");
        btnDBLoad.setOnClickListener(this);

        Button btnJsonLoad = (Button) findViewById(R.id.btnJsonLoad);
        Log.d(TAG, "Добавляем обработчик на кнопку btnJsonLoad");
        btnJsonLoad.setOnClickListener(this);

        tvSl = (TextView) findViewById(R.id.tvStateLoad);

        //проверяем, что у приложения есть разрешение на работу с файлами, и выдаем его при необходимости
        Log.d(TAG, "Проверяем и выдаем разрешение на чтение");
        verifyStoragePermissions(this);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        tvDBl = (TextView) findViewById(R.id.tvDBLoad);
        //создаю Handler для отдельного потока по загрузки данных в БД
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                switch (msg.what){
                    case 1 :
                        tvDBl.setText("Очистка таблиц в БД");
                        break;
                    case 2 :
                        tvDBl.setText("Заполнение таблиц в БД");
                        break;
                    case 3 :
                        tvDBl.setText("Таблицы в БД заполнены");
                        break;
                    case 4 :
                        tvDBl.setText("Загрузите сначала данные из JSON");
                        break;
                }
            }
        };
    }

    //по закрытию активити рубим коннект с БД
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        dbHelper.close();
    }

    //создаем главное меню "Расписание", "О программе"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //обработчик меню, открывает новые активити
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.item1 :
                intent = new Intent(this, CitiesActivity.class);
                startActivity(intent);
                break;
            case R.id.item2 :
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //проверка прав на запись на флешку
    public void verifyStoragePermissions(Activity activity) {
        // проверяем есть ли уже права
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //если прав нет, от предлагаем их выдать
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    //обработчик нажатия кнопок на основном активити
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnDBLoad:
                // запускаем отдельный поток для загрузки данных в БД, чтобы не вешать активити
                Thread thread = new Thread(MainActivity.this);
                thread.start();
                break;
            case R.id.btnJsonLoad:
                //загружаем из json данные по городам/станциям отправления/прибытия
                Log.d(TAG, "Загрузка данных по городам/станциям отправления");
                cf = loadCitiesFrom();
                Log.d(TAG, "Данные по городам/станциям отправления загружены");

                Log.d(TAG, "Загрузка данные по городам/станциям прибытия");
                ct = loadCitiesTo();
                Log.d(TAG, "Данные по городам/станциям прибытия загружены");
                tvSl.setText("Данные из json загружены");
                break;
        }
    }

    //поток по загрузке данных в БД
    @Override
    public void run() {
        if (cf != null && ct!= null){
            Log.d(TAG, "Поток загрузки данных в БД - чистим таблицы");
            h.sendEmptyMessage(1);
            dbHelper.onDelete(db);

            Log.d(TAG, "Добавляю в БД города и станции отправления/прибытия");
            h.sendEmptyMessage(2);
            dbHelper.onInsert(db, cf.getCitiesFrom(), ct.getCitiesTo());
            Log.d(TAG, "Данные в БД добавлены");
            h.sendEmptyMessage(3);
        } else {
            Log.d(TAG, "JSON пустой");
            h.sendEmptyMessage(4);
        }
    }

    //загрузка данных из json по городам/станциям отправления
    public CitiesFrom loadCitiesFrom() {
        CitiesFrom citiesFrom = null;
        //проверка доступности sd-карты
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }
        // формируем объект File, который содержит путь к файлу на внешнем носителе
        File sdFile = new File(FILENAME_SD);

        InputStreamReader streamReaderCF = null;
        try {
            //открываем поток и читаем данные из json
            streamReaderCF = new InputStreamReader(new FileInputStream(sdFile));
            Gson gsonCF = new Gson();
            citiesFrom = gsonCF.fromJson(streamReaderCF, CitiesFrom.class);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        } finally {
            try {
                //закрываем поток
                streamReaderCF.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return citiesFrom;
        }
    }

    //загрузка данных из json по городам/станциям отправления
    public CitiesTo loadCitiesTo() {
        CitiesTo citiesTo = null;
        //проверка доступности sd-карты
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }
        // формируем объект File, который содержит путь к файлу на внешнем носителе
        File sdFile = new File(FILENAME_SD);
        InputStreamReader streamReaderCT = null;
        try {
            //открываем поток и читаем данные из json
            streamReaderCT = new InputStreamReader(new FileInputStream(sdFile));
            Gson gsonCT = new Gson();
            citiesTo = gsonCT.fromJson(streamReaderCT, CitiesTo.class);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        } finally {
            try {
                //закрываем поток
                streamReaderCT.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return citiesTo;
        }
    }
}
