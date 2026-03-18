package io.github.chsbuffer.revancedxposed

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private val shizukuRequestCode = 1001

    private val shizukuPermissionListener = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            if (requestCode == shizukuRequestCode) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@MainActivity, "Shizuku permission granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Shizuku permission ditolak!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 64, 64, 64)
        }

        val title = TextView(this).apply {
            text = "ReVanced Xposed Spotify"
            textSize = 22f
            setPadding(0, 0, 0, 16)
        }

        val statusText = TextView(this).apply {
            text = getStatus()
            textSize = 14f
            setPadding(0, 0, 0, 24)
        }

        val btnRefresh = Button(this).apply {
            text = "Refresh Status"
            setOnClickListener {
                statusText.text = getStatus()
            }
        }

        val btnShizuku = Button(this).apply {
            text = "Request Shizuku Permission"
            setOnClickListener {
                requestShizuku()
            }
        }

        layout.addView(title)
        layout.addView(statusText)
        layout.addView(btnRefresh)
        layout.addView(btnShizuku)

        setContentView(layout)
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
    }

    private fun getStatus(): String {
        return buildString {
            appendLine("=== Status ===\n")

            // Xposed status
            val xposedActive = try {
                Class.forName("de.robv.android.xposed.XposedBridge")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            appendLine("Xposed aktif: ${if (xposedActive) "✓ Ya" else "✗ Tidak"}")

            // Shizuku status
            val shizukuRunning = try {
                Shizuku.pingBinder()
            } catch (e: Exception) {
                false
            }
            appendLine("Shizuku berjalan: ${if (shizukuRunning) "✓ Ya" else "✗ Tidak"}")

            if (shizukuRunning) {
                val granted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                appendLine("Shizuku permission: ${if (granted) "✓ Granted" else "✗ Belum di-grant"}")
            }

            appendLine("\nModule ini bekerja via LSPosed/Xposed.")
            appendLine("Shizuku digunakan untuk fitur tambahan.")
        }
    }

    private fun requestShizuku() {
        try {
            if (!Shizuku.pingBinder()) {
                Toast.makeText(this, "Shizuku tidak berjalan! Install & jalankan Shizuku dulu.", Toast.LENGTH_LONG).show()
                return
            }
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Shizuku sudah granted!", Toast.LENGTH_SHORT).show()
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                Toast.makeText(this, "Permission ditolak permanen. Reset di app Shizuku.", Toast.LENGTH_LONG).show()
            } else {
                Shizuku.requestPermission(shizukuRequestCode)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
