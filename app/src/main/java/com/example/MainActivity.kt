package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.database.WashlyDatabase
import com.example.data.repository.WashlyRepository
import com.example.ui.WashlyApp
import com.example.ui.WashlyViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WashlyViewModel by viewModels {
        val database = WashlyDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = WashlyRepository(database.washlyDao())
        WashlyViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WashlyApp(viewModel = viewModel)
                }
            }
        }
    }
}
