package com.example.dexter.daycast;

/**
 * Created by dexter on 4/17/16.
 */
public class WeatherItem {
    private String city_id;
    private String city_name;
    private String city_temp;
    private String humiditylevel ;

    public WeatherItem(){

    }
    public WeatherItem(String city_id , String city_name , String city_temp , String humiditylevel){
        this.city_id = city_id ;
        this.city_name = city_name ;
        this.city_temp = city_temp ;
        this.humiditylevel = humiditylevel;
    }
    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }



    public String getHumiditylevel() {
        return humiditylevel;
    }

    public void setHumiditylevel(String humiditylevel) {
        this.humiditylevel = humiditylevel;
    }

    public String getCity_temp() {
        return city_temp;
    }

    public void setCity_temp(String city_temp) {
        this.city_temp = city_temp;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }


}
