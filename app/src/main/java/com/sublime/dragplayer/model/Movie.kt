package com.sublime.dragplayer.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by goonerdroid
 * on 16/1/18.
 */
class Movie(val movieName: String, val movieThumbnail: Int, val movieYear: String
            , val movieGenre: String, val movieTrailer: String) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(movieName)
        parcel.writeInt(movieThumbnail)
        parcel.writeString(movieYear)
        parcel.writeString(movieGenre)
        parcel.writeString(movieTrailer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField val CREATOR = object : Parcelable.Creator<Movie>{
            override fun createFromParcel(parcel: Parcel): Movie {
                return Movie(parcel)
            }

            override fun newArray(size: Int): Array<Movie?> {
                return arrayOfNulls(size)
            }
        }
    }
}