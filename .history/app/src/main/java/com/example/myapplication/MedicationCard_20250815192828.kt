package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MedAppTheme

@Composable
fun MedicationCard(
    medication: Medication,
    onTakeMedication: (Medication) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("medication_card_${medication.id}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with medication icon and name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = stringResource(android.R.string.unknownName),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("medication_icon_${medication.id}")
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("medication_name_${medication.id}")
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Medication details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Dosage
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dosage:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("dosage_label_${medication.id}")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = medication.dosage,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.testTag("dosage_value_${medication.id}")
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Time to take
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = stringResource(android.R.string.unknownName),
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.testTag("time_icon_${medication.id}")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = medication.timeToTake,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("time_value_${medication.id}")
                        )
                    }
                }
                
                // Take button
                Button(
                    onClick = { onTakeMedication(medication) },
                    enabled = !medication.isTaken,
                    modifier = Modifier.testTag("take_button_${medication.id}")
                ) {
                    Text(
                        text = if (medication.isTaken) "Taken" else "Take",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            
            // Status indicator
            if (medication.isTaken) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "✓ Medication taken",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.testTag("status_indicator_${medication.id}")
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationCardPreview() {
    MedAppTheme {
        val sampleMedication = Medication(
            id = 1,
            name = "Aspirin",
            dosage = "100mg",
            timeToTake = "Morning",
            isTaken = false
        )
        
        MedicationCard(
            medication = sampleMedication,
            onTakeMedication = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationCardTakenPreview() {
    MedAppTheme {
        val sampleMedication = Medication(
            id = 2,
            name = "Vitamin D",
            dosage = "1000 IU",
            timeToTake = "Afternoon",
            isTaken = true
        )
        
        MedicationCard(
            medication = sampleMedication,
            onTakeMedication = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
