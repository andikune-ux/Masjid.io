package com.example.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BuildHistoryFetcher {

    private const val TAG = "BuildHistoryFetcher"
    private const val REPO_API = "https://api.github.com/repos/andikune-ux/Masjid.io/actions/runs?per_page=10"

    data class BuildRun(
        val runNumber: Int,
        val name: String,
        val status: String,         // completed / in_progress / queued
        val conclusion: String,     // success / failure / cancelled / null
        val createdAt: String,
        val updatedAt: String,
        val headBranch: String,
        val headCommitMessage: String,
        val htmlUrl: String,
        val runId: Long
    )

    /**
     * Fetch 10 build terakhir dari GitHub Actions.
     * Tanpa token — akses public repo.
     * Rate limit: 60 request/jam per IP (cukup untuk kebutuhan app).
     */
    suspend fun fetchRecentBuilds(): List<BuildRun> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(REPO_API).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("User-Agent", "MasjidIO-App")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            if (connection.responseCode != 200) {
                Log.w(TAG, "GitHub API response: ${connection.responseCode}")
                return@withContext emptyList()
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = JSONObject(response)
            val runsArray = json.optJSONArray("workflow_runs") ?: return@withContext emptyList()

            val result = mutableListOf<BuildRun>()
            for (i in 0 until runsArray.length()) {
                val run = runsArray.optJSONObject(i) ?: continue

                val headCommit = run.optJSONObject("head_commit")
                val commitMessage = headCommit?.optString("message") ?: ""

                result.add(
                    BuildRun(
                        runNumber = run.optInt("run_number", 0),
                        name = run.optString("name", "Unknown"),
                        status = run.optString("status", "unknown"),
                        conclusion = run.optString("conclusion", "null"),
                        createdAt = run.optString("created_at", ""),
                        updatedAt = run.optString("updated_at", ""),
                        headBranch = run.optString("head_branch", ""),
                        headCommitMessage = commitMessage.lines().firstOrNull() ?: "",
                        htmlUrl = run.optString("html_url", ""),
                        runId = run.optLong("id", 0)
                    )
                )
            }

            Log.d(TAG, "Fetched ${result.size} builds")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Gagal fetch build history: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Format daftar build jadi teks rapi untuk Backup Aman.
     */
    fun formatBuildsAsText(builds: List<BuildRun>): String {
        if (builds.isEmpty()) {
            return """
============================================================
RIWAYAT BUILD (GitHub Actions)
============================================================
(Tidak dapat fetch data — kemungkinan tidak ada koneksi
 internet, atau rate limit GitHub API tercapai.)
============================================================
            """.trimIndent()
        }

        val dateFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

        return buildString {
            appendLine("=" .repeat(60))
            appendLine("RIWAYAT BUILD (GitHub Actions)")
            appendLine("=".repeat(60))
            appendLine()
            appendLine("Sumber: ${REPO_API.substringBefore('?')}")
            appendLine("Total: ${builds.size} build terakhir")
            appendLine()

            builds.forEach { build ->
                val statusIcon = when (build.conclusion) {
                    "success" -> "✅ SUKSES"
                    "failure" -> "❌ GAGAL"
                    "cancelled" -> "⚪ DIBATALKAN"
                    else -> "⏳ ${build.status.uppercase()}"
                }

                val dateStr = try {
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss'Z'",
                        Locale.getDefault()
                    ).parse(build.createdAt)
                    date?.let { dateFormatter.format(it) } ?: build.createdAt
                } catch (e: Exception) {
                    build.createdAt
                }

                appendLine("#${build.runNumber}  $statusIcon")
                appendLine("   Tanggal  : $dateStr")
                appendLine("   Branch   : ${build.headBranch}")
                appendLine("   Commit   : ${build.headCommitMessage.take(80)}")
                if (build.conclusion == "failure") {
                    appendLine("   ⚠️ BUILD INI GAGAL — cek log di:")
                    appendLine("   ${build.htmlUrl}")
                }
                appendLine()
            }

            appendLine("=".repeat(60))
            appendLine()
            appendLine("CATATAN:")
            appendLine("Build yang GAGAL perlu dicek log-nya di GitHub Actions.")
            appendLine("Cari step 'Build Debug APK' → cari baris yang diawali 'e:'")
            appendLine("Lalu perbaiki file yang error.")
            appendLine("=".repeat(60))
        }
    }

    /**
     * Cek apakah ada build terakhir yang gagal.
     */
    suspend fun hasRecentFailedBuild(): Boolean {
        val builds = fetchRecentBuilds()
        return builds.firstOrNull()?.conclusion == "failure"
    }

    /**
     * Ambil build terakhir (apapun statusnya).
     */
    suspend fun getLatestBuild(): BuildRun? {
        return fetchRecentBuilds().firstOrNull()
    }
}
