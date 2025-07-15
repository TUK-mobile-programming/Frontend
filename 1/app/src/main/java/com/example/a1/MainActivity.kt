package com.example.a1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.a1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* ───────── ViewBinding ───────── */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ───────── NavController ──────── */
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        /* ───────── BottomNavigation ───── */
        binding.bottomNavigation.setupWithNavController(navController)

        /* ───────── 로그인 계열 화면 → 하단바 숨김 ────────
           필요하다면 회원가입·비밀번호 찾기 등도 여기 setOf 안에 추가
        */
        val hideBottomDestinations = setOf(R.id.loginFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id !in hideBottomDestinations
        }
    }
}