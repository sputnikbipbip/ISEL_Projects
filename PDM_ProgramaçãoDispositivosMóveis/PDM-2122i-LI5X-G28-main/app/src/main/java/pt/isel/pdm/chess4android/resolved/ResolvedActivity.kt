package pt.isel.pdm.chess4android.resolved

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.PuzzleInfo
import pt.isel.pdm.chess4android.databinding.ActivityResolvedBinding
import pt.isel.pdm.chess4android.puzzle.PuzzleActivity
import pt.isel.pdm.chess4android.puzzle.PuzzleActivityViewModel

class ResolvedActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityResolvedBinding.inflate(layoutInflater)
    }

    private val viewModel: PuzzleActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val i: Intent = intent
        val puzzleInfo: PuzzleInfo? = i.getParcelableExtra("puzzleInfo")

        if (savedInstanceState == null) {
            viewModel.createBoard(puzzleInfo!!.puzzle.initialPly)
            viewModel.createPuzzle(puzzleInfo.game.pgn)
        }

        viewModel.pieces.observe(this) {
            binding.boardView.resetBoard()
            it.forEach { (col, row, player, piece) ->
                binding.boardView.drawPiece(col, row, player, piece)
            }
        }

        binding.again.setOnClickListener {
            val intent = Intent(this, PuzzleActivity::class.java)
            intent.putExtra("puzzleInfo", puzzleInfo)
            startActivity(intent)
        }

        binding.solution.setOnClickListener {
            for (i in 0 until puzzleInfo?.puzzle?.solution?.size!!) {
                val prevPieceCol = viewModel.get(puzzleInfo.puzzle.solution[i][0])
                val prevPieceRow = viewModel.get(puzzleInfo.puzzle.solution[i][1])
                val currPieceCol = viewModel.get(puzzleInfo.puzzle.solution[i][2])
                val currPieceRow = viewModel.get(puzzleInfo.puzzle.solution[i][3])
                viewModel.move(
                    Pair(currPieceCol, currPieceRow),
                    Pair(prevPieceCol, prevPieceRow),
                    puzzleInfo.puzzle.solution,
                    i
                )
            }
            binding.solution.visibility = View.INVISIBLE
        }
    }
}