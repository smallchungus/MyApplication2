# Architecture Documentation - Lean MVP

## Problem
Solo developer needs simple, maintainable architecture that can deliver family medication coordination MVP in 8 weeks while ensuring offline reliability for healthcare use cases.

## Decision
**Clean Architecture + MVVM** with Firebase backend-as-a-service for rapid MVP development and validation.

### Core Principles
- **Offline-First**: Healthcare apps must work without internet
- **Lean Startup**: Ship fast, validate early, iterate based on user feedback
- **Firebase Native**: Leverage managed services to focus on user value
- **Test-Driven**: Write tests for critical medication logic

## Architecture Layers

### MVP Architecture Stack
```
Jetpack Compose UI (Declarative UI)
        ↓
ViewModel + StateFlow (State Management)
        ↓
Use Cases (Business Logic)
        ↓
Repository Pattern (Data Abstraction)
        ↓
Room Database (Local Storage)
        ↕
Firebase Sync (Background Cloud Sync)
```

### Layer Responsibilities
| Layer | Technology | Purpose | Testing Focus |
|-------|-----------|---------|---------------|
| **UI** | Jetpack Compose + Material 3 | User interaction and display | UI journey tests |
| **Presentation** | ViewModel + StateFlow | UI state management | ViewModel unit tests |
| **Domain** | Kotlin + Coroutines | Business rules and logic | Comprehensive unit tests |
| **Data** | Repository + Room + Firebase | Data storage and sync | Integration tests |
| **Infrastructure** | Hilt + Firebase SDK | Dependency injection | Mock-based tests |

## Key Architectural Decisions

### Firebase Over Custom Backend
| Aspect | Firebase | Custom Backend | MVP Decision |
|--------|----------|----------------|-------------|
| **Development Speed** | 2 weeks | 10+ weeks | ✅ Firebase |
| **Infrastructure** | Zero management | DevOps overhead | ✅ Firebase |
| **MVP Cost** | $0-20/month | $200+/month | ✅ Firebase |
| **Scalability** | Automatic to 100K users | Manual scaling | ✅ Firebase |

### Offline-First Architecture
- **Local Source of Truth**: Room database for instant app response
- **Background Sync**: Firebase updates when network available
- **Conflict Resolution**: Last-write-wins for MVP simplicity
- **Emergency Access**: Critical medication data always available

## Data Flow Architecture

### MVP Data Flow
```kotlin
// Simplified data flow for medication tracking
User Interaction (Compose)
        ↓
ViewModel (StateFlow updates)
        ↓
Use Case (Business validation)
        ↓
Repository (Data coordination)
        ↓
Room Database (Immediate storage)
        ↓
Background Worker (Sync when connected)
        ↓
Firebase Firestore (Family coordination)
```

### State Management Pattern
```kotlin
// Example: Medication list state
@HiltViewModel
class MedicationListViewModel @Inject constructor(
    private val getMedicationsUseCase: GetMedicationsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MedicationListUiState.Loading)
    val uiState: StateFlow<MedicationListUiState> = _uiState.asStateFlow()
    
    fun loadMedications(parentId: String) {
        viewModelScope.launch {
            _uiState.value = MedicationListUiState.Loading
            try {
                val medications = getMedicationsUseCase(parentId)
                _uiState.value = MedicationListUiState.Success(medications)
            } catch (e: Exception) {
                _uiState.value = MedicationListUiState.Error(e.message)
            }
        }
    }
}
```

## Module Structure

### Simplified Package Organization
```
app/src/main/kotlin/com/medsync/
├── ui/                          # Jetpack Compose screens and components
│   ├── medication/              # Medication management screens
│   ├── family/                  # Family coordination screens
│   ├── components/              # Reusable UI components
│   └── theme/                   # Material 3 theme and styling
├── domain/                      # Business logic (pure Kotlin)
│   ├── model/                   # Domain entities (Medication, Family, etc.)
│   ├── usecase/                 # Business operations
│   └── repository/              # Repository interfaces
├── data/                        # Data layer implementation
│   ├── repository/              # Repository implementations
│   ├── local/                   # Room database
│   ├── remote/                  # Firebase integration
│   └── sync/                    # Background synchronization
└── di/                          # Hilt dependency injection modules
```

## Testing Architecture

### MVP Testing Strategy
| Test Type | Coverage Target | Focus |
|-----------|----------------|-------|
| **Unit Tests** | 90% domain layer | Medication scheduling logic |
| **Integration Tests** | 80% data layer | Firebase/Room integration |
| **UI Tests** | Critical paths only | Core user journeys |

### Testing Implementation
```kotlin
// Example: Medication scheduling test
class MedicationSchedulerTest {
    
    @Test
    fun `should create daily schedule with correct intervals`() = runTest {
        // Given
        val medication = createTestMedication(
            times = listOf("08:00", "20:00"),
            frequency = DailyFrequency()
        )
        
        // When
        val schedules = medicationScheduler.generateSchedule(
            medication = medication,
            startDate = LocalDate.now(),
            days = 7
        )
        
        // Then
        assertEquals(14, schedules.size) // 2 times × 7 days
        schedules.forEach { schedule ->
            assertTrue(schedule.time in listOf("08:00", "20:00"))
        }
    }
}
```

## Performance Considerations

### MVP Performance Strategy
- **Instant UI**: Room database provides immediate data access
- **Minimal APK Size**: Target <20MB for slower download networks
- **Battery Efficiency**: Smart sync to preserve device battery
- **Memory Management**: Proper coroutine scoping and lifecycle awareness

### Device Target Strategy
```kotlin
// Performance targets for different device tiers
object PerformanceTargets {
    // Low-end: 2GB RAM, Android 7.0
    const val LOW_END_STARTUP_TIME = 3000L // 3 seconds
    
    // Mid-range: 4GB RAM, Android 9.0+
    const val MID_RANGE_STARTUP_TIME = 2000L // 2 seconds
    
    // High-end: 6GB+ RAM, Android 11+
    const val HIGH_END_STARTUP_TIME = 1500L // 1.5 seconds
}
```

## Security Architecture

### MVP Security Implementation
| Security Layer | Implementation | Rationale |
|----------------|----------------|-----------|
| **Authentication** | Firebase Auth + biometric | Standard, reliable, user-friendly |
| **Local Storage** | SQLCipher encryption | Protect sensitive health data |
| **Network** | Firebase TLS 1.3 | Automatic with Firebase |
| **Access Control** | Firestore security rules | Simple family-based permissions |

### Security Rules Example
```javascript
// Firestore security rules for family data
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /families/{familyId} {
      allow read, write: if request.auth != null 
        && request.auth.uid in resource.data.memberIds
        && resource.data.memberIds.size() <= 10; // Prevent abuse
    }
  }
}
```

## Scalability Patterns

### MVP Scaling Strategy
| User Count | Architecture | Database | Cost |
|-----------|-------------|----------|------|
| **0-1K families** | Current Firebase | Firestore only | <$50/month |
| **1K-10K families** | Add caching | + Redis | <$200/month |
| **10K+ families** | Consider microservices | + Custom API | $500+/month |

### Firebase Limitations & Solutions
- **Read/Write Limits**: Implement client-side caching
- **Query Complexity**: Denormalize data for common queries
- **Vendor Lock-in**: Repository pattern enables migration if needed

## Development Workflow

### MVP Development Process
1. **Write Failing Test**: Start with test that describes desired behavior
2. **Implement Minimum Code**: Make test pass with simplest solution
3. **Refactor if Needed**: Improve code while keeping tests green
4. **User Validation**: Test with real families weekly
5. **Iterate**: Add next most important feature

### Quality Gates
- **All tests pass**: Automated CI/CD requirement
- **Performance benchmarks**: <2s startup on target devices  
- **User acceptance**: Weekly feedback from 5 test families
- **Crash rate**: <1% on production builds

## Migration Strategy

### Post-MVP Evolution
```kotlin
// Example: Adding advanced features while maintaining backward compatibility
interface MedicationRepository {
    // MVP methods
    suspend fun getMedications(parentId: String): List<Medication>
    suspend fun saveMedication(medication: Medication)
    
    // Post-MVP methods (added later)
    suspend fun getMedicationInsights(parentId: String): AdherenceInsights
    suspend fun exportMedicationData(familyId: String): MedicationExport
}
```

### Technical Debt Management
- **Acceptable for MVP**: Simple last-write-wins conflict resolution
- **Fix Post-MVP**: Sophisticated sync conflict handling
- **Monitor**: User complaints about data loss or confusion
- **Iterate**: Based on real user problems, not theoretical issues

## Success Metrics

### Architecture Success Indicators
| Metric | MVP Target | Measurement |
|--------|------------|-------------|
| **App Startup Time** | <2s on mid-range devices | Performance monitoring |
| **Offline Reliability** | 100% core features work | Integration testing |
| **Data Sync Success** | >99% sync operations | Firebase monitoring |
| **Development Velocity** | 2 features per week | Sprint tracking |

### Technical Health Monitoring
- **Build Success Rate**: >95% CI/CD pipeline
- **Test Suite Runtime**: <5 minutes full test execution  
- **Code Coverage**: >85% domain layer, >70% overall
- **Firebase Quota Usage**: <80% of free tier limits

## References
- [System Design](./system-design.md) - Complete MVP architecture
- [Database Schema](./database-schema.md) - Data model and sync strategy
- [Firebase Integration](../api/firebase-integration.md) - Service implementation
- [Lean Startup Methodology](https://theleanstartup.com/principles)