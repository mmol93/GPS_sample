package com.example.gps_sample

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.gps_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // 얻어야 하는 권한의 리스트
    val permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    lateinit var binder : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        // 위치 정보를 관리하는 매니저를 추출
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
            binder.textView.text = "Provider: ${location.provider}"
            binder.textView2.text = "위도: ${location.latitude}"
            binder.textView3.text = "경도: ${location.longitude}"
        }
    }
}