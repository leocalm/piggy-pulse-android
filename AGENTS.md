# AGENTS.md — PiggyPulse Android

## Project Overview

Native Android client for PiggyPulse. Kotlin + Jetpack Compose, targeting API 26+ (Android 8.0).

## Architecture

```
UI (Compose Screens) → ViewModel (StateFlow) → Repository → ApiService (Retrofit)
                              ↑ Hilt DI wires everything
```

Single-activity, Compose-first. 4 bottom tabs: Dashboard, Transactions, Accounts, More.

## Package Structure

```
com.piggypulse.android/
├── core/
│   ├── network/     — ApiService, AuthInterceptor, TokenManager, ApiClient, ApiError
│   ├── model/       — Data classes for all API request/response types
│   ├── repository/  — Repository classes (one per domain)
│   ├── di/          — Hilt modules
│   └── util/        — CurrencyFormatter, DateUtils
├── design/
│   ├── theme/       — PiggyPulseTheme, ColorTheme, ThemeManager, Surfaces, Type
│   └── component/   — Shared composables (PpCard, PpButton, CurrencyText, etc.)
├── feature/
│   ├── auth/        — Login, Register, ForgotPassword, TwoFactor
│   ├── onboarding/  — OnboardingScreen
│   ├── dashboard/   — DashboardScreen + widget composables
│   ├── transactions/ — List, form, filters
│   ├── accounts/    — List, detail, form
│   ├── categories/  — List, form (tabbed)
│   ├── vendors/     — List, form, search
│   ├── subscriptions/ — List, form, cancel
│   ├── periods/     — List, form
│   ├── targets/     — List
│   ├── overlays/    — List, form
│   ├── settings/    — Settings with theme picker
│   └── navigation/  — Routes, MainScaffold, RootNavHost, PeriodSelectorBar
└── app/
    ├── PiggyPulseApp.kt  — @HiltAndroidApp
    └── AppState.kt       — Root ViewModel
```

## Key Patterns

- **Currency display:** Always use `CurrencyText` composable or `CurrencyFormatter`. Never format manually.
- **Card actions:** Always use `PpKebabMenu` (three-dot menu). No hover-only or long-press-only actions.
- **Financial colors:** Never use green/red for good/bad. Use theme accents + destructive (#C4786A).
- **Auth:** Bearer token in EncryptedSharedPreferences. Auto-refresh on 401 via OkHttp Authenticator.
- **API base URL:** `/v2` (debug: `http://10.0.2.2:8000/v2`, release: `https://api.piggy-pulse.com/v2`).
- **Themes:** 6 color themes (Nebula, Sunrise, Neon, Tropical, CandyPop, Moonlit) × dark/light/system.

## Build Commands

```bash
./gradlew assembleDebug       # Build debug APK
./gradlew installDebug        # Build and install on connected device/emulator
./gradlew assembleRelease     # Build release APK
./gradlew test                # Run unit tests
```

## Conventions

- Conventional Commits for PR titles
- Branch protection on main — all changes via PRs
- Squash merge PRs
