@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Family Coordination Dashboard - Main screen for adult children monitoring parents.
 * 
 * This screen serves as mission control for family members coordinating
 * medication care for elderly parents. Unlike traditional medication apps
 * that require seniors to use technology, this dashboard is designed for
 * adult children who want to ensure their parents' medication adherence.
 * 
 * Key Design Principles:
 * - Family-first: Built for adult children as primary users
 * - Assignment-based: Shows who's responsible for checking on parents
 * - Status-focused: Immediate visibility into medication adherence
 * - Emergency-ready: Quick access to critical information
 * 
 * User Flow:
 * 1. Adult child opens app and sees today's assignments
 * 2. Completes assigned check-ins (call parent, confirm meds taken)
 * 3. Updates status for other family members to see
 * 4. Receives alerts if critical medications are missed
 * 
 * @param onNavigateToEmergency Callback to navigate to emergency information
 * @param onNavigateToReports Callback to navigate to care reports
 * @since 1.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyDashboardScreen(
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToReports: () -> Unit = {}
) {
    // MOCK DATA for MVP demonstration
    val mockFamily = FamilyData(
        parentName = "Mom (Betty)",
        todaysAssignments = listOf(
            Assignment(
                time = "8:00 AM",
                medication = "Lisinopril 10mg", 
                assignedTo = "Sarah",
                status = AssignmentStatus.COMPLETED,
                completedAt = "8:05 AM"
            ),
            Assignment(
                time = "12:00 PM",
                medication = "Aspirin 81mg",
                assignedTo = "You",
                status = AssignmentStatus.PENDING,
                completedAt = null
            ),
            Assignment(
                time = "6:00 PM", 
                medication = "Vitamin D 1000 IU",
                assignedTo = "Mike",
                status = AssignmentStatus.UPCOMING,
                completedAt = null
            )
        ),
        missedMedications = listOf(
            MissedMed("Dad", "Evening insulin", "Yesterday 7:00 PM")
        )
    )

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Family Care Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("family_dashboard_top_bar")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alert section for missed critical meds
            if (mockFamily.missedMedications.isNotEmpty()) {
                item {
                    CriticalAlertCard(missedMeds = mockFamily.missedMedications)
                }
            }
            
            // Today's assignments section
            item {
                Text(
                    text = "Today's Assignments - ${mockFamily.parentName}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("assignments_header")
                )
            }
            
            items(mockFamily.todaysAssignments) { assignment ->
                AssignmentCard(
                    assignment = assignment,
                    onCompleteAssignment = { /* Handle assignment completion */ },
                    onReassign = { /* Handle reassignment */ },
                    modifier = Modifier.testTag("assignment_card_${assignment.time.replace(":", "_")}")
                )
            }
            
            // Quick actions
            item {
                QuickActionsCard()
            }
        }
    }
}

/**
 * Displays critical alerts for missed medications that require immediate attention.
 * 
 * This card is prominently displayed at the top when there are missed medications
 * to ensure family members take immediate action on critical situations.
 * 
 * @param missedMeds List of missed medications requiring attention
 */
@Composable
fun CriticalAlertCard(missedMeds: List<MissedMed>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("critical_alert_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Critical alert",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("warning_icon")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Critical Alert",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("critical_alert_title")
                )
            }
            
            missedMeds.forEach { missed ->
                Text(
                    text = "${missed.parent} missed ${missed.medication} (${missed.whenMissed})",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Call parent */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("call_now_button")
                ) {
                    Text("Call Now")
                }
                OutlinedButton(
                    onClick = { /* Notify other family */ },
                    modifier = Modifier.testTag("alert_family_button")
                ) {
                    Text("Alert Family")
                }
            }
        }
    }
}

/**
 * Displays individual assignment card with status and action buttons.
 * 
 * Each assignment card shows the medication details, who's responsible,
 * current status, and appropriate action buttons based on the assignment state.
 * 
 * @param assignment The medication assignment to display
 * @param onCompleteAssignment Callback when assignment is completed
 * @param onReassign Callback when assignment needs to be reassigned
 * @param modifier Optional modifier for customizing appearance
 */
@Composable
fun AssignmentCard(
    assignment: Assignment,
    onCompleteAssignment: () -> Unit,
    onReassign: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${assignment.time} - ${assignment.medication}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.testTag("assignment_title_${assignment.time.replace(":", "_")}")
                    )
                    Text(
                        text = "Assigned to: ${assignment.assignedTo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("assignment_assigned_to_${assignment.time.replace(":", "_")}")
                    )
                }
                
                AssignmentStatusIndicator(
                    status = assignment.status,
                    modifier = Modifier.testTag("status_indicator_${assignment.time.replace(":", "_")}")
                )
            }
            
            if (assignment.status == AssignmentStatus.COMPLETED && assignment.completedAt != null) {
                Text(
                    text = "✓ Confirmed at ${assignment.completedAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag("completion_time_${assignment.time.replace(":", "_")}")
                )
            }
            
            if (assignment.status == AssignmentStatus.PENDING && assignment.assignedTo == "You") {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCompleteAssignment,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("complete_button_${assignment.time.replace(":", "_")}")
                    ) {
                        Text("Complete Check-in")
                    }
                    OutlinedButton(
                        onClick = onReassign,
                        modifier = Modifier.testTag("reassign_button_${assignment.time.replace(":", "_")}")
                    ) {
                        Text("Reassign")
                    }
                }
            }
        }
    }
}

/**
 * Shows visual status indicator for assignment completion.
 * 
 * Provides immediate visual feedback on the status of each assignment
 * using color-coded icons and text for quick recognition.
 * 
 * @param status The current status of the assignment
 * @param modifier Optional modifier for customizing appearance
 */
@Composable
fun AssignmentStatusIndicator(
    status: AssignmentStatus,
    modifier: Modifier = Modifier
) {
    val (icon, color, text) = when (status) {
        AssignmentStatus.COMPLETED -> Triple(Icons.Default.CheckCircle, MaterialTheme.colorScheme.tertiary, "Done")
        AssignmentStatus.PENDING -> Triple(Icons.Default.CheckCircle, MaterialTheme.colorScheme.primary, "Pending")
        AssignmentStatus.UPCOMING -> Triple(Icons.Default.CheckCircle, MaterialTheme.colorScheme.onSurfaceVariant, "Upcoming")
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

/**
 * Quick action buttons for common family coordination tasks.
 * 
 * Provides easy access to frequently used features like emergency
 * information and weekly reports for family coordination.
 */
@Composable
fun QuickActionsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_actions_card"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.testTag("quick_actions_title")
            )
            
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Emergency info */ },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("emergency_info_button")
                ) {
                    Text("Emergency Info")
                }
                OutlinedButton(
                    onClick = { /* View reports */ },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("weekly_report_button")
                ) {
                    Text("Weekly Report")
                }
            }
        }
    }
}

/**
 * Preview function for development and testing.
 * 
 * Shows how the FamilyDashboardScreen will look with sample data
 * in the Android Studio preview pane.
 */
@Preview(showBackground = true)
@Composable
fun FamilyDashboardPreview() {
    MaterialTheme {
        FamilyDashboardScreen()
    }
}
