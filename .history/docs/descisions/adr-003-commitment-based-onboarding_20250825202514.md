

# ADR-003: Commitment-Based Onboarding

## Status
Accepted

## Context
Traditional medication reminder apps suffer from catastrophic retention rates (<10% after 90 days) despite high initial signup rates (60-80%). They optimize for frictionless onboarding, resulting in users who haven't invested emotionally or temporally in the solution.

Research shows:
- Users who invest 30+ minutes in setup are 8x more likely to remain active
- Family involvement increases retention by 3.5x
- Sunk cost fallacy can be leveraged for positive behavior change
- Quality of users matters more than quantity for network effects

## Decision
We will implement an intentionally intensive 30-45 minute onboarding process that:
1. Requires significant time investment (minimum 30 minutes)
2. Involves multiple family members during setup
3. Includes educational content about medication adherence
4. Captures detailed parent health information
5. Ends with a "commitment contract" psychological tool
6. Allows multi-session completion but tracks progress

We expect only 15-25% completion rate but >70% long-term retention.

## Consequences

### Positive
- **Higher quality users** - Self-selection for motivated individuals
- **Better retention** - 7-10x industry average expected
- **Psychological commitment** - Sunk cost creates stickiness
- **Family buy-in** - Social accountability from start
- **Complete data** - Thorough profiles enable better features
- **Lower support costs** - Committed users need less hand-holding
- **Word-of-mouth growth** - Highly engaged users more likely to refer

### Negative
- **Lower vanity metrics** - Fewer total users initially
- **Slower growth** - Can't claim "1M downloads"
- **A/B testing challenges** - Smaller sample sizes
- **Investor skepticism** - Unconventional metrics
- **Marketing difficulty** - Can't promise "quick and easy"
- **Risk of frustration** - Some good users may abandon

### Risks
- **Onboarding too intensive** - Monitor completion rate, adjust if <10%
- **Wrong friction points** - Ensure friction creates value, not frustration
- **Competitor copying** - First-mover advantage critical
- **User expectations** - Clear value communication essential

## Alternatives Considered

### Traditional Easy Onboarding
- ❌ **Rejected**: Proven to fail in this market
- ❌ Creates no commitment or investment
- ✅ Higher initial numbers
- ✅ Industry standard approach

### Freemium Model
- ❌ **Rejected**: Free users unlikely to convert
- ❌ Doesn't solve commitment problem
- ✅ Larger user base for network effects
- ✅ Potential for upselling

### Paid App Model
- ❌ **Rejected**: Price alone doesn't ensure commitment
- ❌ Barrier to family member participation
- ✅ Revenue from day one
- ✅ Self-selecting for serious users

### Progressive Onboarding
- ❌ **Rejected**: Allows users to avoid commitment
- ❌ Partial setup leads to poor experience
- ✅ Less intimidating initially
- ✅ Higher completion rates

## Implementation Details

### Onboarding Stages
1. **Introduction Video** (3 min) - Why medication adherence matters
2. **Commitment Assessment** (5 min) - Quiz to gauge readiness
3. **Parent Profile** (10 min) - Detailed health information
4. **Medication Inventory** (10 min) - Current medications with photos
5. **Family Coordination** (7 min) - Invite and assign roles
6. **Emergency Preparation** (5 min) - Critical information setup
7. **Commitment Contract** (5 min) - Psychological commitment tool

### Commitment Scoring Algorithm
```
Score = (Time Invested × 0.25) + 
        (Profile Completeness × 0.30) +
        (Family Members × 0.20) +
        (Detail Quality × 0.15) +
        (Contract Strength × 0.10)
```

### Success Metrics
- Onboarding completion: 15-25%
- Average time invested: 35-45 minutes
- Multi-session users: <30%
- 30-day retention post-onboarding: >80%
- Family members invited average: 2.5

## References
- [Behavioral Psychology Research](../research/commitment-psychology.md)
- [Retention Analysis](../research/retention-study.md)
- [Onboarding Flow Designs](../design/onboarding-flow.md)

---

