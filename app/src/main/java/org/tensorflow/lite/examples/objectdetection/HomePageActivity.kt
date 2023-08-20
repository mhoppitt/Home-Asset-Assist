package org.tensorflow.lite.examples.objectdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePageActivity : AppCompatActivity() {

    fun View.visible(v: Int) {
        visibility = v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarAction)

        val homeInsuranceVisibility = intent.getIntExtra(ResultsActivity.KEY_HOME_INSURANCE_VISIBILITY, View.INVISIBLE)
        val homeInsuranceCard = findViewById<CardView>(R.id.cardView3)
        homeInsuranceCard.visible(homeInsuranceVisibility)
    }

    private val navBarAction = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        when (item.itemId) {
            R.id.plus -> {
                val popupMenu: PopupMenu = PopupMenu(this, bottomNavigationView, Gravity.CENTER)
                popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.home_insurance_option ->
                            startActivity(Intent(this, MainActivity::class.java))

                        R.id.business_insurance_option ->
                            Toast.makeText(this@HomePageActivity, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()

                        R.id.income_protection_option ->
                            Toast.makeText(this@HomePageActivity, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    }
                    true
                })
                popupMenu.show()
                return@OnNavigationItemSelectedListener true
            }

            R.id.insurance -> {
                return@OnNavigationItemSelectedListener true
            }

            R.id.rewards -> {
                return@OnNavigationItemSelectedListener true
            }

            R.id.profile -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
