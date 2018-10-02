package strattons.com.echomusicplayer.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import strattons.com.echomusicplayer.R
import strattons.com.echomusicplayer.activities.MainActivity
import strattons.com.echomusicplayer.fragments.SongPlayingFragment

class captureBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.statified.tracknotificationManager?.cancel(1978)
            }catch (e:Exception){
                e.printStackTrace()
            }

            try {

                if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.statified.mediaplayer?.pause()
                    SongPlayingFragment.statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val vm: TelephonyManager = p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (vm?.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try {
                        MainActivity.statified.tracknotificationManager?.cancel(1978)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                    try {
                        if (SongPlayingFragment.statified.mediaplayer?.isPlaying as Boolean) {
                            SongPlayingFragment.statified.mediaplayer?.pause()
                            SongPlayingFragment.statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }else->{

            }

            }
        }
    }

}
