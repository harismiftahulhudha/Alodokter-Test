package co.harismiftahulhudha.alodoktertest.mvvm.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            viewModel.preferencesFlow.collect {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (it.id != -1) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
                    }
                    finish()
                }, 2000)
            }
        }
    }
}