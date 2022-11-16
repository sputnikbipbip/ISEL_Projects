package pt.isel.pdm.chess4android.puzzle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.ChessPiece
import pt.isel.pdm.chess4android.PuzzleInfo
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityPuzzleBinding
import pt.isel.pdm.chess4android.views.Tile

class PuzzleActivity: AppCompatActivity() {

    private val binding by lazy {
        ActivityPuzzleBinding.inflate(layoutInflater)
    }

    private val viewModel: PuzzleActivityViewModel by viewModels()

    private var prevPiece: ChessPiece? = null

    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val i: Intent = intent
        val info: PuzzleInfo? = i.getParcelableExtra("puzzleInfo")
        val toastWrongPrevCol =
            Toast.makeText(this, this.getString(R.string.prev_col_wrong), Toast.LENGTH_SHORT)
        val toastWrongPrevRow =
            Toast.makeText(this, this.getString(R.string.prev_row_wrong), Toast.LENGTH_SHORT)
        val toastWrongCurrCol =
            Toast.makeText(this, this.getString(R.string.curr_col_wrong), Toast.LENGTH_SHORT)
        val toastWrongCurrRow =
            Toast.makeText(this, this.getString(R.string.curr_row_wrong), Toast.LENGTH_SHORT)
        val toastCorrect =
            Toast.makeText(this, this.getString(R.string.currect_move), Toast.LENGTH_SHORT)
        val toastComplete =
            Toast.makeText(this, this.getString(R.string.puzzle_complete), Toast.LENGTH_SHORT)
        val toastCompleted = Toast.makeText(
            this,
            this.getString(R.string.puzzle_already_completed),
            Toast.LENGTH_SHORT
        )

        if (savedInstanceState == null) {
            viewModel.createBoard(info!!.puzzle.initialPly)
            viewModel.createPuzzle(info.game.pgn)
        }

        viewModel.pieces.observe(this) {
            binding.boardView.resetBoard()
            it.forEach { (col, row, player, piece) ->
                binding.boardView.drawPiece(col, row, player, piece)
            }
        }

        viewModel.currentPiece.observe(this) {
            prevPiece = it
        }

        viewModel.index.observe(this) {
            index = it
        }

        binding.boardView.onTileClickedListener = { tile: Tile, row: Int, column: Int ->
            if (index <= info!!.puzzle.solution.size - 1) {
                if (prevPiece == null) {
                    if (tile.piece != null && tile.piece!!.first == viewModel.playerOrientation)
                        viewModel.setCurrPiece(
                            ChessPiece(
                                column,
                                row,
                                tile.piece!!.first,
                                tile.piece!!.second
                            )
                        )
                } else if (prevPiece!!.col != column || prevPiece!!.row != row) {
                    when (viewModel.move(
                        Pair(column, row),
                        Pair(prevPiece!!.col, prevPiece!!.row),
                        info.puzzle.solution,
                        index
                    )) {
                        PuzzleActivityViewModel.Solution.WRONG_PREV_COL -> toastWrongPrevCol.show()
                        PuzzleActivityViewModel.Solution.WRONG_PREV_ROW -> toastWrongPrevRow.show()
                        PuzzleActivityViewModel.Solution.WRONG_CURR_COL -> toastWrongCurrCol.show()
                        PuzzleActivityViewModel.Solution.WRONG_CURR_ROW -> toastWrongCurrRow.show()
                        PuzzleActivityViewModel.Solution.CORRECT -> {
                            binding.boardView.deletePiece(prevPiece!!.col, prevPiece!!.row)
                            if (index == info.puzzle.solution.size) {
                                viewModel.gameCompleted(info.puzzle.id)
                                toastComplete.show()
                            }
                            else {
                                toastCorrect.show()

                                val prevPieceCol = viewModel.get(info.puzzle.solution[index][0])
                                val prevPieceRow = viewModel.get(info.puzzle.solution[index][1])
                                val currPieceCol = viewModel.get(info.puzzle.solution[index][2])
                                val currPieceRow = viewModel.get(info.puzzle.solution[index][3])
                                /**Auto move*/
                                viewModel.move(
                                    Pair(currPieceCol, currPieceRow),
                                    Pair(prevPieceCol, prevPieceRow),
                                    info.puzzle.solution,
                                    index
                                )
                                binding.boardView.deletePiece(prevPieceCol, prevPieceRow)

                            }
                        }
                    }
                    viewModel.setCurrPiece(null)
                } else {
                    viewModel.setCurrPiece(null)
                }
            } else {
                toastCompleted.show()
            }
        }
    }
}