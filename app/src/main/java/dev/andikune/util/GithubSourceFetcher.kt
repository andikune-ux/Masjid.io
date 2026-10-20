package dev.andikune.masjidio.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Ambil SEMUA file source code dari repo GitHub publik via API.
 */
object GithubSourceFetcher {

    private const val USER = "andikune-ux"
    private const val REPO = "Masjid.io"
    private const val BRANCH = "main"
    private const val TIMEOUT_MS = 15000

    private val ALLOWED_EXT = setOf(
        "kt", "java", "xml", "gradle", "kts",
        "toml", "yml", "yaml", "properties", "pro"
    )

    suspend fun listFiles(): List<String> = withContext(Dispatchers.IO) {
        val api = "https://api.github.com/repos/$USER/$REPO/git/trees/$BRANCH?recursive=1"
        val json = httpGet(api) ?: return@withContext emptyList()
        runCatching {
            val obj = JSONObject(json)
            val arr = obj.getJSONArray("tree")
            val result = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                if (item.getString("type") != "blob") continue
                val path = item.getString("path")
                if (path.contains("/build/") || path.startsWith("build/")) continue
                val ext = path.substringAfterLast('.', "").lowercase()
                if (ext in ALLOWED_EXT) result.add(path)
            }
            result.sorted()
        }.getOrDefault(emptyList())
    }

    suspend fun fetchFile(path: String): String? = withContext(Dispatchers.IO) {
        val raw = "https://raw.githubusercontent.com/$USER/$REPO/$BRANCH/$path"
        httpGet(raw)
    }

    suspend fun fetchAllFiles(
        onProgress: (current: Int, total: Int, path: String) -> Unit = { _, _, _ -> }
    ): Map<String, String> = withContext(Dispatchers.IO) {
        val files = listFiles()
        val result = LinkedHashMap<String, String>()
        files.forEachIndexed { idx, path ->
            onProgress(idx + 1, files.size, path)
            val content = fetchFile(path)
            if (content != null) result[path] = content
        }
        result
    }

    private fun httpGet(urlStr: String): String? {
        return runCatching {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.setRequestProperty("User-Agent", "Masjid.io-Backup")
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
            try {
                if (conn.responseCode !in 200..299) return null
                conn.inputStream.bufferedReader().use { it.readText() }
            } finally {
                conn.disconnect()
            }
        }.getOrNull()
    }
}
