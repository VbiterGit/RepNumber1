package mypackage.taskjson;
/**
 * Активити для работы со списком городов/станций
 *
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.widget.DatePicker;
import android.content.Context;
import android.widget.Toast;

public class CitiesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private int DIALOG_DATE = 1;
    private int myYear = 2018;
    private int myMonth = 7;
    private int myDay = 19;
    private TextView tvDate;
    private Cursor cCitiesF;
    private Cursor cCitiesT;
    private Cursor DetailSF;
    private Cursor DetailST;
    private SimpleCursorTreeAdapter adapter;
    private SimpleCursorTreeAdapter adapterCT;
    private ExpandableListView elvCF;
    private ExpandableListView elvCT;

    public DBHelper dbHelper;
    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        tvDate = (TextView) findViewById(R.id.tvDate);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        //выбираем данные из БД для списка и привязываем их через адаптер к ExpandableListView
        setAdapterAllListView();

        // привязываем к списку городов отправлений обработчик нажатие на элемент дочернего списка
        elvCF.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                DetailSF = dbHelper.getSFDetail(db, id);
                String text = StrOfCursor(DetailSF);
                //выводим детализацию по станции
                Toast.makeText(CitiesActivity.this, text, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        // привязываем к списку городов прибытий обработчик нажатие на элемент дочернего списка
        elvCT.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                DetailST = dbHelper.getSTDetail(db, id);
                String text = StrOfCursor(DetailST);
                //выводим детализацию по станции
                Toast.makeText(CitiesActivity.this, text, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    //класс адаптер для ExpandableListView
    class MyAdapter extends SimpleCursorTreeAdapter {

        public MyAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout,
                         String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        }

        public Cursor getChildrenCursor(Cursor groupCursor) {
            // получаем курсор по станциям для конкретной группы из списка городов отправлений
            if (groupCursor.getColumnName(1).equals(dbHelper.CITY_NAME)){
                Log.d(MainActivity.TAG, "Ch1 " + groupCursor.getColumnIndex(dbHelper.ID));
                int idColumn = groupCursor.getColumnIndex(dbHelper.ID);
                Log.d(MainActivity.TAG, "Ch2 " + groupCursor.getInt(idColumn));
                //возвращаем список станций для данной группы
                return dbHelper.getStationFrom(db, groupCursor.getInt(idColumn));
            } else
                // получаем курсор по станциям для конкретной группы из списка городов прибытий
                if(groupCursor.getColumnName(1).equals(dbHelper.CITY_NAME2)){
                    Log.d(MainActivity.TAG, "Ch3 " + groupCursor.getColumnIndex(dbHelper.ID));
                    int idColumn = groupCursor.getColumnIndex(dbHelper.ID);
                    Log.d(MainActivity.TAG, "Ch4 " + groupCursor.getInt(idColumn));
                    //возвращаем список станций для данной группы
                    return dbHelper.getStationTo(db, groupCursor.getInt(idColumn));
                } else return null;
        }
    }

    //привязываем адаптер для списка без фильтра
    public void setAdapterAllListView(){
        //формируем список городов и станций отправлений
        cCitiesF = dbHelper.getCitiesFrom(db);
//        logCursor(cCitiesF);

        startManagingCursor(cCitiesF);
        String [] groupFrom = { dbHelper.CITY_NAME };
        int[] groupTo = { android.R.id.text1 };
        // сопоставление данных и View для элементов
        String[] childFrom = { dbHelper.STATION_NAME };
        int[] childTo = { android.R.id.text1 };

        // создаем адаптер
        adapter = new MyAdapter(this, cCitiesF, android.R.layout.simple_expandable_list_item_1,
                groupFrom, groupTo, android.R.layout.simple_list_item_1, childFrom, childTo);
        elvCF = (ExpandableListView) findViewById(R.id.elvCityFrom);
        elvCF.setAdapter(adapter);
        Log.d(MainActivity.TAG, "Адаптер elvCF привязан");

        //формирвем список городов и станций прибытий
        cCitiesT = dbHelper.getCitiesTo(db);
//        logCursor(cCitiesF);
        startManagingCursor(cCitiesT);
        String [] groupFrom2 = { dbHelper.CITY_NAME2 };

        // создаем адаптер
        adapterCT = new MyAdapter(this, cCitiesT, android.R.layout.simple_expandable_list_item_1,
                groupFrom2, groupTo, android.R.layout.simple_list_item_1, childFrom, childTo);
        elvCT = (ExpandableListView) findViewById(R.id.elvCityTo);
        elvCT.setAdapter(adapterCT);
        Log.d(MainActivity.TAG, "Адаптер elvCT привязан");
    }

    //привязываем адаптер для списка с фильтром
    public void setAdapterSearchListView(String s){
        //формируем список городов и станций отправлений c учетом фильтра
        cCitiesF = dbHelper.getCitiesFromSearch(db, s);
//        logCursor(cCitiesF);

        startManagingCursor(cCitiesF);
        String [] groupFrom = { dbHelper.CITY_NAME };
        int[] groupTo = { android.R.id.text1 };
        // сопоставление данных и View для элементов
        String[] childFrom = { dbHelper.STATION_NAME };
        int[] childTo = { android.R.id.text1 };


        adapter = new MyAdapter(this, cCitiesF, android.R.layout.simple_expandable_list_item_1,
                groupFrom, groupTo, android.R.layout.simple_list_item_1, childFrom, childTo);
        elvCF = (ExpandableListView) findViewById(R.id.elvCityFrom);
        elvCF.setAdapter(adapter);

        Log.d(MainActivity.TAG, "Адаптер elvCF привязан для поиска");

        //формирвем список городов и станций прибытий
        cCitiesT = dbHelper.getCitiesToSearch(db, s);
//        logCursor(cCitiesT);

        startManagingCursor(cCitiesT);
        String [] groupFrom2 = { dbHelper.CITY_NAME2 };

        // создаем адаптер
        adapterCT = new MyAdapter(this, cCitiesT, android.R.layout.simple_expandable_list_item_1,
                groupFrom2, groupTo, android.R.layout.simple_list_item_1, childFrom, childTo);
        elvCT = (ExpandableListView) findViewById(R.id.elvCityTo);
        elvCT.setAdapter(adapterCT);
        Log.d(MainActivity.TAG, "Адаптер elvCT привязан для поиска");
    }

    //создаем меню с поиском
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    //обработчик запуска фильтра в поиске по нажатию на кнопку
    @Override
    public boolean onQueryTextSubmit(String s) {
        //все подвязываем с учетом фильтра
        setAdapterSearchListView(s);
        return false;
    }

    //обработчик фильтра в поиске по набору текста
    @Override
    public boolean onQueryTextChange(String s) {
        //подвязываем адаптеры как при первом запуске формы, если поиск пустой
        if (s.equals("")) setAdapterAllListView();
        return false;
    }

    //по закрытию активити рубим коннект с БД
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        dbHelper.close();
    }

    //формируем строку для вывода детализации по станции из списка
    public String StrOfCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                str = "Cтанция: " + c.getString(c.getColumnIndex("stationTitle")) + "\n" +
                      "Город: " + c.getString(c.getColumnIndex("cityTitle")) + "\n" +
                      "Район: " + c.getString(c.getColumnIndex("districtTitle")) + "\n" +
                      "Регион: " + c.getString(c.getColumnIndex("regionTitle")) + "\n" +
                      "Cтрана: " + c.getString(c.getColumnIndex("countryTitle"));
                return str;
            }
        } else {
            Log.d(MainActivity.TAG, "Станция не выбрана, курсор пустой");
        }
        return null;
    }

    //вызываем диалог-календарь по нажатию на TextEdit для даты
    public void onclick(View view) {
        showDialog(DIALOG_DATE);
    }

    // создаем диалоговое окно для ввода даты в календаре
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    //сохраняем введенную дату в TextEdit
    OnDateSetListener myCallBack = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear + 1;
            myDay = dayOfMonth;
            tvDate.setText("Выбранная дата: " + myDay + "/" + myMonth + "/" + myYear);
        }
    };

    //метод для отладки, пишет в лог содержимое курсора
    public void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(MainActivity.TAG, str);
                } while (c.moveToNext());
            }
        } else
            Log.d(MainActivity.TAG, "Курсор пустой");
    }

}


