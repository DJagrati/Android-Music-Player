package strattons.com.echomusicplayer.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import strattons.com.echomusicplayer.CurrentSongHelper
import strattons.com.echomusicplayer.R
import strattons.com.echomusicplayer.Songs
import strattons.com.echomusicplayer.databases.EchoDatabase
import java.util.*
import java.util.concurrent.TimeUnit


class SongPlayingFragment : Fragment() {

    object statified {
        var myActivity: Activity? = null
        var mediaplayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fab: ImageButton? = null
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"

        var favouriteContent: EchoDatabase? = null

        var updateSongTime = object : Runnable {
            override fun run() {
                val getcurrent = mediaplayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",

                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as
                                Long))))
                seekbar?.setProgress(getcurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }

        }

    }

    object staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"
        fun onSongComplete() {
            if (statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                statified.currentSongHelper?.isPlaying = true
            } else {
                if (statified.currentSongHelper?.isLoop as Boolean) {
                    statified.currentSongHelper?.isPlaying = true
                    var nextSong = statified.fetchSongs?.get(statified.currentPosition)

                    statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    statified.currentSongHelper?.songPath = nextSong?.songData
                    statified.currentSongHelper?.songArtist = nextSong?.artist
                    statified.currentSongHelper?.currentPosition = statified.currentPosition
                    statified.currentSongHelper?.songId = nextSong?.songID as Long

                    updateTextViews(statified.currentSongHelper?.songTitle as String, statified.currentSongHelper?.songArtist as String)
                    statified.mediaplayer?.reset()
                    try {
                        statified.mediaplayer?.setDataSource(statified.myActivity, Uri.parse(statified.currentSongHelper?.songPath))
                        statified.mediaplayer?.prepare()
                        statified.mediaplayer?.start()
                        processInformation(statified.mediaplayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    statified.currentSongHelper?.isPlaying = true
                }

            }
            if ((statified.favouriteContent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
                statified.fab?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                statified.fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated = songTitle
            var songArtistUpdated = songArtist
            if (songTitle.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistUpdated = "unknown"
            }
            statified.songTitleView?.setText(songTitleUpdated)
            statified.songArtistView?.setText(songArtistUpdated)

        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            statified.seekbar?.max = finalTime
            statified.startTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            )

            statified.endTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            )
            statified.seekbar?.setProgress(startTime)
            Handler().postDelayed(statified.updateSongTime, 1000)
        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                statified.currentPosition = statified.currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(statified.fetchSongs?.size?.plus(1) as Int)
                statified.currentPosition = randomPosition


            }
            if (statified.currentPosition == statified.fetchSongs?.size) {
                statified.currentPosition = 0
            }
            statified.currentSongHelper?.isLoop = false
            var nextSong = statified.fetchSongs?.get(statified.currentPosition)
            statified.currentSongHelper?.songPath = nextSong?.songData
            statified.currentSongHelper?.songTitle = nextSong?.songTitle
            statified.currentSongHelper?.songArtist = nextSong?.artist
            statified.currentSongHelper?.songId = nextSong?.songID as Long

            updateTextViews(statified.currentSongHelper?.songTitle as String, statified.currentSongHelper?.songArtist as String)

            statified.mediaplayer?.reset()
            try {
                statified.mediaplayer?.setDataSource(statified.myActivity, Uri.parse(statified.currentSongHelper?.songPath))
                statified.mediaplayer?.prepare()
                statified.mediaplayer?.start()
                processInformation(statified.mediaplayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if ((statified.favouriteContent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
                statified.fab?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                statified.fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }
    }

    var mAcceleration: Float = 0f
    var mAccelerateCurrent: Float = 0f
    var mAccelerationLast: Float = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity!!.title = "Now Playing"
        statified.seekbar = view?.findViewById(R.id.seekBar)
        statified.startTimeText = view?.findViewById(R.id.startTime)
        statified.endTimeText = view?.findViewById(R.id.endTime)
        statified.playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        statified.nextImageButton = view?.findViewById(R.id.nextButton)
        statified.previousImageButton = view?.findViewById(R.id.previousButton)
        statified.loopImageButton = view?.findViewById(R.id.loopButton)
        statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        statified.songArtistView = view?.findViewById(R.id.songArtist)
        statified.songTitleView = view?.findViewById(R.id.songTitle)
        statified.glView = view?.findViewById(R.id.visualizer_view)
        statified.fab = view?.findViewById(R.id.favouriteIcon)
        statified.fab?.alpha = 0.8f
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statified.audioVisualization = statified.glView as AudioVisualization
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        statified.myActivity = context as Activity
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        statified.audioVisualization?.onResume()
        statified.mSensorManager?.registerListener(statified.mSensorListener,
                statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        statified.audioVisualization?.onPause()
        statified.mSensorManager?.unregisterListener(statified.mSensorListener)
    }

    override fun onDestroyView() {
        statified.audioVisualization?.release()
        super.onDestroyView()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statified.mSensorManager = statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerateCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.songs_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        statified.favouriteContent = EchoDatabase(statified.myActivity)
        statified.currentSongHelper = CurrentSongHelper()
        statified.currentSongHelper?.isPlaying = true
        statified.currentSongHelper?.isShuffle = false
        statified.currentSongHelper?.isLoop = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")!!.toLong()
            statified.currentPosition = arguments!!.getInt("songPosition")
            statified.fetchSongs = arguments?.getParcelableArrayList("songData")

            statified.currentSongHelper?.songPath = path
            statified.currentSongHelper?.songArtist = _songArtist
            statified.currentSongHelper?.songTitle = _songTitle
            statified.currentSongHelper?.songId = songId
            statified.currentSongHelper?.currentPosition = statified.currentPosition

            staticated.updateTextViews(statified.currentSongHelper?.songTitle as String, statified.currentSongHelper?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments!!.get("FavBottomBar") as? String
        if (fromFavBottomBar != null) {
            statified.mediaplayer = favouriteFragment.Statified.mediaPlayer
        } else {
            statified.mediaplayer = MediaPlayer()
            statified.mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                statified.mediaplayer?.setDataSource(statified.myActivity, Uri.parse(path))
                statified.mediaplayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            statified.mediaplayer?.start()
        }
        staticated.processInformation(statified.mediaplayer as MediaPlayer)
        if (statified.currentSongHelper?.isPlaying as Boolean) {
            statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        statified.mediaplayer?.setOnCompletionListener {
            staticated.onSongComplete()
        }
        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(statified.myActivity as Context, 0)
        statified.audioVisualization?.linkTo(visualizationHandler)

        var prefsforshuffle = statified.myActivity?.getSharedPreferences(staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsforshuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            statified.currentSongHelper?.isShuffle = true
            statified.currentSongHelper?.isLoop = false
            statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            statified.currentSongHelper?.isShuffle = false
            statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

        }
        var prefsforLoop = statified.myActivity?.getSharedPreferences(staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsforLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            statified.currentSongHelper?.isShuffle = false
            statified.currentSongHelper?.isLoop = true
            statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
            statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        } else {
            statified.currentSongHelper?.isLoop = false
            statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        }
        if (statified.favouriteContent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as
                        Boolean) {
            statified.fab?.setBackgroundResource(R.drawable.favorite_on)
        } else {
            statified.fab?.setBackgroundResource(R.drawable.favorite_off)
        }

    }

    fun clickHandler() {
        statified.fab?.setOnClickListener({
            if ((statified.favouriteContent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
                statified.fab?.setBackgroundResource(R.drawable.favorite_off)
                statified.favouriteContent?.deleteFavourite(statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(statified.myActivity, "Remove From Favourites", Toast.LENGTH_SHORT).show()
            } else {
                statified.fab?.setBackgroundResource(R.drawable.favorite_on)
                statified.favouriteContent?.storeAsFavourite(statified.currentSongHelper?.songId?.toInt(), statified.currentSongHelper?.songArtist, statified.currentSongHelper?.songTitle, statified.currentSongHelper?.songPath)
                Toast.makeText(statified.myActivity, "Added To Favourites", Toast.LENGTH_SHORT).show()
            }
        })
        statified.shuffleImageButton?.setOnClickListener({
            var editorShuffle = statified.myActivity?.getSharedPreferences(staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = statified.myActivity?.getSharedPreferences(staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (statified.currentSongHelper?.isShuffle as Boolean) {
                statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                statified.currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                statified.currentSongHelper?.isShuffle = true
                statified.currentSongHelper?.isLoop = false
                statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        statified.nextImageButton?.setOnClickListener({
            statified.currentSongHelper?.isPlaying = true
            statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (statified.currentSongHelper?.isShuffle as Boolean) {
                staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                staticated.playNext("PlayNextNormal")
            }
        })
        statified.previousImageButton?.setOnClickListener({
            statified.currentSongHelper?.isPlaying = true
            if (statified.currentSongHelper?.isLoop as Boolean) {
                statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        statified.loopImageButton?.setOnClickListener({
            var editorShuffle = statified.myActivity?.getSharedPreferences(staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = statified.myActivity?.getSharedPreferences(staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (statified.currentSongHelper?.isLoop as Boolean) {
                statified.currentSongHelper?.isLoop = false
                statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                statified.currentSongHelper?.isLoop = true
                statified.currentSongHelper?.isShuffle = false
                statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })
        statified.playpauseImageButton?.setOnClickListener({
            if (statified.mediaplayer?.isPlaying as Boolean) {
                statified.mediaplayer?.pause()
                statified.currentSongHelper?.isPlaying = false
                statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                statified.mediaplayer?.start()
                statified.currentSongHelper?.isPlaying = true
                statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })

    }


    fun playPrevious() {

        statified.currentPosition = statified.currentPosition - 1

        if (statified.currentPosition == -1) {
            statified.currentPosition = 0
        }
        if (statified.currentSongHelper?.isPlaying as Boolean) {
            statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        statified.currentSongHelper?.isLoop = false


        var nextSong = statified.fetchSongs?.get(statified.currentPosition)
        statified.currentSongHelper?.songPath = nextSong?.songData
        statified.currentSongHelper?.songTitle = nextSong?.songTitle
        statified.currentSongHelper?.songArtist = nextSong?.artist
        statified.currentSongHelper?.songId = nextSong?.songID as Long

        staticated.updateTextViews(statified.currentSongHelper?.songTitle as String, statified.currentSongHelper?.songArtist as String)
        statified.mediaplayer?.reset()
        try {
            statified.mediaplayer?.setDataSource(statified.myActivity, Uri.parse(statified.currentSongHelper?.songPath))
            statified.mediaplayer?.prepare()
            statified.mediaplayer?.start()
            staticated.processInformation(statified.mediaplayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if ((statified.favouriteContent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
            statified.fab?.setBackgroundResource(R.drawable.favorite_on)
        } else {
            statified.fab?.setBackgroundResource(R.drawable.favorite_off)
        }
    }

    fun bindShakeListener() {
        statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent) {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]

                mAccelerationLast = mAccelerateCurrent
                mAccelerateCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                val delta = mAccelerateCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta

                if (mAcceleration > 12) {
                    val prefs = statified.myActivity?.getSharedPreferences(statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        staticated.playNext("PlayNextNormal")
                    }


                }


            }

        }
    }


}

