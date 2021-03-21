package co.harismiftahulhudha.alodoktertest.mvvm.views.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.FragmentHomeBinding
import co.harismiftahulhudha.alodoktertest.mvvm.joinmodels.ContentImageJoinModel
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.ContentViewModel
import co.harismiftahulhudha.alodoktertest.mvvm.views.activities.AuthenticationActivity
import co.harismiftahulhudha.alodoktertest.mvvm.views.activities.MainActivity
import co.harismiftahulhudha.alodoktertest.mvvm.views.adapters.ContentAdapter
import co.harismiftahulhudha.alodoktertest.utils.SortContents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: ContentViewModel by viewModels()
    private val contentAdapter: ContentAdapter = ContentAdapter()
    private var userId = -1
    private var isNewest = true
    private var isScrollToTop = false
    private lateinit var searchView: SearchView
    private val handler = Handler(Looper.getMainLooper())
    private var runnableScrollTop: Runnable = Runnable {  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        subscribeListeners()
        subscribeObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchQuery.value = newText
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
            }
            R.id.sort -> {
                val action = HomeFragmentDirections.actionGlobalSortContentDialogFragment(isNewest)
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initComponents(view: View) {
        binding = FragmentHomeBinding.bind(view)
        (requireActivity() as MainActivity).binding.bnvMain.menu.findItem(R.id.homeNav)
            .setChecked(true)
        binding.apply {
            rvContent.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = contentAdapter
            }
        }
    }

    private fun subscribeListeners() {
        contentAdapter.setListener(object : ContentAdapter.OnListener {
            override fun onClickListener(contentImageJoin: ContentImageJoinModel, position: Int) {
                viewModel.onClickContent(contentImageJoin)
            }

            override fun onLongClickListener(contentImageJoin: ContentImageJoinModel, position: Int) {
                viewModel.onLongClickContent(contentImageJoin)
            }
        })
    }

    private fun subscribeObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.contentsEvent.collect {
                when(it) {
                    is ContentViewModel.ContentEvent.NavigateToDetail -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToDetailContentFragment(it.contentImageJoin)
                        findNavController().navigate(action)
                    }
                    is ContentViewModel.ContentEvent.NavigateToOption -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToOptionsContentDialogFragment(it.contentImageJoin)
                        findNavController().navigate(action)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.preferencesFlow.collect {
                when(it.sortContents) {
                    SortContents.OLDEST -> {
                        isNewest = false
                    }
                    SortContents.NEWEST -> {
                        isNewest = true
                    }
                }
                if (it.id == -1) {
                    Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
                    requireActivity().finish()
                } else {
                    userId = it.id
                }
                isScrollToTop = it.isContentScrollToTop
            }
        }

        viewModel.getContents(userId).observe(viewLifecycleOwner, {
            if (it != null) {
                contentAdapter.submitList(it)
                scrollTop()
            }
        })
    }

    private fun scrollTop() {
        if (isScrollToTop) {
            handler.removeCallbacks(runnableScrollTop)
            runnableScrollTop = Runnable {
                binding.rvContent.scrollToPosition(0)
                viewModel.updateIsContentScrollToTop(false)
            }
            Handler(Looper.getMainLooper()).postDelayed(runnableScrollTop, 200)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}