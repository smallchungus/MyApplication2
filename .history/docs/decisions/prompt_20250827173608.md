# Documentation Guidelines for Claude Code

When writing technical documentation, follow these constraints:

## Length Limits
- **Architecture docs**: 200-300 lines max
- **ADRs**: 50-80 lines max  
- **API specs**: 150-200 lines max
- **Feature docs**: 100-150 lines max
- **README files**: 200-250 lines max

## Structure Requirements
1. **Use bullet points** over paragraphs where possible
2. **Code examples**: Max 10-15 lines per example, only if essential
3. **Focus on decisions and rationale**, not implementation details
4. **Tables** for comparisons (more scannable than text)
5. **Links to detailed docs** instead of inline explanations

## FAANG Standards to Maintain
- Clear problem statement (2-3 sentences)
- Decision with rationale (bullet points)
- Trade-offs acknowledged (table format)
- Success metrics defined (simple list)
- Test strategy mentioned (not detailed)

## Format Example
```markdown
# Feature: [Name]

## Problem (2-3 sentences max)
What we're solving and why.

## Decision
- **Choice**: [What we chose]
- **Why**: [1 sentence rationale]
- **Trade-off**: [What we accept]

## Alternatives (table)
| Option | Pro | Con | Why Rejected |
|--------|-----|-----|--------------|
| A | ... | ... | ... |

## Success Metrics
- Metric 1: Target
- Metric 2: Target

## Test Strategy
- Unit: 80% coverage
- Integration: Critical paths
- [Link to detailed test plan]