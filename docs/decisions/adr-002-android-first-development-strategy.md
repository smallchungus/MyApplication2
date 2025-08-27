# ADR-002: Android-First Development Strategy

## Status
Accepted

## Problem (2-3 sentences)
Limited resources as solo developer require platform prioritization for medication coordination app. Target demographic (adults 45-65 caring for elderly parents) shows 70% Android usage with longer device retention cycles.

## Decision
- **Choice**: Android-native first using Kotlin and Jetpack Compose
- **Why**: 70% of target market uses Android, faster single-platform MVP delivery
- **Trade-off**: Miss 30% of potential iOS users initially

### Implementation
- Target Android 7.0+ (API 24) for wide device compatibility
- Use Jetpack Compose for modern declarative UI
- Begin iOS development after achieving product-market fit (3-6 months)

## Alternatives
| Option | Pro | Con | Why Rejected |
|--------|-----|-----|-------------|
| Flutter | Single codebase for both platforms | Learning curve for Dart, larger app size | Development complexity |
| React Native | Large community, web skills transfer | JavaScript bridge overhead, debugging complexity | Performance concerns |
| KMM | Share business logic, Kotlin consistency | Still evolving, limited UI sharing | Maturity concerns |
| PWA | Single codebase, no app store approval | Limited offline capabilities on iOS | Platform limitations |

## Success Metrics
- **Timeline**: Launch Android MVP within 8 weeks
- **User Acquisition**: 1000+ active users before iOS development
- **Quality**: >4.5 star rating on Play Store
- **Stability**: <1% crash rate on Android

## Test Strategy
- **Device Testing**: Focus on mid-range Android devices ($200-400 price point)
- **Performance**: Optimize for older devices with limited resources
- **Market Validation**: Gather iOS user feedback for future development priorities