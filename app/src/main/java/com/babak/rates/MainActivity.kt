package com.babak.rates

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.babak.rates.ui.RatesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, RatesFragment()).apply {
            commit()
        }

    }
}
