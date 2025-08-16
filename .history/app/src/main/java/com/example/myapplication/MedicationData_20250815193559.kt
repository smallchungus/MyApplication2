package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Data class representing a medication in the medication reminder system.
 * 
 * This entity is designed for Room database storage and follows Material Design 3 principles
 * for medical applications. It includes comprehensive medication information with
 * accessibility considerations for senior users.
 * 
 * @property id Unique identifier for the medication (auto-generated)
 * @property name Human-readable name of the medication
 * @property dosage Prescribed dosage amount and unit
 * @property timeToTake Time of day when medication should be taken
 * @property isTaken Whether the medication has been taken for the current day
 * 
 * @author Medication Reminder App Team
 * @version 1.0.0
 * @since 2024
 */
@Entity(
    tableName = "medications",
    // Room table configuration for optimal performance
    indices = [
        androidx.room.Index(value = ["timeToTake"]),
        androidx.room.Index(value = ["isTaken"])
    ]
)
data class Medication(
    /**
     * Primary key for the medication record.
     * Auto-generated to ensure uniqueness across the database.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    
    /**
     * Human-readable name of the medication.
     * Examples: "Aspirin", "Vitamin D", "Blood Pressure Medication"
     * 
     * @see [androidx.room.ColumnInfo] for database column configuration
     */
    @ColumnInfo(name = "name", typeAffinity = androidx.room.ColumnInfo.TEXT)
    val name: String,
    
    /**
     * Prescribed dosage with unit of measurement.
     * Examples: "100mg", "1000 IU", "5mg"
     * 
     * @see [androidx.room.ColumnInfo] for database column configuration
     */
    @ColumnInfo(name = "dosage", typeAffinity = androidx.room.ColumnInfo.TEXT)
    val dosage: String,
    
    /**
     * Time of day when medication should be taken.
     * Examples: "Morning", "Afternoon", "Evening", "Night"
     * 
     * @see [androidx.room.ColumnInfo] for database column configuration
     */
    @ColumnInfo(name = "timeToTake", typeAffinity = androidx.room.ColumnInfo.TEXT)
    val timeToTake: String,
    
    /**
     * Whether the medication has been taken for the current day.
     * Used for tracking medication adherence and UI state management.
     * 
     * @see [androidx.room.ColumnInfo] for database column configuration
     */
    @ColumnInfo(name = "isTaken", typeAffinity = androidx.room.ColumnInfo.INTEGER)
    val isTaken: Boolean = false
) {
    /**
     * Companion object containing utility functions and constants.
     */
    companion object {
        /**
         * Default medication for testing and development purposes.
         * 
         * @return A sample medication instance
         */
        fun createSampleMedication(): Medication = Medication(
            id = 1,
            name = "Sample Medication",
            dosage = "100mg",
            timeToTake = "Morning",
            isTaken = false
        )
        
        /**
         * Validates medication data for business logic compliance.
         * 
         * @param medication The medication to validate
         * @return True if medication data is valid, false otherwise
         */
        fun isValid(medication: Medication): Boolean {
            return medication.name.isNotBlank() &&
                   medication.dosage.isNotBlank() &&
                   medication.timeToTake.isNotBlank()
        }
        
        /**
         * Common medication time periods for UI consistency.
         */
        val TIME_PERIODS = listOf("Morning", "Afternoon", "Evening", "Night", "As Needed")
        
        /**
         * Common dosage units for validation and UI display.
         */
        val DOSAGE_UNITS = listOf("mg", "mcg", "IU", "ml", "tablet", "capsule", "drop")
    }
    
    /**
     * Creates a copy of this medication with updated taken status.
     * 
     * @param taken New taken status
     * @return New medication instance with updated status
     */
    fun markAsTaken(taken: Boolean = true): Medication = copy(isTaken = taken)
    
    /**
     * Creates a copy of this medication with updated dosage.
     * 
     * @param newDosage New dosage information
     * @return New medication instance with updated dosage
     */
    fun updateDosage(newDosage: String): Medication = copy(dosage = newDosage)
    
    /**
     * Creates a copy of this medication with updated time.
     * 
     * @param newTime New time to take medication
     * @return New medication instance with updated time
     */
    fun updateTime(newTime: String): Medication = copy(timeToTake = newTime)
    
    /**
     * Returns a human-readable description of the medication status.
     * 
     * @return Status description string
     */
    fun getStatusDescription(): String = when {
        isTaken -> "✓ Taken"
        else -> "⏰ Due"
    }
    
    /**
     * Returns a formatted display string for the medication.
     * 
     * @return Formatted medication string
     */
    fun getDisplayString(): String = "$name - $dosage at $timeToTake"
    
    override fun toString(): String = "Medication(id=$id, name='$name', dosage='$dosage', timeToTake='$timeToTake', isTaken=$isTaken)"
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Medication
        
        if (id != other.id) return false
        if (name != other.name) return false
        if (dosage != other.dosage) return false
        if (timeToTake != other.timeToTake) return false
        if (isTaken != other.isTaken) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + dosage.hashCode()
        result = 31 * result + timeToTake.hashCode()
        result = 31 * result + isTaken.hashCode()
        return result
    }
}
