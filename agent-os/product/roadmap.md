# UnTigritoApp — Phased Roadmap (Android‑first)

## Phase 0 — Validation & Foundations (Weeks 1‑4)
- Define categories and taxonomy for services.
- Conduct 10‑15 interviews (clients and professionals) in 2 urban centers.
- Technical foundations: Kotlin, Jetpack Compose, Clean Architecture skeleton; CI setup.
- Success criteria: Clear feature prioritization and validated problem statements.

## Phase 1 — V1 MVP (Weeks 5‑16)
Scope (Must‑Have):
- Client/Professional authentication and role separation.
- Job posting: category selection, description, photos, budget, location.
- Professional profile: services, skills, availability, service range; push notifications for relevant jobs.
- In‑app messaging for matched client‑professional pairs.

Non‑functional:
- Analytics events for activation, job post, match, conversation, completion.
- Basic moderation/reporting hooks.

Release Target: Private pilot in 1 city.
KPI Targets: First 100 weekly active professionals; 30% client activation.

## Phase 1.1 — Trust & Quality (Weeks 17‑20)
- Mandatory ratings & reviews after job completion.
- Professional verification badge (basic ID/background check status).
- Profile quality nudges (photo, description completeness, response time prompts).

## Phase 2 — Scale & Reliability (Weeks 21‑28)
- Matching quality improvements (better job notifications and filters).
- Operational dashboards (reporting, dispute intake workflow).
- Performance hardening, offline resilience for messaging.

## Phase 3 — Payments (Pilot) (Weeks 29‑36)
- Simple payment confirmation flow (external transfer confirmation with proof upload).
- Experiment: escrow prototype behind feature flag for a subset of jobs.
- Partnerships exploration for insurance/warranty.

## Phase 4 — Expansion (Weeks 37‑40)
- Expand to additional cities.
- Growth features: referral incentives, promo codes.

## Dependencies & Risks
- Payment complexity: Gate escrow behind flags; legal review.
- Supply/demand balance: Seed professionals before broad client marketing.
- Going‑offline risk: Make ratings central to visibility and search ranking.

## Release Management
- Feature flags for risky features (payments, verification providers).
- Staged rollouts via internal testing, closed testing, then production tracks.


