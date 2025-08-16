# ParentCare - Family Coordination App

A comprehensive Android application built with **Kotlin**, **Jetpack Compose**, and **Firebase** for coordinating medication care among family members.

## 🎯 **Project Overview**

ParentCare is designed for adult children (ages 45-65) who need to coordinate medication care for elderly parents. Unlike traditional medication apps that require seniors to use technology, this dashboard is built for adult children who want to ensure their parents' medication adherence.

## 🏗️ **Architecture**

### **Technology Stack**

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM with Repository Pattern
- **Dependency Injection**: Hilt
- **Backend**: Firebase (Authentication, Firestore, Messaging)
- **Testing**: JUnit 4, Mockito, Compose UI Testing

### **Key Components**

```
app/
├── src/main/java/com/example/myapplication/
│   ├── auth/                    # Authentication system
│   │   ├── AuthRepository.kt   # Firebase auth operations
│   │   └── LoginScreen.kt      # Login/registration UI
│   ├── di/                     # Dependency injection
│   │   └── FirebaseModule.kt   # Firebase service providers
│   ├── MainActivity.kt         # App entry point
│   ├── FamilyData.kt           # Data models
│   ├── TodayScreen.kt          # Family dashboard (legacy)
│   └── ParentCareApplication.kt # Hilt application class
└── src/test/java/              # Unit tests
    └── auth/
        └── AuthRepositoryTest.kt
```

## 🔐 **Firebase Authentication System**

### **Features**

- **Email/Password Authentication**: Secure user registration and login
- **Real-time State Management**: Observable authentication state with StateFlow
- **User Profile Management**: Firestore integration for user data
- **Error Handling**: User-friendly error messages for common issues
- **Session Management**: Automatic token refresh and state persistence

### **Security Features**

- Encrypted authentication with Firebase best practices
- User data encrypted in transit and at rest
- Family-based access control foundation
- Automatic session management and token refresh

### **Authentication Flow**

```
1. User opens app → Loading state
2. Check authentication → Unauthenticated → LoginScreen
3. User signs in → Authenticated → Check family groups
4. No family groups → FamilySetupScreen (to be built)
5. Has family groups → FamilyDashboardScreen
```

## 🚀 **Getting Started**

### **Prerequisites**

- Android Studio Arctic Fox or later
- Android SDK 24+
- Firebase project with Authentication and Firestore enabled

### **Setup Instructions**

#### **1. Firebase Configuration**

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication (Email/Password)
3. Enable Firestore Database
4. Download `google-services.json` and place it in `app/` directory
5. Replace the placeholder `google-services.json` with your actual configuration

#### **2. Build and Run**

```bash
# Clone the repository
git clone <repository-url>
cd MyApplication2

# Build the project
./gradlew assembleDebug

# Run tests
./gradlew test

# Install on device/emulator
./gradlew installDebug
```

### **Dependencies**

The app automatically manages Firebase dependencies through the Firebase BoM:

- Firebase Authentication
- Firebase Firestore
- Firebase Messaging
- Firebase UI Auth

## 🧪 **Testing**

### **Test Coverage**

- **Unit Tests**: AuthRepository with mocked Firebase services
- **UI Tests**: LoginScreen interaction flows
- **Integration Tests**: Firebase service integration

### **Running Tests**

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AuthRepositoryTest

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

### **Test Strategy**

- Mock Firebase services for isolated testing
- Test all authentication states and transitions
- Verify error handling for common failure scenarios
- Test coroutine-based async operations
- Validate user data persistence in Firestore

## 🔧 **Development**

### **Adding New Features**

1. **Data Layer**: Create models in appropriate package
2. **Repository Layer**: Implement business logic with Firebase
3. **UI Layer**: Create Compose screens with proper state management
4. **Testing**: Add comprehensive tests for new functionality

### **Code Style**

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comprehensive KDoc documentation
- Implement proper error handling
- Follow Material Design 3 guidelines

### **Architecture Principles**

- **Single Responsibility**: Each class has one clear purpose
- **Dependency Injection**: Use Hilt for service management
- **Repository Pattern**: Abstract data operations
- **MVVM**: Clear separation of concerns
- **Reactive Programming**: Use StateFlow for state management

## 📱 **User Experience**

### **Design Principles**

- **Family-First**: Built for adult children as primary users
- **Assignment-Based**: Shows who's responsible for checking on parents
- **Status-Focused**: Immediate visibility into medication adherence
- **Emergency-Ready**: Quick access to critical information

### **Accessibility Features**

- Large touch targets for senior users
- High contrast color schemes
- Clear typography hierarchy
- Screen reader support
- WCAG 2.1 AA compliance

## 🔮 **Future Enhancements**

### **Phase 2: Family Management**

- Family group creation and management
- Member invitation system
- Role-based permissions
- Family chat and notifications

### **Phase 3: Medication Tracking**

- Medication schedule management
- Dosage tracking and reminders
- Health provider integration
- Medication adherence analytics

### **Phase 4: Advanced Features**

- Push notifications for missed medications
- Emergency contact management
- Health record integration
- Multi-language support

## 🐛 **Troubleshooting**

### **Common Issues**

#### **Build Errors**

- **Missing google-services.json**: Ensure Firebase configuration file is in `app/` directory
- **Hilt compilation errors**: Clean and rebuild project
- **Firebase dependency issues**: Check internet connection and sync project

#### **Runtime Errors**

- **Authentication failures**: Verify Firebase project configuration
- **Network errors**: Check device internet connection
- **Permission errors**: Ensure app has required permissions

### **Debug Mode**

Enable debug logging in `build.gradle.kts`:

```kotlin
buildTypes {
    debug {
        isDebuggable = true
        isMinifyEnabled = false
    }
}
```

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### **Development Guidelines**

- Follow existing code style and architecture
- Add comprehensive tests for new features
- Update documentation for API changes
- Ensure all tests pass before submitting PR

## 📞 **Support**

For support and questions:

- Create an issue in the GitHub repository
- Check the Firebase documentation
- Review the test cases for usage examples

## 🙏 **Acknowledgments**

- **Firebase Team**: For excellent backend services
- **Jetpack Compose Team**: For modern Android UI framework
- **Material Design Team**: For design system and guidelines
- **Android Community**: For continuous improvement and feedback

---

**Built with ❤️ for families coordinating care**
