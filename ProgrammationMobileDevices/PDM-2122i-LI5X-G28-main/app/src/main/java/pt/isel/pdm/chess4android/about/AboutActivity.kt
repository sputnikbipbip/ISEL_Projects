package pt.isel.pdm.chess4android.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.R

private const val LichessOrgURL = "https://lichess.org/api#tag/Puzzles"

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<ImageView>(R.id.lichess_icon).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(LichessOrgURL)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }
}