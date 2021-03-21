package co.harismiftahulhudha.alodoktertest.mvvm.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.ActivityMainBinding
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.UserViewModel
import co.harismiftahulhudha.alodoktertest.mvvm.views.fragments.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        subscribeListeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.updateIsUserEdit(false)
    }

    private fun subscribeListeners() {
        binding.bnvMain.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeNav -> {
                    viewModel.updateIsUserEdit(false)
                    navController.navigate(R.id.homeFragment)
                    true
                }
                else -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToUserFragment()
                    navController.navigate(action)
                    true
                }
            }
        }
    }

    private fun initComponents() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostMainFragment) as NavHostFragment
        navController = navHostFragment.findNavController()
    }
}