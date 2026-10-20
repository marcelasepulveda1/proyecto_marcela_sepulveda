package com.example.panalsuite.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.panalsuite.data.remote.dto.TicketDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.ticketDataStore by preferencesDataStore(name = "tickets")

/** Keeps a copy of the tickets on the phone (as JSON in DataStore) so the app also works offline. */
class TicketLocalDataSource(
    private val context: Context,
    private val gson: Gson = Gson()
) {

    private val ticketsKey = stringPreferencesKey("tickets_json")
    private val listType = object : TypeToken<List<TicketDto>>() {}.type

    val tickets: Flow<List<TicketDto>> = context.ticketDataStore.data.map { preferences ->
        decode(preferences[ticketsKey])
    }

    suspend fun current(): List<TicketDto> = tickets.first()

    suspend fun replaceAll(tickets: List<TicketDto>) {
        context.ticketDataStore.edit { preferences ->
            preferences[ticketsKey] = gson.toJson(tickets)
        }
    }

    /** Reads, transforms and saves the list in a single atomic DataStore transaction. */
    suspend fun update(transform: (List<TicketDto>) -> List<TicketDto>) {
        context.ticketDataStore.edit { preferences ->
            val updated = transform(decode(preferences[ticketsKey]))
            preferences[ticketsKey] = gson.toJson(updated)
        }
    }

    private fun decode(json: String?): List<TicketDto> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching { gson.fromJson<List<TicketDto>>(json, listType) }
            .getOrNull()
            .orEmpty()
    }
}
