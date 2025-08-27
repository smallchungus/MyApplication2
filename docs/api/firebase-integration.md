# Firebase Integration API Documentation

## Problem
MedSync Pro integrates with multiple Firebase services requiring clear interface documentation for authentication, data storage, messaging, and analytics to ensure consistent implementation across the app.

## Firebase Services Architecture

### Service Integration Overview
| Service | Purpose | Key Interfaces |
|---------|---------|---------------|
| Firebase Auth | User authentication and session management | `FirebaseAuth`, `AuthRepository` |
| Cloud Firestore | Real-time database and data synchronization | `FirebaseFirestore`, `FamilyRepository` |
| Cloud Messaging | Push notifications and alerts | `FirebaseMessaging`, `NotificationService` |
| Analytics | User behavior tracking and insights | `FirebaseAnalytics`, `AnalyticsService` |
| Crashlytics | Crash reporting and performance monitoring | `FirebaseCrashlytics` |

## Authentication API

### Firebase Auth Interface
```kotlin
interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun updateProfile(displayName: String, photoUrl: String?): Result<Unit>
    fun observeAuthState(): Flow<User?>
}
```

### Implementation Patterns
- **Error Handling**: All auth methods return `Result<T>` for consistent error handling
- **Session Management**: 24-hour auto-logout for security
- **Biometric Integration**: Local biometric authentication for app access
- **State Observation**: Reactive auth state changes via Flow

### Common Auth Flows
```kotlin
// Sign-in flow with error handling
suspend fun signIn(email: String, password: String) {
    authRepository.signInWithEmail(email, password)
        .onSuccess { user -> navigateToHome(user) }
        .onFailure { error -> handleAuthError(error) }
}
```

## Firestore Data API

### Repository Interfaces
```kotlin
interface FamilyRepository {
    suspend fun getFamilies(userId: String): List<Family>
    suspend fun createFamily(family: Family): Result<String>
    suspend fun updateFamily(family: Family): Result<Unit>
    suspend fun inviteToFamily(familyId: String, email: String): Result<Unit>
    fun observeFamilyChanges(familyId: String): Flow<Family>
}

interface MedicationRepository {
    suspend fun getMedications(parentId: String): List<Medication>
    suspend fun addMedication(medication: Medication): Result<String>
    suspend fun updateMedication(medication: Medication): Result<Unit>
    suspend fun deleteMedication(medicationId: String): Result<Unit>
    fun observeMedications(parentId: String): Flow<List<Medication>>
}
```

### Document Structure Patterns
- **Hierarchical Data**: `families/{familyId}/parents/{parentId}/medications/{medicationId}`
- **Real-time Updates**: Use `Flow<T>` for live data observation
- **Offline Support**: Firestore cache enables offline-first operations
- **Security Rules**: Role-based access control enforced at database level

### Query Optimization
```kotlin
// Efficient querying with indexes
fun getActiveMedications(parentId: String): Query {
    return firestore.collection("medications")
        .whereEqualTo("parentId", parentId)
        .whereEqualTo("isActive", true)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(20)
}
```

## Cloud Messaging API

### Notification Service Interface
```kotlin
interface NotificationService {
    suspend fun subscribeTo(topic: String): Result<Unit>
    suspend fun unsubscribeFrom(topic: String): Result<Unit>
    suspend fun sendToUser(userId: String, notification: Notification): Result<Unit>
    suspend fun sendToFamily(familyId: String, notification: Notification): Result<Unit>
    suspend fun scheduleMedicationReminder(medication: Medication): Result<Unit>
    suspend fun cancelScheduledNotifications(medicationId: String): Result<Unit>
}
```

### Notification Types
| Type | Topic Pattern | Purpose |
|------|---------------|---------|
| Medication Reminder | `user_{userId}_medications` | Individual medication alerts |
| Family Updates | `family_{familyId}_updates` | Family coordination messages |
| Emergency Alerts | `family_{familyId}_emergency` | Critical health alerts |
| System Announcements | `all_users` | App updates and maintenance |

### Message Payload Structure
```kotlin
data class NotificationPayload(
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap(),
    val priority: Priority = Priority.NORMAL,
    val actionButtons: List<ActionButton> = emptyList()
)

enum class NotificationType {
    MEDICATION_REMINDER,
    ASSIGNMENT_UPDATE,
    EMERGENCY_ALERT,
    FAMILY_INVITATION
}
```

## Analytics API

### Analytics Service Interface
```kotlin
interface AnalyticsService {
    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap())
    fun setUserProperty(name: String, value: String)
    fun setUserId(userId: String)
    fun logScreenView(screenName: String, screenClass: String)
    fun logMedicationTaken(medicationId: String, onTime: Boolean)
    fun logFamilyInteraction(action: String, familyId: String)
}
```

### Key Events Tracked
```kotlin
// Medication adherence tracking
analyticsService.logMedicationTaken(
    medicationId = medication.id,
    onTime = timeDiff < 30.minutes
)

// Family coordination events
analyticsService.logFamilyInteraction(
    action = "assignment_completed",
    familyId = family.id
)

// User engagement metrics
analyticsService.logScreenView(
    screenName = "medication_list",
    screenClass = "MedicationListScreen"
)
```

## Error Handling Patterns

### Firebase Error Types
```kotlin
sealed class FirebaseError : Exception() {
    object NetworkError : FirebaseError()
    object AuthenticationError : FirebaseError()
    object PermissionError : FirebaseError()
    object QuotaExceededError : FirebaseError()
    object DocumentNotFoundError : FirebaseError()
    data class UnknownError(val cause: Throwable) : FirebaseError()
}
```

### Error Handling Strategy
```kotlin
suspend fun <T> handleFirebaseOperation(
    operation: suspend () -> T
): Result<T> = try {
    Result.success(operation())
} catch (e: FirebaseAuthException) {
    Result.failure(mapAuthError(e))
} catch (e: FirebaseFirestoreException) {
    Result.failure(mapFirestoreError(e))
} catch (e: Exception) {
    Crashlytics.recordException(e)
    Result.failure(FirebaseError.UnknownError(e))
}
```

## Security Configuration

### Firestore Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Family access control
    match /families/{familyId} {
      allow read, write: if request.auth != null 
        && request.auth.uid in resource.data.memberIds;
    }
    
    // Medication data - family members only
    match /families/{familyId}/medications/{medicationId} {
      allow read, write: if request.auth != null 
        && isFamilyMember(familyId, request.auth.uid);
    }
  }
  
  function isFamilyMember(familyId, userId) {
    return userId in get(/databases/$(database)/documents/families/$(familyId)).data.memberIds;
  }
}
```

### API Key Management
- **Development**: Use Firebase emulator for local testing
- **Staging**: Separate Firebase project with restricted quotas
- **Production**: Full Firebase project with monitoring and alerts
- **Security**: API keys restricted by package name and SHA certificates

## Performance Optimization

### Caching Strategy
```kotlin
// Local caching with TTL
@Singleton
class CachedFamilyRepository @Inject constructor(
    private val remoteRepository: FamilyRepository,
    private val localCache: FamilyCache
) : FamilyRepository {
    
    override suspend fun getFamilies(userId: String): List<Family> {
        val cached = localCache.get(userId)
        if (cached != null && !cached.isExpired()) {
            return cached.data
        }
        
        return remoteRepository.getFamilies(userId).also { families ->
            localCache.put(userId, families, ttl = 30.minutes)
        }
    }
}
```

### Batch Operations
```kotlin
// Efficient batch writes
suspend fun batchUpdateMedications(updates: List<MedicationUpdate>) {
    firestore.runBatch { batch ->
        updates.forEach { update ->
            val docRef = firestore.collection("medications").document(update.id)
            batch.update(docRef, update.toMap())
        }
    }
}
```

## Testing Strategies

### Firebase Emulator Setup
```kotlin
// Test configuration
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseModule::class]
)
object TestFirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth.apply {
            useEmulator("10.0.2.2", 9099)
        }
    }
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore.apply {
            useEmulator("10.0.2.2", 8080)
        }
    }
}
```

### Mock Repository Implementation
```kotlin
class MockFamilyRepository : FamilyRepository {
    private val families = mutableListOf<Family>()
    
    override suspend fun getFamilies(userId: String): List<Family> {
        return families.filter { family ->
            family.memberIds.contains(userId)
        }
    }
    
    override suspend fun createFamily(family: Family): Result<String> {
        families.add(family)
        return Result.success(family.id)
    }
}
```

## References
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started)
- [Repository Pattern Implementation](../architecture/README.md#repository-pattern)