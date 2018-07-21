package strattons.com.echomusicplayer.activities

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import strattons.com.echomusicplayer.R
import strattons.com.echomusicplayer.activities.MainActivity.statified.drawerlayout
import strattons.com.echomusicplayer.adapters.NavigationDrawerAdapter
import strattons.com.echomusicplayer.fragments.MainActivityFragment

class MainActivity : AppCompatActivity() {

    var navigationDrawerIconList: ArrayList<String> = arrayListOf();
        var imagesForNavDrawer= intArrayOf(R.drawable.navigation_allsongs, R.drawable.navigation_favorites,
                R.drawable.navigation_settings, R.drawable.navigation_aboutus)
object statified {
    var drawerlayout: DrawerLayout? = null
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerlayout=findViewById(R.id.drawer_layout)

        navigationDrawerIconList.add("All Songs")
        navigationDrawerIconList.add("Favourites")
        navigationDrawerIconList.add("Settings")
        navigationDrawerIconList.add("About Us")


        val toggle = ActionBarDrawerToggle(this@MainActivity, drawerlayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerlayout?.addDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragment = MainActivityFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment,mainScreenFragment,"MainScreenFragment")
                .commit()

        var _navigationAdapter=NavigationDrawerAdapter(navigationDrawerIconList,imagesForNavDrawer,this)
        _navigationAdapter.notifyDataSetChanged()


        var navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter=_navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)


    }

    override fun onStart() {
        super.onStart()
    }
}


