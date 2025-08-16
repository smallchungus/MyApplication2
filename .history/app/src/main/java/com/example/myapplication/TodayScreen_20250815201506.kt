@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen() {
    val medications = listOf(
        Medication(1, "Aspirin", "81mg", "08:00", false),
        Medication(2, "Vitamin D", "1000 IU", "18:00", false)
    )
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Today") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(medications) { med ->
                Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(med.name, style = MaterialTheme.typography.titleMedium)
                        Text("${med.dosage} at ${med.timeToTake}")
                    }
                }
            }
        }
    }
}
