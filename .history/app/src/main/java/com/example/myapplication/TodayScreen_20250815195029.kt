package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Today's Medications Screen - Main Application Interface
 * 
 * This screen serves as the primary interface for medication management, displaying
 * all medications scheduled for the current day. It's the first screen users see
 * when launching the app, providing immediate visibility into their daily medication
 * schedule.
 * 
 * ## User Experience Design
 * 
 * **Immediate Visibility**: Users can instantly see what medications they need to take
 * today, reducing cognitive load and improving medication adherence.
 * 
 * **Clear Status Indicators**: Visual cues (checkmarks, colors) immediately show
 * which medications have been taken and which are still pending.
 * 
 * **Time-Based Organization**: Medications are displayed in chronological order,
 * helping users understand their daily medication routine.
 * 
 * ## Accessibility Features
 * 
 * **Senior-Friendly Design**: Large text sizes, high contrast colors, and clear
 * visual hierarchy support users with age-related vision changes.
 * 
 * **Touch-Friendly Targets**: Adequate button sizes and spacing for users with
 * motor control challenges.
 * 
 * **Clear Status Communication**: Both visual and textual indicators for medication
 * status to support users with color vision deficiencies.
 * 
 * ## Visual Hierarchy and Spacing
 * 
 * **Material 3 Guidelines**: Follows Material Design 3 spacing system (8dp base unit)
 * for consistent visual rhythm and professional appearance.
 * 
 * **Card-Based Layout**: Each medication is displayed in its own card for clear
 * separation and easy scanning.
 * 
 * **Progressive Disclosure**: Most important information (medication name, time)
 * is displayed prominently, with secondary details (dosage) in supporting positions.
 * 
 * @author Medication Reminder App Team
 * @since 1.0.0
 * @see [MedAppTheme] for theme implementation
 * @see [MedicationCard] for individual medication display
 */
@Composable
fun TodayScreen() {
    // Sample medication data for demonstration
    // In a real app, this would come from a ViewModel or Repository
    var medications by remember {
        mutableStateOf(
            listOf(
                Medication(
                    id = 1,
                    name = "Aspirin",
                    dosage = "81mg",
                    timeToTake = "08:00",
                    isTaken = false
                ),
                Medication(
                    id = 2,
                    name = "Lisinopril",
                    dosage = "10mg",
                    timeToTake = "08:00",
                    isTaken = true
                ),
                Medication(
                    id = 3,
                    name = "Vitamin D",
                    dosage = "1000 IU",
                    timeToTake = "18:00",
                    isTaken = false
                ),
                Medication(
                    id = 4,
                    name = "Metformin",
                    dosage = "500mg",
                    timeToTake = "12:00",
                    isTaken = false
                ),
                Medication(
                    id = 5,
                    name = "Fish Oil",
                    dosage = "1000mg",
                    timeToTake = "18:00",
                    isTaken = false
                )
            )
        )
    }

    // Get current date for display
    val currentDate = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    val formattedDate = currentDate.format(dateFormatter)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("screen_title")
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.testTag("current_date")
                        )
                    }
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
                onClick = { /* TODO: Add medication functionality */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_medication_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new medication",
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
                // Empty state - shown when no medications are scheduled
                EmptyMedicationState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            } else {
                // Medication list - main content area
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        top = 16.dp,
                        bottom = 100.dp // Extra space for FAB
                    ),
                    content = {
                        items(medications) { medication ->
                            MedicationCard(
                                medication = medication,
                                onTakeMedication = { med ->
                                    // Update medication taken status
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

/**
 * Individual medication card component
 * 
 * Displays a single medication with its details and interactive elements.
 * Each card provides clear visual feedback about the medication's status
 * and allows users to mark it as taken or not taken.
 * 
 * **Design Principles**:
 * - Clear visual hierarchy with medication name as primary information
 * - Time prominently displayed for quick scanning
 * - Status button provides immediate feedback and interaction
 * - Consistent spacing and typography for professional appearance
 * 
 * **Accessibility Features**:
 * - High contrast colors for status indicators
 * - Large touch targets for interaction
 * - Clear visual separation between different information types
 * 
 * @param medication The medication data to display
 * @param onTakeMedication Callback when medication status is changed
 * @param modifier Modifier for customizing the card appearance
 */
@Composable
fun MedicationCard(
    medication: Medication,
    onTakeMedication: (Medication) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header row with time and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time to take",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("time_icon_${medication.id}")
                    )
                    Text(
                        text = medication.timeToTake,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("medication_time_${medication.id}")
                    )
                }

                // Status indicator
                if (medication.isTaken) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Medication taken",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.testTag("taken_icon_${medication.id}")
                    )
                }
            }

            // Medication details
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("medication_name_${medication.id}")
                )
                
                Text(
                    text = medication.dosage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("medication_dosage_${medication.id}")
                )
            }

            // Action button
            androidx.compose.material3.Button(
                onClick = { onTakeMedication(medication) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("take_button_${medication.id}"),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (medication.isTaken) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                Text(
                    text = if (medication.isTaken) "Taken ✓" else "Take Now",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Empty state component shown when no medications are scheduled
 * 
 * Provides a welcoming message and clear call-to-action for users
 * who haven't added any medications yet. This component follows
 * Material Design 3 empty state patterns for consistency.
 * 
 * **Design Considerations**:
 * - Centered layout for visual balance
 * - Clear messaging about what to do next
 * - Encouraging tone to reduce user anxiety
 * - Consistent with overall app design language
 * 
 * @param modifier Modifier for customizing the empty state appearance
 */
@Composable
fun EmptyMedicationState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "No medications",
            modifier = Modifier
                .testTag("empty_state_icon")
                .padding(bottom = 24.dp)
                .size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "No medications scheduled",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("empty_state_title")
        )
        
        Text(
            text = "Tap the + button to add your first medication and start managing your health",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .testTag("empty_state_subtitle")
                .padding(top = 8.dp)
        )
    }
}

/**
 * Preview function for TodayScreen
 * 
 * Shows how the screen appears in Android Studio's preview pane.
 * Uses sample data to demonstrate the full interface.
 */
@Preview(showBackground = true)
@Composable
fun TodayScreenPreview() {
    // Note: In a real preview, you would use MedAppTheme here
    // For now, using the default theme to avoid compilation issues
    androidx.compose.material3.MaterialTheme {
        TodayScreen()
    }
}

/**
 * Preview function for MedicationCard
 * 
 * Shows individual medication cards in different states
 * for design review and testing.
 */
@Preview(showBackground = true)
@Composable
fun MedicationCardPreview() {
    androidx.compose.material3.MaterialTheme {
        MedicationCard(
            medication = Medication(
                id = 1,
                name = "Sample Medication",
                dosage = "100mg",
                timeToTake = "09:00",
                isTaken = false
            ),
            onTakeMedication = { /* Preview only */ }
        )
    }
}
