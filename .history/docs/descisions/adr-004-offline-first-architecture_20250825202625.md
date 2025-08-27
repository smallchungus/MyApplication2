
---

# ADR-004: Offline-First Architecture

## Status
Accepted

## Context
Our users need reliable access to medication information regardless of connectivity. Many scenarios require offline access:
- Rural areas with poor connectivity
- Hospital buildings with limited cell service
- Emergency situations where internet may be unavailable
- International travel without data plans
- Battery-saving mode with data disabled
- Network outages or service disruptions

Additionally, offline-first architecture provides performance benefits and reduces Firebase costs through caching.

## Decision
We will implement a true offline-first architecture where:
1. **Room database is the single source of truth** for the UI
2. **Firebase Firestore acts as a synchronization layer**, not primary storage
3. **All features work offline** with sync when connected
4. **Conflict resolution uses last-write-wins** with audit logging
5. **Background sync via WorkManager** when connection restored
6. **7-day offline capability** before requiring sync

## Consequences

### Positive
- **100% feature availability offline** - Critical for emergencies
- **Instant UI response** - No network latency for reads
- **Reduced Firebase costs** - Fewer reads through caching
- **Better perceived performance** - Immediate feedback
- **Resilient to outages** - Service disruptions don't affect users
- **Battery efficiency** - Less network usage
- **Predictable behavior** - Same UX online and offline

### Negative
- **Increased complexity** - Sync logic and conflict resolution
- **Larger app size** - Local database and sync queue
- **Data freshness challenges** - Potential for stale data
- **Testing complexity** - Many edge cases for sync
- **Debugging difficulty** - Sync issues hard to reproduce
- **Storage management** - Need to handle device storage limits

### Risks
- **Sync conflicts** - Multiple family members editing offline
- **Data loss** - Failed syncs could lose changes
- **Storage exhaustion** - Too much cached data
- **Stale data** - Medication changes not reflected

## Alternatives Considered

### Online-First with Cache
- ❌ **Rejected**: Features unavailable offline
- ❌ Poor user experience with connectivity issues
- ✅ Simpler implementation
- ✅ Always fresh data

### Selective Offline
- ❌ **Rejected**: Confusing UX with partial functionality
- ❌ Difficult to explain limitations to users
- ✅ Reduced complexity
- ✅ Smaller storage footprint

### Manual Sync
- ❌ **Rejected**: Users forget to sync
- ❌ Risk of data loss
- ✅ User control over data usage
- ✅ Predictable sync timing

## Implementation Strategy

### Data Flow Architecture
```
UI Layer
    ↓ (observes)
ViewModels
    ↓ (reads from)
Repository Layer
    ↓ (single source of truth)
Room Database
    ↓ (background sync)
Sync Manager ←→ Firebase
```

### Sync Strategy
1. **Immediate sync** for critical operations (emergency contacts)
2. **Batched sync** for routine updates (every 15 minutes)
3. **Opportunistic sync** when app foregrounded
4. **Scheduled sync** via WorkManager (every 6 hours)
5. **Force sync** option for users

### Conflict Resolution
- **Last-write-wins** for simple fields
- **Merge strategy** for collections (medications list)
- **Audit log** for all changes with timestamps
- **Manual resolution UI** for critical conflicts

### Storage Management
- **Maximum cache**: 100MB for images
- **Data retention**: 30 days for historical data
- **Cleanup strategy**: LRU eviction for images
- **User control**: Clear cache option in settings

## Success Metrics
- Offline session success rate: >95%
- Sync failure rate: <1%
- Average sync delay: <30 seconds
- Storage usage: <200MB average
- Conflict rate: <0.1% of syncs

## References
- [Offline Patterns for Mobile Apps](https://example.com)
- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [Sync Strategy Design](../architecture/offline-sync.md)

---

