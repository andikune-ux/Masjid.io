package com.example.util

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateManager {

    private const val TAG = "UpdateManager"
    private const val GITHUB_API = "https://api.github.com/repos/andikune-ux/Masjid.io/releases/latest"

    data class UpdateInfo(
        val available: Boolean,
        val currentVersion: String,
        val latestVersion: String,
        val downloadUrl: String?,
        val releaseNotes: String?,
        val releaseDate: String?,
        val isForceUpdate: Boolean
    )

    /**
     * Cek update terbaru dari GitHub Releases.
     * Panggil via Coroutine (suspend).
     */
    suspend fun checkForUpdate(): UpdateInfo = withContext(Dispatchers.IO) {
        val currentVersion = try {
            BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            "V0.0.0"
        }

        try {
            val connection = URL(GITHUB_API).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("User-Agent", "MasjidIO-App")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode != 200) {
                Log.w(TAG, "GitHub API response: ${connection.responseCode}")
                return@withContext noUpdate(currentVersion)
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)

            val latestVersion = json.optString("tag_name", "").ifBlank {
                return@withContext noUpdate(currentVersion)
            }

            val releaseNotes = json.optString("body", "")
            val releaseDate = json.optString("published_at", "")

            // Cari APK di assets
            val downloadUrl = findApkAssetUrl(json.optJSONArray("assets"))

            val isNewer = compareVersions(latestVersion, currentVersion) > 0

            UpdateInfo(
                available = isNewer,
                currentVersion = currentVersion,
                latestVersion = latestVersion,
                downloadUrl = downloadUrl,
                releaseNotes = releaseNotes,
                releaseDate = releaseDate,
                isForceUpdate = isNewer
            )
        } catch (e: Exception) {
            Log.e(TAG, "Cek update gagal: ${e.message}")
            noUpdate(currentVersion)
        }
    }

    private fun findApkAssetUrl(assets: JSONArray?): String? {
        if (assets == null) return null
        for (i in 0 until assets.length()) {
            val asset = assets.optJSONObject(i) ?: continue
            val name = asset.optString("name", "")
            if (name.endsWith(".apk", ignoreCase = true)) {
                return asset.optString("browser_download_url", null)
            }
        }
        return null
    }

    /**
     * Bandingkan versi format V1.28.1.
     * Return: >0 jika v1 > v2, 0 jika sama, <0 jika v1 < v2
     */
    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = parseVersion(v1)
        val parts2 = parseVersion(v2)
        val maxLen = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLen) {
            val p1 = parts1.getOrElse(i) { 0 }
            val p2 = parts2.getOrElse(i) { 0 }
            if (p1 != p2) return p1 - p2
        }
        return 0
    }

    private fun parseVersion(version: String): List<Int> {
        return version
            .removePrefix("V")
            .removePrefix("v")
            .split(".")
            .mapNotNull { it.trim().toIntOrNull() }
    }

    private fun noUpdate(currentVersion: String) = UpdateInfo(
        available = false,
        currentVersion = currentVersion,
        latestVersion = currentVersion,
        downloadUrl = null,
        releaseNotes = null,
        releaseDate = null,
        isForceUpdate = false
    )
}
