# UI Component Library Documentation

## Problem
MedSync Pro uses custom Jetpack Compose components across multiple screens requiring consistent design patterns, reusable components, and clear implementation guidelines for maintainability and design consistency.

## Design System Foundation

### Material 3 Theme
- **Primary Colors**: Healthcare blue palette for trust and reliability
- **Typography**: Roboto font family with accessibility-optimized scales
- **Spacing**: 8dp grid system for consistent layouts
- **Elevation**: Material 3 surface tones for depth perception

### Component Architecture
```kotlin
// Base component structure
@Composable
fun MedSyncComponent(
    modifier: Modifier = Modifier,
    // Component-specific parameters
) {
    // Implementation following Material 3 guidelines
}
```

## Core UI Components

### 1. Cards and Containers

#### AssignmentCard
**Purpose**: Display daily medication assignments with completion status
```kotlin
@Composable
fun AssignmentCard(
    assignment: Assignment,
    onMarkComplete: (String) -> Unit,
    modifier: Modifier = Modifier
)
```

**Usage Patterns**:
- Assignment list screens
- Dashboard overview
- Family coordination views

**Design Tokens**:
- Corner radius: 12dp
- Padding: 16dp
- Elevation: 2dp

#### MedicationCard
**Purpose**: Display medication information with scheduling details
```kotlin
@Composable
fun MedicationCard(
    medication: Medication,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    modifier: Modifier = Modifier
)
```

**States**:
- Active medication (full opacity)
- Inactive medication (50% opacity)
- Loading state (shimmer effect)

#### CriticalAlertCard
**Purpose**: Emergency alerts and urgent notifications
```kotlin
@Composable
fun CriticalAlertCard(
    alert: Alert,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Visual Design**:
- Red accent color for urgency
- Bold typography
- Prominent action buttons

### 2. Input Components

#### MedicationTimeSelector
**Purpose**: Time picker for medication scheduling
```kotlin
@Composable
fun MedicationTimeSelector(
    selectedTimes: List<LocalTime>,
    onTimesChanged: (List<LocalTime>) -> Unit,
    modifier: Modifier = Modifier
)
```

**Features**:
- Multiple time selection
- 12/24 hour format support
- Validation for reasonable intervals

#### FamilyMemberSelector
**Purpose**: Multi-select component for family member assignment
```kotlin
@Composable
fun FamilyMemberSelector(
    members: List<FamilyMember>,
    selectedMembers: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
)
```

**Interaction Patterns**:
- Checkbox selection
- Avatar display with names
- Role indicators

### 3. Navigation Components

#### BottomNavigationBar
**Purpose**: Primary app navigation with medication-focused icons
```kotlin
@Composable
fun MedSyncBottomNavigation(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
)
```

**Navigation Tabs**:
- Dashboard (home icon)
- Medications (pill icon)
- Assignments (calendar icon)
- Family (people icon)

#### TopAppBar
**Purpose**: Screen-specific app bar with contextual actions
```kotlin
@Composable
fun MedSyncTopAppBar(
    title: String,
    actions: List<AppBarAction> = emptyList(),
    navigationIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
)
```

### 4. Status and Feedback Components

#### ConnectionStatusBanner
**Purpose**: Offline/online status indicator
```kotlin
@Composable
fun ConnectionStatusBanner(
    isOnline: Boolean,
    modifier: Modifier = Modifier
)
```

**States**:
- Online: Hidden by default
- Offline: Yellow banner with sync pending message
- Sync Error: Red banner with retry option

#### AdherenceProgressIndicator
**Purpose**: Visual medication adherence tracking
```kotlin
@Composable
fun AdherenceProgressIndicator(
    adherenceRate: Float, // 0.0 to 1.0
    period: String, // "This Week", "This Month"
    modifier: Modifier = Modifier
)
```

**Visual Elements**:
- Circular progress indicator
- Color coding (green >85%, yellow 70-85%, red <70%)
- Percentage and period labels

### 5. Dialog Components

#### MedicationDetailsDialog
**Purpose**: Full medication information overlay
```kotlin
@Composable
fun MedicationDetailsDialog(
    medication: Medication,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
)
```

#### FamilyInviteDialog
**Purpose**: Family member invitation flow
```kotlin
@Composable
fun FamilyInviteDialog(
    onInvite: (String, Role) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
)
```

## Layout Patterns

### Screen Templates

#### ListScreen Pattern
```kotlin
@Composable
fun <T> ListScreen(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    emptyContent: @Composable () -> Unit,
    floatingActionButton: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
)
```

#### DetailScreen Pattern
```kotlin
@Composable
fun <T> DetailScreen(
    item: T?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
)
```

### Responsive Design
| Screen Size | Columns | Padding | Component Scaling |
|-------------|---------|---------|-------------------|
| Phone | 1 | 16dp | Standard |
| Tablet (7") | 2 | 24dp | 1.1x scale |
| Tablet (10"+) | 3 | 32dp | 1.2x scale |

## Accessibility Guidelines

### Semantic Properties
```kotlin
@Composable
fun AccessibleMedicationCard(medication: Medication) {
    Card(
        modifier = Modifier.semantics {
            contentDescription = "Medication: ${medication.name}, " +
                    "Dosage: ${medication.dosage}, " +
                    "Next dose: ${medication.nextDose}"
            role = Role.Button
        }
    ) {
        // Card content
    }
}
```

### Touch Target Sizes
- **Minimum**: 48dp for all interactive elements
- **Recommended**: 56dp for primary actions
- **Spacing**: 8dp minimum between targets

### Color Accessibility
- **Contrast Ratio**: 4.5:1 minimum for text
- **Color Blind Support**: Icons and shapes, not just colors
- **Focus Indicators**: 2dp outline for keyboard navigation

## Animation Guidelines

### Transition Animations
```kotlin
// Standard screen transitions
val slideTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
)

// Card state changes
val cardElevation by animateDpAsState(
    targetValue = if (isSelected) 8.dp else 2.dp,
    animationSpec = tween(200)
)
```

### Micro-interactions
- **Button Press**: 150ms scale animation (0.95x)
- **List Item Selection**: 200ms elevation change
- **Loading States**: Infinite shimmer or pulse effects

## Theme Customization

### Color Tokens
```kotlin
object MedSyncColors {
    val Primary = Color(0xFF1976D2)        // Healthcare blue
    val PrimaryVariant = Color(0xFF1565C0) // Darker blue
    val Secondary = Color(0xFF4CAF50)      // Success green
    val Error = Color(0xFFD32F2F)          // Alert red
    val Warning = Color(0xFFFF9800)        // Warning orange
    val Surface = Color(0xFFFAFAFA)        // Light background
}
```

### Typography Scale
```kotlin
object MedSyncTypography {
    val displayLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp
    )
    val headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    )
    val bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    )
}
```

## Testing Patterns

### Component Testing
```kotlin
@Test
fun medicationCard_displaysCorrectInformation() {
    composeTestRule.setContent {
        MedicationCard(
            medication = testMedication,
            onEdit = {},
            onToggleActive = {}
        )
    }
    
    composeTestRule
        .onNodeWithText(testMedication.name)
        .assertIsDisplayed()
        
    composeTestRule
        .onNodeWithContentDescription("Edit medication")
        .assertIsDisplayed()
}
```

### Accessibility Testing
```kotlin
@Test
fun medicationCard_hasCorrectSemantics() {
    composeTestRule.setContent {
        MedicationCard(medication = testMedication)
    }
    
    composeTestRule
        .onNodeWithText(testMedication.name)
        .assert(hasClickAction())
        .assert(hasRole(Role.Button))
}
```

## Component Usage Examples

### Dashboard Implementation
```kotlin
@Composable
fun DashboardScreen(
    assignments: List<Assignment>,
    adherenceRate: Float
) {
    LazyColumn {
        item {
            AdherenceProgressIndicator(
                adherenceRate = adherenceRate,
                period = "This Week"
            )
        }
        
        items(assignments) { assignment ->
            AssignmentCard(
                assignment = assignment,
                onMarkComplete = { id -> /* handle completion */ }
            )
        }
    }
}
```

### Form Implementation
```kotlin
@Composable
fun AddMedicationForm() {
    var selectedTimes by remember { mutableStateOf(listOf<LocalTime>()) }
    
    Column {
        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication Name") }
        )
        
        MedicationTimeSelector(
            selectedTimes = selectedTimes,
            onTimesChanged = { selectedTimes = it }
        )
        
        Button(
            onClick = { /* save medication */ }
        ) {
            Text("Save Medication")
        }
    }
}
```

## Best Practices

### Performance Optimization
- Use `remember` for expensive calculations
- Implement `key` parameters in lists
- Minimize recomposition with stable types
- Use `LazyColumn` for large lists

### State Management
- Hoist state to appropriate levels
- Use `StateFlow` for reactive updates
- Implement loading and error states
- Handle configuration changes gracefully

### Code Organization
```kotlin
// Component file structure
composables/
├── cards/
│   ├── AssignmentCard.kt
│   ├── MedicationCard.kt
│   └── CriticalAlertCard.kt
├── inputs/
│   ├── MedicationTimeSelector.kt
│   └── FamilyMemberSelector.kt
└── navigation/
    ├── BottomNavigation.kt
    └── TopAppBar.kt
```

## References
- [Material Design 3](https://m3.material.io/)
- [Jetpack Compose Guidelines](https://developer.android.com/jetpack/compose)
- [Accessibility Best Practices](https://developer.android.com/guide/topics/ui/accessibility)