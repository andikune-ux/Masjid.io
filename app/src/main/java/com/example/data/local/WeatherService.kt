package com.example.data.local

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class WeatherInfo(
    val temperature: Int = 30,
    val condition: String = "Cerah", // "Cerah", "Berawan", "Hujan", "Gerimis"
    val humidity: Int = 75,
    val windSpeedKmh: Int = 12
)

object WeatherService {

    /**
     * Fetches real-time weather using Open-Meteo open free API (No API key needed, unlimited, accurate).
     */
    suspend fun fetchWeather(latitude: Double, longitude: Double): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 4000
            conn.readTimeout = 4000

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                val current = json.getJSONObject("current_weather")
                val temp = current.getDouble("temperature").toInt()
                val weatherCode = current.getInt("weathercode")
                val windSpeed = current.optDouble("windspeed", 10.0).toInt()

                // WMO Weather interpretation codes
                // 0: Clear sky
                // 1, 2, 3: Mainly clear, partly cloudy, and overcast
                // 45, 48: Fog
                // 51, 53, 55: Drizzle
                // 61, 63, 65: Rain
                // 80, 81, 82: Rain showers
                // 95, 96, 99: Thunderstorm
                val condition = when (weatherCode) {
                    0, 1 -> "Cerah"
                    2, 3 -> "Berawan"
                    45, 48 -> "Berkabut"
                    51, 53, 55 -> "Gerimis"
                    61, 63, 65, 80, 81, 82 -> "Hujan"
                    95, 96, 99 -> "Hujan Petir"
                    else -> "Cerah"
                }

                return@withContext WeatherInfo(
                    temperature = temp,
                    condition = condition,
                    windSpeedKmh = windSpeed
                )
            }
        } catch (e: Exception) {
            Log.w("WeatherService", "Failed to fetch real-time weather: ${e.message}")
        }
        // Fallback default
        return@withContext WeatherInfo(temperature = 30, condition = "Cerah")
    }
}
