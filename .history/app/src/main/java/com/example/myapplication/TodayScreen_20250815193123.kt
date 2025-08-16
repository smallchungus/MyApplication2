package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.LazyColumn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MedAppTheme

@Composable
fun TodayScreen() {
    // Sample data for demonstration
    var medications by remember {
        mutableStateOf(
            listOf(
                Medication(
                    id = 1,
                    name = "Aspirin",
                    dosage = "100mg",
                    timeToTake = "Morning",
                    isTaken = false
                ),
                Medication(
                    id = 2,
                    name = "Vitamin D",
                    dosage = "1000 IU",
                    timeToTake = "Afternoon",
                    isTaken = true
                ),
                Medication(
                    id = 3,
                    name = "Blood Pressure Med",
                    dosage = "5mg",
                    timeToTake = "Evening",
                    isTaken = false
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medication Reminder",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("app_title")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("top_app_bar")
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add medication */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_medication_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add medication",
                    modifier = Modifier.testTag("add_icon")
                )
            }
        },
        modifier = Modifier.testTag("today_screen")
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (medications.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No medications scheduled",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("empty_state_text")
                    )
                    Text(
                        text = "Tap the + button to add your first medication",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("empty_state_subtitle")
                    )
                }
            } else {
                // Medication list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    content = {
                        items(medications.size) { index ->
                            val medication = medications[index]
                            MedicationCard(
                                medication = medication,
                                onTakeMedication = { med ->
                                    medications = medications.map { 
                                        if (it.id == med.id) it.copy(isTaken = !it.isTaken) else it 
                                    }
                                },
                                modifier = Modifier.testTag("medication_card_${medication.id}")
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodayScreenPreview() {
    MedAppTheme {
        TodayScreen()
    }
}
