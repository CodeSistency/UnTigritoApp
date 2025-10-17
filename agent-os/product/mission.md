# UnTigritoApp — Mission & Strategy

## Purpose
UnTigritoApp is the reliable mobile marketplace connecting Venezuelan households and small businesses with verified, skilled local professionals for immediate and reliable informal jobs ("tigritos").

## Vision
Become the trusted digital backbone for Venezuela's informal service economy, driving transparency, reliability, and professional empowerment nationwide.

## Mission
Provide a secure, efficient, and transparent Android platform that empowers local professionals to build a digital reputation and sustainable income, while giving clients easy access to verified, fairly priced, quality local services.

## Target Users
- Clients: Households and small business owners in urban centers needing trustworthy maintenance, repair, or specialty services.
- Professionals ("Tigritos"): Skilled individuals (electricians, mechanics, cleaners, technicians) seeking visibility, consistent job flow, secure communication, and reputation building.
- Secondary: Local hardware stores and material suppliers (future integrations).

## Core Problems to Solve
1. Trust and Quality: Uncertainty about work quality, fair pricing, and safety.
2. Income and Visibility: Inconsistent job flow; lack of portable professional history/portfolio.
3. Payments and Logistics: Friction in scheduling, coordination, and confirmation of payments.

## Strategic Principles
- Trust First: Mandate public, bidirectional ratings/reviews post‑completion. Encourage verification badges.
- Professional Empowerment: Treat the platform as a career builder, not a classifieds board.
- Android‑First Execution: Ship a focused, high‑quality Android experience using Kotlin + Jetpack Compose.
- Clean Architecture: Maintain clear separation of concerns for scalability and maintainability.
- Pragmatic Payments: Start simple (off‑platform confirmation) and evolve to escrow once adoption is proven.

## Value Propositions
- For Clients: Verified professionals, transparent ratings, predictable pricing signals, and secure communication.
- For Professionals: Digital storefront and reputation, job notifications, scheduling tools, and potential access to recurring demand.

## Success Metrics (North Stars)
- Weekly Active Professionals (WAP)
- Client Activation Rate (first hire within 30 days)
- Professional Month‑over‑Month Retention
- Job Completion and Rated Rate

## Key Risks and Mitigations
- Going Offline: Users bypass platform after first match.
  - Mitigation: Early ratings/reviews, visibility tied to on‑platform activity, future escrow and benefits.
- Complex Payment Landscape:
  - Mitigation: Defer escrow to later phases; start with verified external payments and clear guidance.

## Scope for V1 (Android MVP)
1. Core Auth for Clients and Professionals.
2. Job Posting with categories, photos, budget, and location.
3. Professional Profiles with services, skills, availability, and service range.
4. In‑App Communication between matched users.
5. Ratings & Reviews as soon as feasible (V1.1 if not day‑one).

## Roadmap Link
See `agent-os/product/roadmap.md` for phased delivery details and dependencies.


