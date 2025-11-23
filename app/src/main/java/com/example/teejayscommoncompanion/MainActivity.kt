package com.example.teejayscommoncompanion

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teejayscommoncompanion.ui.theme.TeejaysCommonCompanionTheme
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeejaysCommonCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content(name = stringResource(id = R.string.app_name))
                }
            }
        }
    }
}

@Composable
fun Content(
    name: String,
    modifier: Modifier = Modifier
) {
    var recipient by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val generativeModel = Firebase.ai(backend = GenerativeBackend.vertexAI())
        .generativeModel("gemini-2.5-flash")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
        )
        TextField(
            value = recipient,
            onValueChange = { recipient = it },
            label = { Text("Recipient") }
        )
        TextField(
            value = budget,
            onValueChange = { budget = it },
            label = { Text("Budget") }
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val prompt = "Give me a gift idea for a ${recipient} with a budget of ${budget}."
                        result = generativeModel.generateContent(prompt).text.orEmpty()
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error generating content", e)
                        result = "Something unexpected happened."
                    }
                }
            },
            enabled = recipient.isNotBlank() && budget.isNotBlank()
        ) {
            Text(text = "Get Gift Idea")
        }
        Text(text = result)
    }
}