package co.harismiftahulhudha.alodoktertest.mvvm.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.ItemContentImageBinding
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentImageModel
import com.bumptech.glide.Glide

class ContentImageAdapter: ListAdapter<ContentImageModel, ContentImageAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentImageAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemContentImageBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ContentImageAdapter.ViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }

    inner class ViewHolder(private val binding: ItemContentImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ContentImageModel) {
            Glide.with(binding.root.context)
                .load(model.path)
                .error(ContextCompat.getDrawable(binding.root.context, R.drawable.gallery_kosong))
                .into(binding.imgContent)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ContentImageModel>() {
        override fun areItemsTheSame(oldItem: ContentImageModel, newItem: ContentImageModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ContentImageModel, newItem: ContentImageModel) =
            oldItem == newItem
    }
}