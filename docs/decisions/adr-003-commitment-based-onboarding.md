# ADR-003: Commitment-Based Onboarding

## Status
Accepted

## Problem (2-3 sentences)
Traditional medication reminder apps have <10% retention after 90 days despite frictionless onboarding. Users who invest time and emotional energy in setup show 8x higher retention rates and family involvement increases retention by 3.5x.

## Decision
- **Choice**: Intensive 30-45 minute onboarding with family involvement
- **Why**: Quality over quantity - invested users have dramatically better retention
- **Trade-off**: Accept 15-25% completion rate for >70% long-term retention

### Onboarding Components
- Detailed parent health profile setup
- Multi-family member involvement and role assignment
- Educational content about medication adherence
- Psychological commitment contract tool

## Alternatives
| Option | Pro | Con | Why Rejected |
|--------|-----|-----|-------------|
| Easy Onboarding | Higher initial numbers, industry standard | Proven to fail in this market | Low retention rates |
| Freemium Model | Larger user base for network effects | Free users unlikely to convert | No commitment mechanism |
| Paid App | Revenue from day one, serious users | Price alone doesn't ensure commitment | Barrier to family participation |
| Progressive Setup | Less intimidating, higher completion | Partial setup leads to poor experience | Avoids real commitment |

## Success Metrics
- **Completion Rate**: 15-25% of starts complete full onboarding
- **Time Investment**: 35-45 minute average completion time
- **Family Engagement**: 2.5 average family members per setup
- **Retention**: >80% retention at 30 days post-onboarding

## Test Strategy
- **A/B Testing**: Monitor completion rates, adjust if <10%
- **User Feedback**: Track frustration vs commitment indicators
- **Behavioral Analysis**: Measure correlation between setup time and retention
- **Family Dynamics**: Test different family involvement strategies