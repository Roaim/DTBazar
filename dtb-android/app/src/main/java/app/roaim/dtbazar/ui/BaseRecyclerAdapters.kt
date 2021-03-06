package app.roaim.dtbazar.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.roaim.dtbazar.model.ListItem

abstract class BasePagedListAdapter<T : ListItem, B : ViewDataBinding>(
    private val bindingComponent: DataBindingComponent? = null
) : PagedListAdapter<T, BaseListHolder<B>>(DiffItemCallback<T>()) {

    private var itemClickListener: ListItemClickListener<T>? = null

    fun setItemClickListener(itemClickListener: ListItemClickListener<T>) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListHolder<B> =
        BaseListHolder(
            onCreateBinding(
                bindingComponent,
                LayoutInflater.from(parent.context),
                parent,
                viewType
            )
        ).attachClickListener(itemClickListener, this::getItem)

    override fun onBindViewHolder(holder: BaseListHolder<B>, position: Int) {
        bind(holder.binding, getItem(position))
        holder.binding.executePendingBindings()
    }

    abstract fun onCreateBinding(
        bindingComponent: DataBindingComponent?,
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): B

    abstract fun bind(binding: B, item: T?)
}

abstract class BaseListAdapter<T : ListItem, B : ViewDataBinding>(
    private val bindingComponent: DataBindingComponent? = null
) : ListAdapter<T, BaseListHolder<B>>(DiffItemCallback<T>()) {

    private var itemClickListener: ListItemClickListener<T>? = null

    fun setItemClickListener(itemClickListener: ListItemClickListener<T>) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListHolder<B> =
        BaseListHolder(
            onCreateBinding(
                bindingComponent,
                LayoutInflater.from(parent.context),
                parent,
                viewType
            )
        ).attachClickListener(itemClickListener, this::getItem)

    abstract fun onCreateBinding(
        bindingComponent: DataBindingComponent?,
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): B

    override fun onBindViewHolder(holder: BaseListHolder<B>, position: Int) {
        bind(holder.binding, getItem(position))
        holder.binding.executePendingBindings()
    }

    abstract fun bind(binding: B, item: T)

}

class BaseListHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)

@Suppress("ReplaceCallWithBinaryOperator")
class DiffItemCallback<T : ListItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.getItemId() == newItem.getItemId()

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem.equals(newItem)
}

fun <T, B : ViewDataBinding> BaseListHolder<B>.attachClickListener(
    itemClickListener: ListItemClickListener<T>?,
    getItem: (Int) -> T?
) = apply {
    itemClickListener?.apply {
        itemView.setOnClickListener {
            onItemClick(getItem(adapterPosition), it, false)
        }
        itemView.setOnLongClickListener {
            onItemClick(getItem(adapterPosition), it, true)
            true
        }
    }
}