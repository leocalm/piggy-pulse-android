# PiggyPulse Android

Native Android client for [PiggyPulse](https://piggy-pulse.com) — a personal finance app with custom budget periods, spending tracking, and financial health insights.

Built with Kotlin and Jetpack Compose, targeting Android 8.0+ (API 26).

## Features

### Core
- **Dashboard** — Net position, current period progress, cash flow, recent transactions, subscriptions summary, spending trend charts, top vendors, variable categories
- **Transactions** — Infinite scroll with date grouping, direction filters, multi-select filters (account/category/vendor), create/edit/delete
- **Budget Periods** — Create and manage custom-length budget periods

### Structure Management
- **Accounts** — Grouped by type (Checking/Savings/CreditCard/Wallet/Allowance), net position, detail view, archive
- **Categories** — Income/Expense/Transfer tabs, emoji icons, archive
- **Vendors** — Searchable list with spend stats, merge support
- **Subscriptions** — Active/Paused/Cancelled tabs, upcoming charges, cancel flow
- **Overlays** — Temporary spending plans with cap tracking
- **Targets** — Per-period category budget allocation

### Settings & Customization
- **6 Color Themes** — Nebula, Sunrise, Neon, Tropical, CandyPop, Moonlit
- **Dark/Light/System mode**
- **Profile management**

### Authentication
- Email/password login with Bearer token auth
- Two-factor authentication (2FA) support
- Auto-refresh tokens
- Encrypted token storage (Android Keystore)
- Register and forgot password flows

### Internationalization
- English and Portuguese

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **DI:** Hilt
- **Networking:** Retrofit + OkHttp + Kotlin Serialization
- **Charts:** Vico
- **Navigation:** Jetpack Navigation Compose (type-safe)
- **Security:** EncryptedSharedPreferences

## License

AGPLv3 — see [LICENSE](LICENSE) for details.
