package workshop.studylab

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import workshop.studylab.data.DemoSessionsRepository

class CatalogueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        val factory = viewModelFactory {
            initializer { CatalogueViewModel(DemoSessionsRepository(), createSavedStateHandle()) }
        }
        setContent {
            StudyLabTheme {
                // Outside NavHost: both destinations share this Activity-owned instance.
                val model: CatalogueViewModel = viewModel(factory = factory)
                CatalogueApp(model)
            }
        }
    }
}
