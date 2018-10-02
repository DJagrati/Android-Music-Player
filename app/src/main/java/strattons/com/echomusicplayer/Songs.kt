package strattons.com.echomusicplayer

import android.os.Parcel
import android.os.Parcelable
import java.util.Comparator

class Songs(var songID: Long, var songTitle: String, var artist: String, var songData: String, var date: Long) : Parcelable {
    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    object statified {
        var nameComparator: Comparator<Songs> = kotlin.Comparator<Songs> { song1, songs2 ->
            val songOne = song1.songTitle.toUpperCase()
            val songTwo = songs2.songTitle.toUpperCase()
            songOne.compareTo(songTwo)
        }
        var dateComparator: Comparator<Songs> = kotlin.Comparator<Songs> { song1, songs2 ->
            val songOne = song1.date.toDouble()
            val songTwo = songs2.date.toDouble()
            songTwo.compareTo(songOne)
        }


    }
}