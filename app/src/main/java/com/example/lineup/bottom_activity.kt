package com.example.lineup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.lineup.databinding.ActivityBottomBinding
import java.util.Scanner


class bottom_activity : AppCompatActivity() {

    private lateinit var binding : ActivityBottomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBottomBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        replaceFragments(Qr_code())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Leaderboard -> replaceFragments(Leaderboard())
                R.id.QR_code -> replaceFragments(Qr_code())
                R.id.route -> replaceFragments(route())
            }
            return@setOnItemSelectedListener true


        }
        true
    }
    private fun replaceFragments(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }


    }
