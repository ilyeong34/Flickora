package com.ilyeong.movieverse.core.ui.common.extension

import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun SearchView.getQueryFlow() = callbackFlow<String> {
    setOnQueryTextListener(object : OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            trySend(newText.toString())
            return true
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            trySend(query.toString())
            return true
        }
    })

    awaitClose {
        setOnQueryTextListener(null)
    }
}