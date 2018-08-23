package mypackage.taskjson;

/**
 * Класс работы с БД
 *
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.Context;
import android.content.ContentValues;

public class DBHelper extends SQLiteOpenHelper {

    //запросы по созданию таблиц
    private static final String EXEC_CCF =
            "CREATE TABLE if not exists 'cityfrom' ('city_id' INTEGER PRIMARY KEY NOT NULL UNIQUE, 'countryTitle' text, 'longitude' real, \n" +
            "'latitude' real, 'districtTitle' text,  'cityTitle' text, 'cityTitleUP' text, 'regionTitle' text);";

    private static final String EXEC_CCT =
            "CREATE TABLE if not exists 'cityto' ('city_id' INTEGER PRIMARY KEY NOT NULL UNIQUE, 'countryTitle' text, 'longitude' real," +
            "'latitude' real, 'districtTitle' text, 'cityTitle' text, 'cityTitleUP' text, 'regionTitle' text);";

    private static final String EXEC_CSF =
            "CREATE TABLE if not exists 'stationfrom' ('station_id' INTEGER PRIMARY KEY NOT NULL UNIQUE, 'stationTitle' text, 'longitude' real," +
            "'latitude' real, 'c_id' INTEGER REFERENCES cityfrom (city_id) ON DELETE CASCADE ON UPDATE CASCADE);";

    private static final String EXEC_CST =
            "CREATE TABLE if not exists 'stationto' ('station_id' INTEGER PRIMARY KEY NOT NULL UNIQUE, 'stationTitle' text, 'longitude' real," +
            "'latitude' real, 'c_id' INTEGER REFERENCES cityto (city_id) ON DELETE CASCADE ON UPDATE CASCADE);";

    //запросы по очистке таблиц
    private static final String EXEC_DCF =
            "DELETE FROM cityfrom";

    private static final String EXEC_DCT =
            "DELETE FROM cityto";

    private static final String EXEC_DSF =
            "DELETE FROM stationfrom";

    private static final String EXEC_DST =
            "DELETE FROM stationto";


    //запросы выборки для основных списков
    private static final String QUERY_CF =
            "select city_id as _id, cityTitle as cityTitleF from cityfrom";

    private static final String QUERY_CT =
            "select city_id as _id, cityTitle as cityTitleT from cityto";

    private static final String QUERY_SF =
           "select station_id as _id, stationTitle \n" +
           "from stationfrom \n" +
           "where c_id = ?";

    private static final String QUERY_ST =
            "select station_id as _id, stationTitle \n" +
            "from stationto \n" +
            "where c_id = ?";

    //заполсы выборки для фильтрованных списков
    private static final String QUERY_CF_SEARCH =
            "select city_id as _id, cityTitle as cityTitleF from cityfrom where cityTitleUP like ?";

    private static final String QUERY_CT_SEARCH =
            "select city_id as _id, cityTitle as cityTitleT from cityto where cityTitleUP like ?";

    //запросы для вывода детализации по станциям
    private static final String QUERY_DETAIL_SF =
            "select sf.station_id as _id, sf.stationTitle, cf.cityTitle, cf.districtTitle, cf.regionTitle, cf.countryTitle\n" +
            "from cityfrom cf \n" +
            "join stationfrom sf on cf.city_id = sf.c_id\n" +
            "where sf.station_id = ?";

    private static final String QUERY_DETAIL_ST =
            "select st.station_id as _id, st.stationTitle, ct.cityTitle, ct.districtTitle, ct.regionTitle, ct.countryTitle\n" +
            "from cityto ct\n" +
            "join stationto st on ct.city_id = st.c_id\n" +
            "where st.station_id = ?";

    //константы для адаптеров
    public static final String CITY_NAME = "cityTitleF";
    public static final String CITY_NAME2 = "cityTitleT";
    public static final String STATION_NAME = "stationTitle";
    public static final String ID = "_id";

    public DBHelper(Context context) {
        //конструктор объекта для работы с БД
        super(context, "CityStation.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MainActivity.TAG, "Создаю таблицу cityfrom");
        // создаем таблицу городов отправления
        db.execSQL(EXEC_CCF);

        Log.d(MainActivity.TAG, "Создаю таблицу cityto");
        // создаем таблицу городов прибытия
        db.execSQL(EXEC_CCT);

        // создаем таблицу станций отправлений
        Log.d(MainActivity.TAG, "Создаю таблицу stationfrom");
        db.execSQL(EXEC_CSF);

        // создаем таблицу станций прибытий
        Log.d(MainActivity.TAG, "Создаю таблицу stationto");
        db.execSQL(EXEC_CST);
    }

    public void onDelete(SQLiteDatabase db){
        // очищаем ранее созданные таблицы
        db.execSQL(EXEC_DCF);
        db.execSQL(EXEC_DCT);
        db.execSQL(EXEC_DSF);
        db.execSQL(EXEC_DST);
    }

    public void onInsert(SQLiteDatabase db, City[] cityfrom, City[] cityto) {
        Log.d(MainActivity.TAG, "Заполняем таблицы");
        // Заполняем таблицы городов и станций отправлений
        for (City ctemp: cityfrom) {
            ContentValues cv = new ContentValues();
            cv.put("city_id", ctemp.getCityId());
            cv.put("countryTitle", ctemp.getCountryTitle());
            cv.put("longitude", ctemp.getPoint().getLongitude());
            cv.put("latitude", ctemp.getPoint().getLatitude());
            cv.put("districtTitle", ctemp.getDistrictTitle());
            cv.put("cityTitle", ctemp.getCityTitle());
            cv.put("cityTitleUP", ctemp.getCityTitle().toUpperCase());
            cv.put("regionTitle", ctemp.getRegionTitle());
            db.insert("cityfrom", null, cv);

            for (Station stemp: ctemp.getStation()) {
                ContentValues cv2 = new ContentValues();
                cv2.put("station_id", stemp.getStationId());
                cv2.put("stationTitle", stemp.getStationTitle());
                cv2.put("longitude", stemp.getPoint().getLongitude());
                cv2.put("latitude", stemp.getPoint().getLatitude());
                cv2.put("c_id", stemp.getCityId());
                db.insert("stationfrom", null, cv2);
            }
        }

        // Заполняем таблицы городов и станций прибытий
        for (City ctemp: cityto) {
            ContentValues cv = new ContentValues();
            cv.put("city_id", ctemp.getCityId());
            cv.put("countryTitle", ctemp.getCountryTitle());
            cv.put("longitude", ctemp.getPoint().getLongitude());
            cv.put("latitude", ctemp.getPoint().getLatitude());
            cv.put("districtTitle", ctemp.getDistrictTitle());
            cv.put("cityTitle", ctemp.getCityTitle());
            cv.put("cityTitleUP", ctemp.getCityTitle().toUpperCase());
            cv.put("regionTitle", ctemp.getRegionTitle());
            db.insert("cityto", null, cv);

            for (Station stemp: ctemp.getStation()) {
                ContentValues cv2 = new ContentValues();
                cv2.put("station_id", stemp.getStationId());
                cv2.put("stationTitle", stemp.getStationTitle());
                cv2.put("longitude", stemp.getPoint().getLongitude());
                cv2.put("latitude", stemp.getPoint().getLatitude());
                cv2.put("c_id", stemp.getCityId());
                db.insert("stationto", null, cv2);
            }
        }
    }

    public Cursor getCitiesFrom(SQLiteDatabase db){
        Log.d(MainActivity.TAG, "Выбираем данные по городам отправления");
        Cursor curs;
        curs = db.rawQuery(QUERY_CF, new String[] {});
        return curs;
    }

    public Cursor getCitiesTo(SQLiteDatabase db){
        Log.d(MainActivity.TAG, "Выбираем данные по городам прибытия");
        Cursor curs;
        curs = db.rawQuery(QUERY_CT, new String[] {});
        return curs;
    }

    public Cursor getStationFrom(SQLiteDatabase db, long args){
        Log.d(MainActivity.TAG, "Выбираем данные по станциям отправления для списка");
        Cursor curs;
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(args);
        curs = db.rawQuery(QUERY_SF, selectionArgs);
        return curs;
    }

    public Cursor getStationTo(SQLiteDatabase db, long args){
        Log.d(MainActivity.TAG, "Выбираем данные по станциям прибытия для списка");
        Cursor curs;
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(args);
        curs = db.rawQuery(QUERY_ST, selectionArgs);
        return curs;
    }

    public Cursor getCitiesFromSearch(SQLiteDatabase db, String str){
        Log.d(MainActivity.TAG, "Выбираем данные по городам отправления для поиска");
        Cursor curs;
        String[] selectionArgs = new String[1];
        selectionArgs[0] = '%' + str.toUpperCase() + '%';
        curs = db.rawQuery(QUERY_CF_SEARCH, selectionArgs);
        return curs;
    }

    public Cursor getCitiesToSearch(SQLiteDatabase db, String str){
        Log.d(MainActivity.TAG, "Выбираем данные по городам прибытия для поиска");
        Cursor curs;
        String[] selectionArgs = new String[1];
        selectionArgs[0] = '%' + str.toUpperCase() + '%';
        curs = db.rawQuery(QUERY_CT_SEARCH, selectionArgs);
        return curs;
    }

    public Cursor getSFDetail(SQLiteDatabase db, long args){
        Log.d(MainActivity.TAG, "Выбираем детальные данные по станции");
        Cursor curs;
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(args);
        curs = db.rawQuery(QUERY_DETAIL_SF, selectionArgs);
        return curs;
    }

    public Cursor getSTDetail(SQLiteDatabase db, long args){
        Log.d(MainActivity.TAG, "Выбираем детальные данные по станции");
        Cursor curs;
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(args);
        curs = db.rawQuery(QUERY_DETAIL_ST, selectionArgs);
        return curs;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //тут ничего не делаем
    }
}