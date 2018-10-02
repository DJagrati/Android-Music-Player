package strattons.com.echomusicplayer.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import strattons.com.echomusicplayer.R
import strattons.com.echomusicplayer.Songs
import strattons.com.echomusicplayer.adapters.FavouritesAdapter
import strattons.com.echomusicplayer.databases.EchoDatabase
import java.util.*

class favouriteFragment : Fragment() {
    var myActivity: Activity? = null
    var getSongsList: ArrayList<Songs>? = null
    var noFavourites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent: EchoDatabase? = null
    var refreshList: ArrayList<Songs>? = null
    var getListfromDatabase: ArrayList<Songs>? = null

    object Statified {

        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        activity!!.title = "Favourites"
        noFavourites = view?.findViewById(R.id.noFavourites)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        recyclerView = view?.findViewById(R.id.favouriteRecycler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(myActivity)
        display_favorites_by_searching()
        bottomBarSetup()
        getSongsList = getSongsFromPhone()
        if (getSongsList == null) {
            recyclerView?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        } else {
            var favouritesAdapter = FavouritesAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = favouritesAdapter
            recyclerView?.setHasFixedSize(true)

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
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

    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.statified.currentSongHelper?.songTitle)
            SongPlayingFragment.statified.mediaplayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.statified.currentSongHelper?.songTitle)
                SongPlayingFragment.staticated.onSongComplete()
            })
            if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer = SongPlayingFragment.statified.mediaplayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.statified.currentSongHelper?.songTitle)
            args.putInt("songId", SongPlayingFragment.statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.statified.fetchSongs)
            args.putString("FavBottomBar", "success")
            songPlayingFragment.arguments = args
            fragmentManager!!.beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()

        })

        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.statified.mediaplayer?.pause()
                trackPosition = SongPlayingFragment.statified.mediaplayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.statified.mediaplayer?.seekTo(trackPosition)
                SongPlayingFragment.statified.mediaplayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    fun display_favorites_by_searching() {
        if (favoriteContent?.checkSize() as Int > 0) {
            refreshList = ArrayList<Songs>()
            getListfromDatabase = favoriteContent?.queryDBList()
            val fetchListfromDevice = getSongsFromPhone()
            if (fetchListfromDevice != null) {
                for (i in 0..fetchListfromDevice?.size - 1) {
                    for (j in 0..getListfromDatabase?.size as Int - 1) {
                        if (getListfromDatabase?.get(j)?.songID ===
                                fetchListfromDevice?.get(i)?.songID) {
                            refreshList?.add((getListfromDatabase as ArrayList<Songs>)
                                    [j])
                        }
                    }
                }
            } else {
            }
            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavourites?.visibility = View.VISIBLE
            } else {
                val favoriteAdapter = FavouritesAdapter(refreshList as ArrayList<Songs>,
                        myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }
        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        }
    }
}




