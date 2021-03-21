package co.harismiftahulhudha.alodoktertest.mvvm.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.harismiftahulhudha.alodoktertest.R
import com.bumptech.glide.Glide
import id.haris.galleryapplication.CustomGalleryModel

class CustomGalleryAdapter : RecyclerView.Adapter<CustomGalleryAdapter.ViewHolder>() {
    private var models: MutableList<CustomGalleryModel>? = null
    private var onClickListener: OnClickListener? = null

    fun updateModel(position: Int, model: CustomGalleryModel) {
        models?.set(position, model)
        notifyItemChanged(position)
    }

    fun setModel(models: MutableList<CustomGalleryModel>) {
        this.models = models
        notifyDataSetChanged()
    }

    fun getModels() = models

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_custom_gallery, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (models != null) {
            val model = models!![position]
            val params: ConstraintLayout.LayoutParams =
                holder.image.getLayoutParams() as ConstraintLayout.LayoutParams

            if (model.select) {
                holder.icon.setVisibility(View.VISIBLE)
                holder.frame.setVisibility(View.VISIBLE)
                params.setMargins(6, 6, 6, 6)
                holder.image.setLayoutParams(params)
            } else {
                holder.icon.setVisibility(View.GONE)
                holder.frame.setVisibility(View.GONE)
                params.setMargins(0, 0, 0, 0)
                holder.image.setLayoutParams(params)
            }

            holder.overlay.setVisibility(View.GONE)
            Glide.with(holder.itemView.context).load(model.path)
                .error(ContextCompat.getDrawable(holder.itemView.context, R.drawable.gallery_kosong))
                .into(holder.image)

            if (onClickListener != null) {
                holder.itemView.setOnClickListener {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = when {
        models == null -> 0
        else -> models!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.imgFile)
        val icon = itemView.findViewById<ImageView>(R.id.imgIconSelect)
        val frame = itemView.findViewById<View>(R.id.viewFrame)
        val overlay = itemView.findViewById<View>(R.id.viewOverlay)
    }

    interface OnClickListener {
        fun onClick(position: Int, model: CustomGalleryModel)
    }
}