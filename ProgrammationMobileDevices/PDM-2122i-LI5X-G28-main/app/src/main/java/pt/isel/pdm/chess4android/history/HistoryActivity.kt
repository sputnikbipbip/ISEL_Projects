package pt.isel.pdm.chess4android.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.puzzle.PuzzleActivity
import pt.isel.pdm.chess4android.resolved.ResolvedActivity
import pt.isel.pdm.chess4android.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater)}
    private val viewModel by viewModels<HistoryActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.puzzleList.layoutManager = LinearLayoutManager(this)

        // Get the list of puzzles, if we haven't fetched it yet
        (viewModel.history ?: viewModel.loadHistory()).observe(this) {
            binding.puzzleList.adapter = HistoryAdapter(it) { puzzleDto ->
                var intent : Intent? = if (puzzleDto.isFinished) {
                    Intent(this, ResolvedActivity::class.java)
                } else {
                    Intent(this, PuzzleActivity::class.java)
                }
                intent?.putExtra("puzzleInfo", puzzleDto.puzzle)
                startActivity(intent)
            }
        }
    }
}