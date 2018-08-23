package mypackage.taskjson;

/**
 * Класс станций
 *
 */

public class Station {
    transient private String countryTitle;              //название страны - дублированная инф., не грузим
    private Point point;                                //координаты станции [longitude, latitude]
    transient private String districtTitle;             //название района - дублированная инф., не грузим
    private int cityId;                                 //идентификатор города
    transient private String cityTitle;                 //название города - дублированная инф., не грузим
    transient private String regionTitle;               //название региона - дублированная инф., не грузим
    private int stationId;                              //идентификатор станции
    private String stationTitle;                        //полное название станции

    public Station(String countryTitle, Point point, String districtTitle, int cityId, String cityTitle, String regionTitle, int stationId, String stationTitle) {
        this.countryTitle = countryTitle;
        this.point = point;
        this.districtTitle = districtTitle;
        this.cityId = cityId;
        this.cityTitle = cityTitle;
        this.regionTitle = regionTitle;
        this.stationId = stationId;
        this.stationTitle = stationTitle;
    }

    public String getCountryTitle() {
        return countryTitle;
    }

    public void setCountryTitle(String countryTitle) {
        this.countryTitle = countryTitle;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getDistrictTitle() {
        return districtTitle;
    }

    public void setDistrictTitle(String districtTitle) {
        this.districtTitle = districtTitle;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityTitle() {
        return cityTitle;
    }

    public void setCityTitle(String cityTitle) {
        this.cityTitle = cityTitle;
    }

    public String getRegionTitle() {
        return regionTitle;
    }

    public void setRegionTitle(String regionTitle) {
        this.regionTitle = regionTitle;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationTitle() {
        return stationTitle;
    }

    public void setStationTitle(String stationTitle) {
        this.stationTitle = stationTitle;
    }
}



