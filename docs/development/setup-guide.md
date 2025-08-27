# Development Setup Guide

## Problem
New developers need comprehensive setup instructions for MedSync Pro's Android development environment including tools, Firebase configuration, and project-specific requirements.

## Prerequisites

### System Requirements
| Component | Minimum | Recommended |
|-----------|---------|-------------|
| OS | Windows 10, macOS 10.14, Ubuntu 18.04 | Latest stable versions |
| RAM | 8GB | 16GB+ |
| Storage | 4GB free space | 10GB+ SSD |
| Java | JDK 11 | JDK 17 |

### Required Tools
- **Android Studio**: Latest stable version (Flamingo or newer)
- **Git**: Version 2.20+
- **Firebase CLI**: For local testing and deployment

## Installation Steps

### 1. Android Studio Setup
```bash
# Download from: https://developer.android.com/studio
# Install with default settings

# Verify installation
android studio --version
```

**Required SDK Components**:
- Android SDK Platform 24 (minimum target)
- Android SDK Platform 34 (compile target)
- Android SDK Build-Tools 34.0.0
- Google Play Services
- Firebase SDK

### 2. Project Clone and Configuration
```bash
# Clone repository
git clone https://github.com/your-org/medsync-pro.git
cd medsync-pro

# Switch to develop branch
git checkout develop

# Open project in Android Studio
# File > Open > Select project folder
```

### 3. Firebase Configuration

#### Development Environment
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Select development project
firebase use medsync-dev

# Download google-services.json
firebase setup:android
```

**File Placement**:
```
app/
├── src/
│   ├── debug/
│   │   └── google-services.json    # Development config
│   └── release/
│       └── google-services.json    # Production config
```

### 4. Local Firebase Emulator Setup
```bash
# Install emulator components
firebase init emulators

# Select services:
# - Authentication
# - Firestore
# - Cloud Functions
# - Hosting

# Start emulators for development
firebase emulators:start
```

**Emulator Ports**:
- Authentication: 9099
- Firestore: 8080
- Functions: 5001
- Hosting: 5000

## Project Structure Overview

### Key Directories
```
app/
├── src/main/kotlin/com/medsync/
│   ├── data/                    # Repository implementations
│   ├── domain/                  # Business logic and use cases
│   ├── presentation/            # UI components and ViewModels
│   └── di/                      # Dependency injection modules
├── src/test/                    # Unit tests
├── src/androidTest/             # Instrumented tests
└── build.gradle.kts             # App-level build configuration
```

### Configuration Files
| File | Purpose |
|------|---------|
| `build.gradle.kts` (project) | Project-wide build settings |
| `build.gradle.kts` (app) | App module dependencies and config |
| `google-services.json` | Firebase configuration |
| `gradle.properties` | Build optimization settings |

## Environment Configuration

### Build Variants
```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "MedSync Dev")
        }
        
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            resValue("string", "app_name", "MedSync Pro")
        }
    }
}
```

### API Configuration
```kotlin
// BuildConfig values for different environments
object ApiConfig {
    const val BASE_URL = BuildConfig.API_BASE_URL
    const val API_KEY = BuildConfig.API_KEY
    const val FIREBASE_PROJECT = BuildConfig.FIREBASE_PROJECT_ID
}
```

## Development Workflow

### Git Workflow
```bash
# Create feature branch
git checkout -b feature/medication-reminders

# Make changes and commit
git add .
git commit -m "feat(reminders): implement medication reminder notifications"

# Push and create PR
git push origin feature/medication-reminders
```

### Code Style
- **Kotlin Style Guide**: Follow official Android Kotlin style guide
- **Formatting**: Use Android Studio default formatter (Ctrl+Alt+L)
- **Linting**: ktlint integration for consistent code style

### Pre-commit Hooks
```bash
# Install pre-commit hooks
./scripts/setup-hooks.sh

# Hooks will run automatically:
# - Code formatting check
# - Unit test execution
# - Lint checks
# - Import organization
```

## Testing Setup

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run with coverage report
./gradlew testDebugUnitTestCoverage

# View coverage report
open build/reports/coverage/test/debug/index.html
```

### Instrumented Tests
```bash
# Start Android emulator first
# Recommended: Pixel 6 API 34

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.medsync.MedicationRepositoryTest
```

### Firebase Emulator Tests
```kotlin
// Test with Firebase emulator
@Before
fun setUp() {
    // Configure Firebase emulator
    Firebase.auth.useEmulator("10.0.2.2", 9099)
    Firebase.firestore.useEmulator("10.0.2.2", 8080)
}
```

## Build and Run

### Debug Build
```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install in one command
./gradlew installDebug
```

### Running from IDE
1. Select device/emulator from dropdown
2. Click Run button or press Shift+F10
3. App will install and launch automatically

### Debugging Tools
- **Layout Inspector**: View UI hierarchy in real-time
- **Database Inspector**: Inspect Room database contents
- **Network Inspector**: Monitor Firebase API calls
- **Logcat**: View app logs and system messages

## Common Issues and Solutions

### Build Issues
**Issue**: "Failed to resolve Firebase dependencies"
**Solution**:
```bash
# Clear Gradle cache
./gradlew clean
rm -rf .gradle/
./gradlew build
```

**Issue**: "Google Services plugin not found"
**Solution**:
```kotlin
// Ensure in project build.gradle.kts
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}

// And in app build.gradle.kts
plugins {
    id("com.google.gms.google-services")
}
```

### Emulator Issues
**Issue**: "Emulator won't start"
**Solution**:
1. Enable Virtualization in BIOS
2. Install Intel HAXM or enable Hyper-V
3. Allocate sufficient RAM (4GB+) to emulator

**Issue**: "Firebase emulator connection failed"
**Solution**:
```bash
# Use 10.0.2.2 instead of localhost in emulator
Firebase.auth.useEmulator("10.0.2.2", 9099)
```

## Performance Optimization

### Gradle Build Performance
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
android.enableJetifier=true
```

### IDE Performance
- **Memory Settings**: Increase Android Studio heap size to 4GB+
- **Power Save Mode**: Disable when actively developing
- **Unused Plugins**: Disable unnecessary IDE plugins
- **File Indexing**: Exclude build directories from indexing

## Team Collaboration

### Code Review Checklist
- [ ] Code follows project style guide
- [ ] Unit tests added for new functionality
- [ ] UI tests updated for screen changes
- [ ] Documentation updated
- [ ] No hardcoded secrets or API keys
- [ ] Performance impact considered

### Branch Protection
- Main branch requires PR approval
- All tests must pass before merge
- Up-to-date with target branch
- Linear commit history preferred

## Deployment Preparation

### Release Build
```bash
# Create release build
./gradlew assembleRelease

# Generate signed AAB for Play Store
./gradlew bundleRelease
```

### Keystore Management
```bash
# Generate release keystore (one-time)
keytool -genkeypair -v -keystore release.keystore \
  -alias medsync -keyalg RSA -keysize 2048 -validity 10000

# Store keystore securely (not in version control)
# Add signing config to app/build.gradle.kts
```

## Resources

### Documentation
- [Project Architecture](../architecture/README.md)
- [API Documentation](../api/firebase-integration.md)
- [UI Components](../ui/component-library.md)
- [Testing Strategy](../testing/testing-strategy.md)

### External References
- [Android Developer Guide](https://developer.android.com/guide)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)

### Support Channels
- **GitHub Issues**: Technical problems and bug reports
- **Team Chat**: Daily development questions
- **Code Review**: Pull request feedback and discussions
- **Architecture Decisions**: ADR documentation process