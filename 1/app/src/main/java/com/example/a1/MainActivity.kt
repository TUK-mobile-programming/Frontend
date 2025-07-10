// MainActivity.kt (Bottom Navigation Bar 및 프래그먼트 전환 로직 포함)
package com.example.a1 // 실제 패키지 이름 확인

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.a1.Homefragment
import com.example.a1.Addfragment
import com.example.a1.Listfragment
import com.example.a1.Mypagefragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // activity_main.xml 로드

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 앱 시작 시 기본 프래그먼트 설정 (로그인 후 메인으로 넘어왔을 때 HomeFragment가 보이도록)
        if (savedInstanceState == null) {
            replaceFragment(Homefragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homefragment -> {
                    replaceFragment(Homefragment())
                    true
                }
                R.id.addfragment -> {
                    replaceFragment(Addfragment())
                    true
                }
                R.id.listfragment -> {
                    replaceFragment(Listfragment())
                    true
                }
                R.id.mypagefragment -> {
                    replaceFragment(Mypagefragment())
                    true
                }
                R.id.capsulefragment -> { // navi_menu.xml의 ID와 일치시켜야 합니다.
                    replaceFragment(CapsuleFragment()) // Navigation Graph의 ID로 이동
                    true
                }
                else -> false
            }
        }
    }

    // 프래그먼트를 교체하는 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame_layout, fragment) // activity_main.xml의 FrameLayout ID
            .commit()
    }
}