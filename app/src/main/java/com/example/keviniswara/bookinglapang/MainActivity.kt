package com.example.keviniswara.bookinglapang

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.widget.FrameLayout
import com.example.keviniswara.bookinglapang.databinding.ActivityMainBinding
import com.example.keviniswara.bookinglapang.user.home.view.HomeFragment
import com.example.keviniswara.bookinglapang.user.order.view.OrderFragment
import com.example.keviniswara.bookinglapang.user.profile.view.ProfileFragment
import com.example.keviniswara.bookinglapang.user.status.view.StatusFragment
import android.util.TypedValue
import android.support.design.internal.BottomNavigationMenuView
import android.support.v7.widget.Toolbar
import android.view.View
import com.example.keviniswara.bookinglapang.utils.BottomNavigationViewHelper
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private var content: FrameLayout? = null
    private lateinit var mToolbar: Toolbar

    private val mOnNavigationItemSelectedListener
            = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.bottom_nav_bar_1 -> {
                val fragment = HomeFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_nav_bar_2 -> {
                val fragment = OrderFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_nav_bar_3 -> {
                var fragment = StatusFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_nav_bar_4 -> {
                var fragment = ProfileFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mBinding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        content = mBinding.content

        mToolbar = mBinding.toolbar

        setSupportActionBar(mToolbar)

        mBinding.ivHelp.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Bantuan")
            alertDialogBuilder.setMessage("1. Isi data lapangan yang ingin di cari (Nama lapangan, Jenis Olahraga, Tanggal main, Jam main dan jam Selesai)\n" +
                    "\n" +
                    "2. Tunggu notifikasi pemesanan pada halaman Status Pemesanan. \n" +
                    "- Jika status \" Belum Verifikasi \", penjaga lapangan belum menjawab pesanan\n" +
                    "- Jika status \" lapangan tersedia \", lapangan yg dipesan tersedia dan user diharapkan segera melakukan pembayaran\n" +
                    "- Jika status \" tidak tersedia \",  lapangan yg dipesan tidak tersedia, silahkan mengajukan pesanan lain\n" +
                    "\n" +
                    "3.  Jika lapangan tersedia, tekan pesanan pada Status Pemesanan hingga muncul detail pesanan, kemudian tekan tombol bayar. Pilih ingin melakukan pembayaran transfer ke bank apa, dan lakukan transfer ke no rekening bank yang tertera sesuai dengan biaya serta KODE UNIK-nya (3 angka paling belakang)\n" +
                    "\n" +
                    "4. Tunggu konfirmasi dari lapangan pada menu Status Pemesanan. Apabila sudah dikonfirmasi untuk pemesanan dan pembayaran, KorbanBooking hanya tinggal datang ke lapangan pada waktu dan tempat yang telah dipesan. Selamat bermain :)")

            alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

            })

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        val navigation = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val navHelper = BottomNavigationViewHelper()
        navHelper.removeShiftMode(navigation)
        val menuView = navigation.getChildAt(0) as BottomNavigationMenuView

        for (i in 0 until menuView.childCount) {

            // change icon size
            val iconView = menuView.getChildAt(i).findViewById<View>(android.support.design.R.id.icon)
            val layoutParams = iconView.getLayoutParams()
            val displayMetrics = resources.displayMetrics
            layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, displayMetrics).toInt()
            layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, displayMetrics).toInt()
            iconView.setLayoutParams(layoutParams)
        }

        val fragment = HomeFragment()
        addFragment(fragment)
    }

    @SuppressLint("CommitTransaction")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {

            val stackCount = fragmentManager.backStackEntryCount
            var exit: Boolean = false
            var popStack: Boolean = false

            if (supportFragmentManager.findFragmentByTag("HomeFragment") != null &&
                    supportFragmentManager.findFragmentByTag("HomeFragment").isVisible) {
                popStack = true
                exit = true

            } else if (supportFragmentManager.findFragmentByTag("OrderFragment") != null &&
                    supportFragmentManager.findFragmentByTag("OrderFragment").isVisible) {
                popStack = true
                exit = true
            } else if (supportFragmentManager.findFragmentByTag("StatusFragment") != null &&
                    supportFragmentManager.findFragmentByTag("StatusFragment").isVisible) {
                popStack = true
                exit = true
            } else if (supportFragmentManager.findFragmentByTag("ProfileFragment") != null &&
                    supportFragmentManager.findFragmentByTag("ProfileFragment").isVisible) {
                popStack = true
                exit = true
            } else {
                supportFragmentManager.popBackStack()
            }

            if (popStack) {
                for (i in 0 until stackCount) {
                    fragmentManager.popBackStack()
                }
            }

            if (exit) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                .replace(R.id.content, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.getSimpleName())
                .commit()
    }
}
