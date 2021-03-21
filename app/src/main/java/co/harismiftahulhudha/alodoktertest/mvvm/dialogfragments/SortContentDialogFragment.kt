package co.harismiftahulhudha.alodoktertest.mvvm.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.FragmentSortContentDialogBinding
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.ContentViewModel
import co.harismiftahulhudha.alodoktertest.utils.SortContents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortContentDialogFragment : DialogFragment() {
    private val viewModel: ContentViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val binding = FragmentSortContentDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        binding.apply {
            if (viewModel.isNewest) {
                rgSort.check(R.id.rbNewest)
            } else {
                rgSort.check(R.id.rbOldest)
            }
            rgSort.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId == R.id.rbNewest) {
                    viewModel.onSort(SortContents.NEWEST)
                    viewModel.updateIsContentScrollToTop(true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss()
                    }, 200)
                } else if (checkedId == R.id.rbOldest) {
                    viewModel.onSort(SortContents.OLDEST)
                    viewModel.updateIsContentScrollToTop(true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss()
                    }, 200)
                }
            }
        }

        dialog.show()
        return dialog
    }
}