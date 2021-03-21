package co.harismiftahulhudha.alodoktertest.mvvm.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.ItemContentBinding
import co.harismiftahulhudha.alodoktertest.mvvm.joinmodels.ContentImageJoinModel
import com.bumptech.glide.Glide

private const val TAG = "ContentAdapter"
class ContentAdapter: ListAdapter<ContentImageJoinModel, ContentAdapter.ViewHolder>(DiffCallback()) {

    private lateinit var listener: OnListener

    fun setListener(listener: OnListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemContentBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ContentAdapter.ViewHolder, position: Int) {
        val model = getItem(position)
        Log.d(TAG, "onBindViewHolder: ${position} ${model.content}")
        Log.d(TAG, "onBindViewHolder: ${position} ${model.images}")
        holder.bind(model)
    }

    inner class ViewHolder(private val binding: ItemContentBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                itemView.setOnClickListener {
                    if (adapterPosition != -1) {
                        listener.onClickListener(getItem(adapterPosition), adapterPosition)
                    }
                }
                itemView.setOnLongClickListener {
                    if (adapterPosition != -1) {
                        listener.onLongClickListener(getItem(adapterPosition), adapterPosition)
                        true
                    } else {
                        false
                    }
                }
            }
        }

        fun bind(contentImageJoin: ContentImageJoinModel) {
            binding.apply {
                Glide.with(binding.root.context)
                    .load(contentImageJoin.images[0].path)
                    .error(ContextCompat.getDrawable(binding.root.context, R.drawable.gallery_kosong))
                    .into(imgContent)
                txtDescription.text = contentImageJoin.content.description
            }
        }
    }

    interface OnListener {
        fun onClickListener(contentImageJoin: ContentImageJoinModel, position: Int)
        fun onLongClickListener(contentImageJoin: ContentImageJoinModel, position: Int)
    }

    class DiffCallback : DiffUtil.ItemCallback<ContentImageJoinModel>() {
        override fun areItemsTheSame(oldItem: ContentImageJoinModel, newItem: ContentImageJoinModel) =
            oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(oldItem: ContentImageJoinModel, newItem: ContentImageJoinModel) =
            oldItem.content == newItem.content
    }
}