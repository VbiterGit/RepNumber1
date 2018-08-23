package mypackage.taskjson;

/**
 * Класс городов
 *
 */

public class City {
    private String countryTitle;                //название страны
    private Point point;                        //координаты города [longitude, latitude]
    private String districtTitle;               //название района
    private int cityId;                         //идентификатор города
    private String cityTitle;                   //название города
    private String regionTitle;                 //название региона
    private Station[] stations;                  //станции

    public City(String countryTitle, Point point, String districtTitle, int cityId, String cityTitle, String regionTitle, Station[] stations) {
        this.countryTitle = countryTitle;
        this.point = point;
        this.districtTitle = districtTitle;
        this.cityId = cityId;
        this.cityTitle = cityTitle;
        this.regionTitle = regionTitle;
        this.stations = stations;
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

    public Station[] getStation() {
        return stations;
    }

    public void setStation(Station[] stations) {
        this.stations = stations;
    }
}

