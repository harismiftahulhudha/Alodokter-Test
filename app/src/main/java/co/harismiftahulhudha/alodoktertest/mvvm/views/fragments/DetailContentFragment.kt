package co.harismiftahulhudha.alodoktertest.mvvm.views.fragments

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.customcomponents.GravitySnapCustomComponent
import co.harismiftahulhudha.alodoktertest.databinding.FragmentDetailContentBinding
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.ContentViewModel
import co.harismiftahulhudha.alodoktertest.mvvm.views.adapters.ContentImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailContentFragment : Fragment(R.layout.fragment_detail_content) {
    private lateinit var binding: FragmentDetailContentBinding
    private val viewModel: ContentViewModel by viewModels()
    private val imageAdapter: ContentImageAdapter = ContentImageAdapter()
    private lateinit var linearSnapHelper: LinearSnapHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
    }

    private fun initComponents(view: View) {
        binding = FragmentDetailContentBinding.bind(view)
        binding.apply {
            rvImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvImages.setHasFixedSize(true)
            rvImages.setItemViewCacheSize(20)
            rvImages.adapter = imageAdapter
            linearSnapHelper = GravitySnapCustomComponent(Gravity.START)
            linearSnapHelper.attachToRecyclerView(rvImages)
            rvImages.setRecycledViewPool(RecyclerView.RecycledViewPool())
            spiImages.attachToRecyclerView(rvImages)
            spiImages.dotColor = ContextCompat.getColor(requireContext(), R.color.grey_300)
            spiImages.selectedDotColor = ContextCompat.getColor(requireContext(), R.color.primary)
            imageAdapter.submitList(viewModel.detailModel?.images)
            txtDescription.text = viewModel.detailModel?.content?.description
        }
    }
}