package strattons.com.echomusicplayer.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import strattons.com.echomusicplayer.activities.MainActivity
import strattons.com.echomusicplayer.R
import strattons.com.echomusicplayer.fragments.AboutUsFragment
import strattons.com.echomusicplayer.fragments.FavouriteFragment
import strattons.com.echomusicplayer.fragments.MainActivityFragment
import strattons.com.echomusicplayer.fragments.SettingsFragment

class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray, _context: Context) : RecyclerView.Adapter
<NavigationDrawerAdapter.navViewHolder>() {


    var contentList: ArrayList<String>? = null
    var getImages: IntArray? = null
    var mContext: Context? = null

    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mContext = _context
    }


    override fun onBindViewHolder(holder: navViewHolder, position: Int) {
        holder?.iconGET?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.textGET?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener {
            if (position == 0) {
                val mainScreenFragment = MainActivityFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, mainScreenFragment)
                        .commit()
            } else if (position == 1) {
                val favouriteFragment = FavouriteFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, favouriteFragment)
                        .commit()
            } else if (position == 2) {
                val settingsFragment = SettingsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, settingsFragment)
                        .commit()
            } else{
                val aboutusFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, aboutusFragment)
                        .commit()
            }
            MainActivity.statified.drawerlayout?.closeDrawers()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): navViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_navigationdrawer, parent, false)
        val returnThis = navViewHolder(itemView)
        return returnThis
    }




    override fun getItemCount(): Int {
        return (contentList as ArrayList).size
    }

    class navViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconGET = ImageView(null)
        var textGET = TextView(null)
        var contentHolder: RelativeLayout? = null

        init {
            iconGET = itemView?.findViewById(R.id.icon_navdrawer)
            textGET = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)

        }


    }

}