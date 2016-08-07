package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 1;
	public static final int LEVEL_CITY = 2;
	public static final int LEVEL_COUNTY = 3;
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	
	private TextView titleName;
	private ListView listView;
	
	
	private List<String> dataList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	
	private CoolWeatherDB coolWeatherDB ;
	
	private int currentLevel;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);		
		titleName = (TextView) findViewById(R.id.title_name);			
		listView = (ListView) findViewById(R.id.list_view);	
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCity();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounty();
				} 
				
			}	
			
			
		});
		
		queryProvince();		
	
	}
	
	
	private void queryProvince() {
		//�����ݿ���ȡ�����ݣ������ص�ListView�ؼ���
		try {
			provinceList = coolWeatherDB.loadProvince();
			//Log.e("error", provinceList.get(0).getProvinceName());
			if (null != provinceList && provinceList.size() > 0) {
				dataList.clear();
				for (Province p : provinceList) {
					
					dataList.add(p.getProvinceName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleName.setText("�й�");
				currentLevel = LEVEL_PROVINCE;			
				
			} else {
				queryFromServer(null, "province");
			}
		} catch (Exception e) {
			Log.e("error", e.toString());
			return;
		}
		
		
	}
	/*
		�ӷ������в������ݣ����������ݿ��У��ٴ����ݿ��н�����ȡ��������
	 */
	private void queryFromServer(final String code, final String level) {
		String address;
		if (code == null) {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}
		
		HttpUtil.httpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				
				if ("province".equals(level)) {
					result = Utility.handleProvince(coolWeatherDB, response);
				} else if ("city".equals(level)) {
					result = Utility.handleCity(coolWeatherDB, response, selectedProvince.getId());
				} else {
					result = Utility.handleCounty(coolWeatherDB, response, selectedCity.getId());
				}
				
				if (result) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if ("province".equals(level)) {
								queryProvince();
							} else if ("city".equals(level)) {
								queryCity();
							} else {
								queryCounty();
							}
						}
					});
				}
				
				
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				
			}
		});		
	}

	/*
		���ҳ�����Ϣ�������ݿ��в�ѯ������ʾˢ���б�
	*/
	protected void queryCity() {
		try {
			cityList = coolWeatherDB.loadCity(selectedProvince.getId());
			if (null != cityList && cityList.size() > 0) {
				dataList.clear();
				for (City c : cityList) {
					dataList.add(c.getCityName());
				}
				//ǿ������getView()ˢ���б�
				adapter.notifyDataSetChanged();
				//��λ��ĳ��λ��
				listView.setSelection(0);
				//���ñ�������
				titleName.setText(selectedProvince.getProvinceName());
				//���ĵ�ǰ��LEVEL
				currentLevel = LEVEL_CITY;
				
			} else {
				queryFromServer(selectedProvince.getProvinceCode(), "city");
			}
		} catch (Exception e) {
			Log.e("d", e.toString());
		}
		
	}

	/*
	 
	 
	*/
	protected void queryCounty() {
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if (countyList.size() > 0) {
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(1);
			titleName.setText(selectedCity.getCityName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
		
	}	
	
	/*
	*/
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCity();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		} else {
			finish();
		}
		
	}
	

		
	
	
	
}
