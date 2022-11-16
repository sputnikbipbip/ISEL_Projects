package pt.isel.pdm.chess4android.history

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.PuzzleInfoDTO
import pt.isel.pdm.chess4android.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implementation of the ViewHolder pattern. Its purpose is to eliminate the need for
 * executing findViewById each time a reference to a view's child is required.
 */
class HistoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val date = itemView.findViewById<TextView>(R.id.date)
    private val isComplete = itemView.findViewById<TextView>(R.id.isComplete)

    /**
     * Binds this view holder to the given game item
     */
    fun bindTo(puzzleInfoOfDayDTO: PuzzleInfoDTO, onItemCLick: () -> Unit) {

        date.text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(puzzleInfoOfDayDTO.date)
        isComplete.text = if (puzzleInfoOfDayDTO.isFinished) "Puzzle Completed" else "Puzzle Incompleted"
        itemView.setOnClickListener {
            itemView.isClickable = false
            startAnimation {
                onItemCLick()
                itemView.isClickable = true
            }
        }
    }

    /**
     * Starts the item selection animation and calls [onAnimationEnd] once the animation ends
     */
    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(itemView.context, R.color.list_item_background),
            ContextCompat.getColor(itemView.context, R.color.list_item_background_selected),
            ContextCompat.getColor(itemView.context, R.color.list_item_background)
        )

        animation.addUpdateListener { animator ->
            val background = itemView.background as GradientDrawable
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.doOnEnd { onAnimationEnd() }

        animation.start()
    }
}

/**
 * Adapts items in a data set to RecycleView entries
 */
class HistoryAdapter(
    private val dataSource: List<PuzzleInfoDTO>,
    private val onItemCLick: (PuzzleInfoDTO) -> Unit
): RecyclerView.Adapter<HistoryItemViewHolder>() {

    /**
     * Factory method of view holders (and its associated views)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_view, parent, false)
        return HistoryItemViewHolder(view)
    }

    /**
     * Associates (binds) the view associated to [viewHolder] to the item at [position] of the
     * data set to be adapted.
     */
    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bindTo(dataSource[position]) {
            onItemCLick(dataSource[position])
        }
    }

    /**
     * The size of the data set
     */
    override fun getItemCount() = dataSource.size
}