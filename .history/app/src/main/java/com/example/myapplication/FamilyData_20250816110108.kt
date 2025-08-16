package com.example.myapplication

/**
 * Data models for family coordination system.
 * 
 * These models represent the core entities needed for family members
 * to coordinate medication care for elderly parents.
 * 
 * @since 1.0.0
 */

/**
 * Represents a family's medication coordination data.
 * 
 * Contains information about the parent being cared for,
 * today's medication assignments, and any missed medications
 * that require immediate attention.
 * 
 * @param parentName The name of the parent being cared for
 * @param todaysAssignments List of medication assignments for today
 * @param missedMedications List of recently missed medications
 */
data class FamilyData(
    val parentName: String,
    val todaysAssignments: List<Assignment>,
    val missedMedications: List<MissedMed>
)

/**
 * Represents a medication assignment for a family member.
 * 
 * Each assignment includes timing, medication details, who's responsible,
 * current status, and completion information.
 * 
 * @param time When the medication should be taken
 * @param medication Name and dosage of the medication
 * @param assignedTo Family member responsible for this assignment
 * @param status Current completion status
 * @param completedAt When the assignment was completed (if applicable)
 */
data class Assignment(
    val time: String,
    val medication: String,
    val assignedTo: String,
    val status: AssignmentStatus,
    val completedAt: String?
)

/**
 * Represents a missed medication that requires attention.
 * 
 * Critical information for family members to take immediate action
 * on missed medications.
 * 
 * @param parent Which parent missed the medication
 * @param medication What medication was missed
 * @param when When the medication was missed
 */
data class MissedMed(
    val parent: String,
    val medication: String,
    val whenMissed: String
)

/**
 * Status of a medication assignment.
 * 
 * Tracks the progress of medication assignments through
 * the family coordination workflow.
 */
enum class AssignmentStatus {
    /** Assignment has been completed successfully */
    COMPLETED,
    
    /** Assignment is currently pending and needs attention */
    PENDING,
    
    /** Assignment is scheduled for later today */
    UPCOMING
}
