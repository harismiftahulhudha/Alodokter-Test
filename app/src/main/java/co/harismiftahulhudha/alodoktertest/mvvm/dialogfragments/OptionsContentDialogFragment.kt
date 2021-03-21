package co.harismiftahulhudha.alodoktertest.mvvm.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import co.harismiftahulhudha.alodoktertest.databinding.FragmentOptionsContentDialogBinding
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.ContentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OptionsContentDialogFragment : DialogFragment() {
    private val viewModel: ContentViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val binding = FragmentOptionsContentDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        binding.apply {
            btnDelete.setOnClickListener {
                viewModel.onClickDeleteContent()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.contentsEvent.collect {
                when(it) {
                    is ContentViewModel.ContentEvent.DeleteContent -> {
                        viewModel.deleteContent()
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()
                            Toast.makeText(requireContext(), "Berhasil menghapus", Toast.LENGTH_SHORT).show()
                        }, 200)
                    }
                    else -> {
                        //
                    }
                }
            }
        }

        dialog.show()
        return dialog
    }
}