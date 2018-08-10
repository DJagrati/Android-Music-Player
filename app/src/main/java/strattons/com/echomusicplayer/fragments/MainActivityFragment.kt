package strattons.com.echomusicplayer.fragments


import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import strattons.com.echomusicplayer.R
import strattons.com.echomusicplayer.Songs
import strattons.com.echomusicplayer.adapters.MainScreenAdapter
import java.util.*

class MainActivityFragment : Fragment() {
    var getSongsList: ArrayList<Songs>? = null
    var nowPlayingBottom: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var myactivity: Activity? = null
    var _mainScreenAdapter: MainScreenAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_main_activity, container, false)
        setHasOptionsMenu(true)
        activity!!.title = "All Songs"
        visibleLayout = view?.findViewById(R.id.visiblelayout)
        noSongs = view?.findViewById(R.id.noSongs)
        recyclerView = view?.findViewById(R.id.contentMain)
        songTitle = view?.findViewById(R.id.songTitleMainScreen)
        playPauseButton = view?.findViewById(R.id.playPauseMusicButton)
        nowPlayingBottom = view?.findViewById(R.id.hiddenBarMainScreen)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongsFromPhone()
        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        if (getSongsList == null) {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        } else {
            _mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myactivity as Context)
            val mLayoutManager = LinearLayoutManager(myactivity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }

        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            val editor = myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.statified.nameComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {
            val editortwo = myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editortwo?.putString("action_sort_recent", "true")
            editortwo?.putString("action_sort_ascending", "false")
            editortwo?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.statified.dateComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myactivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songtitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songartist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songdata = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateindex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {

                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songtitle)
                var currentArtist = songCursor.getString(songartist)
                var currentData = songCursor.getString(songdata)
                var currentDate = songCursor.getLong(dateindex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))

            }


        }

        return arrayList


    }

}