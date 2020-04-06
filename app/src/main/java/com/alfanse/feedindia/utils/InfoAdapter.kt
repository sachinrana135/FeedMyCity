package com.alfanse.feedindia.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class InfoWindowAdapter(private val myContext: AppCompatActivity) : GoogleMap.InfoWindowAdapter {
    private val view: View
    init {
        val inflater =
            myContext.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(
            com.alfanse.feedindia.R.layout.layout_user_info,
            null
        )
    }

    override fun getInfoContents(marker: Marker): View {
        val title: String = marker.title
        val titleUi = view.findViewById(com.alfanse.feedindia.R.id.title) as TextView
        titleUi.text = "Name- $title"
        val snippet: String = marker.snippet
        val snippetUi = view.findViewById(com.alfanse.feedindia.R.id.phone) as TextView
        snippetUi.text = "Phone- $snippet"
        return view
    }

    override fun getInfoWindow(marker: Marker): View {
        return view
    }
}