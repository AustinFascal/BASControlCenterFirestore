package com.ptbas.controlcenter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager.widget.ViewPager
import com.droidnet.DroidNet
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ptbas.controlcenter.adapter.OnBoardViewPagerAdapter
import com.ptbas.controlcenter.auth.LoginActivity
import com.ptbas.controlcenter.auth.RegisterActivity
import com.ptbas.controlcenter.model.OnBoardingData


class MainActivity : AppCompatActivity() {

    var authProfile: FirebaseAuth? = null

    var onBoardViewPagerAdapter: OnBoardViewPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var onBoardViewPager: ViewPager? = null
    var next: TextView? = null
    var position = 0
    var sharedPreferences: SharedPreferences? = null

    var mDroidNet: DroidNet? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(2000)
        val splashScreen =  installSplashScreen()
        setContentView(R.layout.activity_main)

        /*if (restorePrefData()){
            val i = Intent(applicationContext, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }*/
        DroidNet.init(this)

        tabLayout = findViewById(R.id.tabIndicator)
        next = findViewById(R.id.next)

        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(OnBoardingData("All-in-One Place", "Aplikasi pusat kontrol manajemen PT Bintang Andalan Sejahtera", R.drawable.img_desc1))
        onBoardingData.add(OnBoardingData("Easy Management", "Temukan berbagai fitur untuk memudahkan manajemen aktivitas operasional", R.drawable.img_desc2))
        onBoardingData.add(OnBoardingData("Seamless and Responsive", "Akses aplikasi melalui berbagai macam perangkat secara responsif", R.drawable.img_desc3))

        setOnBoardingViewPagerAdapter(onBoardingData)

        position = onBoardViewPager!!.currentItem

        next?.setOnClickListener{
            if (position<onBoardingData.size){
                position++
                onBoardViewPager!!.currentItem = position
            }
            if (position == onBoardingData.size){
                //savePrefData()
                val i = Intent(applicationContext, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            }
        }

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position==onBoardingData.size - 1){
                    next!!.text = "Mulai Sekarang!"
                } else{
                    next!!.text = "Selanjutnya"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        supportActionBar?.hide()

        authProfile = FirebaseAuth.getInstance()
        val firebaseUser: FirebaseUser? = authProfile!!.currentUser

        if (firebaseUser != null) {
            val i = Intent(this, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardViewPager = findViewById(R.id.screenPager)
        onBoardViewPagerAdapter = OnBoardViewPagerAdapter(this, onBoardingData)
        onBoardViewPager!!.adapter = onBoardViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardViewPager)
    }

    /*private fun savePrefData(){
        sharedPreferences = applicationContext.getSharedPreferences("pref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean("isFirstTimeRun", true)
        editor.apply()
    }*/

    /*private fun restorePrefData(): Boolean{
        sharedPreferences = applicationContext.getSharedPreferences("pref", MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", false)
    }*/

    fun login (view: View) {
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    fun register (view: View) {
        val i = Intent(this, RegisterActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners()
    }

}