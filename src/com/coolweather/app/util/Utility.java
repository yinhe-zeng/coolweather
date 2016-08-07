package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	//解析返回的数据，并将数据存储入数据库中
	public synchronized static boolean handleProvince(CoolWeatherDB db, String response) {
		//判断response是否为空
		if (!TextUtils.isEmpty(response)) {
			
			String[] allProvince = response.split(",");
			for (String p : allProvince) {
				Province province = new Province();
				String[] array = p.split("\\|");
				province.setProvinceCode(array[0]);
				province.setProvinceName(array[1]);
				db.savedProvince(province);
								
			}
			return true;
		}
		return false;		
	}

	public synchronized static boolean handleCity (CoolWeatherDB db, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			
			for (String p : allCities) {
				City city = new City();
				String array[] = p.split("\\|");
				city.setCityCode(array[0]);
				city.setCityName(array[1]);
				city.setProvinceId(provinceId);
				db.savedCity(city);
				
			}
			return true;
		}
		return false;		
	}

	public synchronized static boolean handleCounty (CoolWeatherDB db, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			
			for (String p : allCounties) {
				County county = new County();
				String array[] = p.split("\\|");
				county.setCountyCode(array[0]);
				county.setCountyName(array[1]);
				county.setCityId(cityId);
				db.savedCounty(county);
				
			}
			return true;
		}
		
		return false;
		
	}
}
