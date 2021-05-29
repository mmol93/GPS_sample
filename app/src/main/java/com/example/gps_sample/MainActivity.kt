package com.example.gps_sample

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.gps_sample.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    // 얻어야 하는 권한의 리스트
    val permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    lateinit var binder : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        // 권한 리스트에 있는 권한 요청하기
        requestPermissions(permission_list, 0)
    }

    // 권한 요청 후 호출되는 메서드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in grantResults){
            if (permission == PackageManager.PERMISSION_DENIED){
                return
            }
        }
        // 위치 정보를 관리하는 매니저 객체를 가져온다
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // 저장되어 있는 위치 정보값을 가져온다
        // 즉, 제일 최근 위치 정보값을 가져온다
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return }
        // GPS_Provider의 정확도가 제일 높음
        val location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        // Network는 두 번째
        val location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        // 그래서 GPS_Provider에 먼저 값이 들어있는지 판단한다
        if (location1 != null){
            showInfo(location1)
        }else if (location2 != null){
            showInfo(location2)
        }
        //
        val listener = LocationListener{
            showInfo(it)
        }
        // 측정 시작
        binder.button.setOnClickListener {
            // GPS_Provider가 사용가능 상태인지 확인
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)
            }
            // Network_Provider가 사용가능 상태인지 확인
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, listener)
            }

        }
        // 측정 중단
        binder.button2.setOnClickListener {
            locationManager.removeUpdates(listener)
        }
    }
    fun showInfo(location : Location){
        if (location != null){
            // 위도와 경도를 이용하여 도시 이름 가져오기
            val geocoder = Geocoder(this, Locale.getDefault())
            // address에는 GPS 결과에 따른 후보군들이 리스트 형태로 들어간다
            // maxResults는 해당 후보를 몇 개를 선정할지 결정(숫자가 낮은 것을 권장함)
            // 즉, address[0]에는 1번 후보가 들어가 있는 것임
            val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            var test = ""
            if (addresses.isNotEmpty()) {
                Log.d("test", "${addresses[0]}")
                Log.d("test", "$addresses")
                test = addresses[0].postalCode
            }
            binder.textView.text = "Provider: ${location.provider}"
            binder.textView2.text = "위도: ${location.latitude}"
            binder.textView3.text = "경도: ${location.longitude}"
            binder.textView4.text = "나라 이름: ${addresses[0].countryName}"
            binder.textView5.text = "도 이름: ${addresses[0].adminArea}"
            binder.textView6.text = "도시 이름: ${addresses[0].locality}"
            binder.textView7.text = "거리 이름은 들어있지 않다"
        }
    }
}