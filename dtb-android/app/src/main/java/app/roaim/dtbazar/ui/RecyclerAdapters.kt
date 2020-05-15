package app.roaim.dtbazar.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerBindingAdapter<T, VH : RecyclerBindingViewHolder<T, B>, B : ViewDataBinding> :
    BaseClickableRecyclerAdapter<T, VH>() {
    final override fun onCreateViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): VH = onCreateViewHolder(
        onGetViewBinding(layoutInflater, parent),
        viewType
    )

    abstract fun onGetViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup): B
    abstract fun onCreateViewHolder(binding: B, viewType: Int): VH
}

abstract class RecyclerBindingViewHolder<T, B : ViewDataBinding>(val binding: B) :
    BaseClickableViewHolder<T>(binding.root)

abstract class RecyclerSimpleAdapter<T, VH : RecyclerSimpleViewHolder<T>> :
    BaseClickableRecyclerAdapter<T, VH>() {
    final override fun onCreateViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): VH = onCreateViewHolder(
        layoutInflater.inflate(getLayoutResId(), parent, false),
        viewType
    )

    abstract fun getLayoutResId(): Int
    abstract fun onCreateViewHolder(view: View, viewType: Int): VH
}

abstract class RecyclerSimpleViewHolder<T>(view: View) : BaseClickableViewHolder<T>(view)

abstract class BaseClickableRecyclerAdapter<T, VH : BaseClickableViewHolder<T>> :
    BaseRecyclerAdapter<T, VH>() {
    var itemClickListener: ((item: T?, itemView: View, isLongClick: Boolean) -> Unit)? = null

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        onCreateViewHolder(
            LayoutInflater.from(parent.context), parent, viewType
        ).apply {
            setClickListener(itemClickListener) {
                getItem(it)
            }
        }

    abstract fun onCreateViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): VH
}

abstract class BaseClickableViewHolder<T>(view: View) : BaseViewHolder<T>(view) {
    fun setClickListener(
        clickListener: ((item: T?, itemView: View, isLongClick: Boolean) -> Unit)? = null,
        item: (Int) -> T?
    ) {
        clickListener?.also { block ->
            // Attach click listener only if there is a consumer
            itemView.setOnClickListener {
                block(item(adapterPosition), it, false)
            }
            itemView.setOnLongClickListener {
                block(item(adapterPosition), it, true)
                true
            }
        }
    }
}

abstract class BaseRecyclerAdapter<T, VH : BaseViewHolder<T>> : RecyclerView.Adapter<VH>() {
    private val mList = mutableListOf<T>()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getItem(position: Int) = mList[position]

    fun add(item: T, position: Int) {
        mList.add(position, item)
        notifyItemInserted(position)
    }

    fun addAll(items: List<T>) {
        mList.addAll(items)
        notifyDataSetChanged()
    }

    fun reload(items: List<T>) {
        mList.clear()
        addAll(items)
    }

    fun remove(position: Int): T? {
        return position.takeIf { it in 0 until itemCount }?.let {
            mList.removeAt(it).apply {
                notifyItemRemoved(it)
            }
        }
    }

    fun replace(item: T, position: Int) {
        mList[position] = item
        notifyItemChanged(position)
    }

}

abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T?)
}