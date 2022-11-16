package pt.isel.pdm.chess4android.game

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.ChessPiece
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.views.Tile

class GameActivity: AppCompatActivity()  {

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: GameActivityViewModel by viewModels()

    private var prevPiece: ChessPiece? = null

    private var nplays: Int = 0

    private var currPlayer: Army = Army.WHITE

    private var draw: Boolean = false

    private var quit: Boolean = false

    private var win: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        draw = false
        quit = false
        win = false

        binding.Draw.setOnClickListener {
            val mAlertDialog = AlertDialog.Builder(this@GameActivity)
            mAlertDialog.setTitle(this.getString(R.string.game_drawed_title))
            mAlertDialog.setMessage(this.getString(R.string.game_drawed_message))
            mAlertDialog.setPositiveButton(this.getString(R.string.yes_button)) { dialog, id ->
                Toast.makeText(this@GameActivity, this.getString(R.string.game_drawed), Toast.LENGTH_SHORT).show()
                viewModel.draw()
            }
            mAlertDialog.setNegativeButton(this.getString(R.string.no_button)) { dialog, id ->
                Toast.makeText(this@GameActivity, this.getString(R.string.game_operation_cancelled), Toast.LENGTH_SHORT).show()
            }
            mAlertDialog.show()
        }

        binding.quit.setOnClickListener {
            val mAlertDialog = AlertDialog.Builder(this@GameActivity)
            mAlertDialog.setTitle(this.getString(R.string.game_quit_title))
            mAlertDialog.setMessage(this.getString(R.string.game_quit_message))
            mAlertDialog.setPositiveButton(this.getString(R.string.yes_button)) { dialog, id ->
                Toast.makeText(this@GameActivity, this.getString(R.string.game_quit), Toast.LENGTH_SHORT).show()
                viewModel.quit()
            }
            mAlertDialog.setNegativeButton(this.getString(R.string.no_button)) { dialog, id ->
                Toast.makeText(this@GameActivity, this.getString(R.string.game_operation_cancelled), Toast.LENGTH_SHORT).show()
            }
            mAlertDialog.show()
        }

        if (savedInstanceState == null) {
            viewModel.createBoard()
        }

        viewModel.win.observe(this) {
            win = true
            if (it == Army.WHITE) {
                Toast.makeText(this@GameActivity, this.getString(R.string.whitePLayer), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@GameActivity, this.getString(R.string.blackPlayer), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.draw.observe(this) {
            draw = true
        }
        viewModel.quit.observe(this) {
            quit = true
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

        viewModel.nplays.observe(this) {
            nplays = it
            currPlayer = if (nplays % 2 == 0)
                Army.WHITE
            else
                Army.BLACK
        }

        binding.boardView.onTileClickedListener = { tile: Tile, row: Int, column: Int ->
                if (draw || quit || win) {
                    Toast.makeText(this@GameActivity, this.getString(R.string.game_ended), Toast.LENGTH_SHORT).show()

                } else {
                    if (prevPiece == null) {
                        if (tile.piece != null && tile.piece!!.first == currPlayer)
                            viewModel.setCurrPiece(
                                ChessPiece(
                                    column,
                                    row,
                                    tile.piece!!.first,
                                    tile.piece!!.second
                                )
                            )
                    } else if (prevPiece!!.col != column || prevPiece!!.row != row) {
                        viewModel.move(
                            Pair(column, row),
                            Pair(prevPiece!!.col, prevPiece!!.row),
                            nplays
                        )
                        viewModel.setCurrPiece(null)

                    }
                }
        }
    }
}