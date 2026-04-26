# SubTrack — Architecture

## Clean Architecture: capas

```mermaid
graph TD
    subgraph Presentation ["🖥️ Presentation (feature/)"]
        Screen["Screen\n(Composable)"]
        VM["ViewModel\n(StateFlow)"]
        UiState["UiState\n(data class)"]
        Screen --> VM
        VM --> UiState
        UiState --> Screen
    end

    subgraph Domain ["📐 Domain (pure Kotlin)"]
        UC["UseCase"]
        Repo["Repository\n(interface)"]
        Model["Model\n(data class)"]
        UC --> Repo
        UC --> Model
    end

    subgraph Data ["💾 Data"]
        MockRepo["MockRepository\n(Fase 1)"]
        FirebaseRepo["FirebaseRepository\n(Fase 2)"]
        RoomRepo["RoomRepository\n(Fase 2)"]
    end

    VM -->|"invoca"| UC
    MockRepo -.->|"implementa"| Repo
    FirebaseRepo -.->|"implementa"| Repo
    RoomRepo -.->|"implementa"| Repo

    style Domain fill:#1a1a2e,stroke:#6c63ff,color:#fff
    style Presentation fill:#0f3460,stroke:#4cc9f0,color:#fff
    style Data fill:#16213e,stroke:#4ade80,color:#fff
```

> **Regla de dependencia**: las flechas solo apuntan hacia adentro. `domain` no importa nada de Android ni de `data`.

---

## Flujo de datos (unidireccional)

```mermaid
sequenceDiagram
    participant U as Usuario
    participant S as Screen
    participant VM as ViewModel
    participant UC as UseCase
    participant R as Repository
    participant DS as DataSource

    U->>S: Interacción (click, scroll)
    S->>VM: onEvent(UiEvent)
    VM->>UC: invoke(params)
    UC->>R: getData()
    R->>DS: fetch()
    DS-->>R: Result<T>
    R-->>UC: Result<T>
    UC-->>VM: Result<T>
    VM->>VM: update UiState
    VM-->>S: StateFlow<UiState>
    S-->>U: Recomposición
```

---

## Navegación

```mermaid
stateDiagram-v2
    [*] --> Loading

    Loading --> ShowOnboarding : onboarding no completado
    Loading --> ShowMain : onboarding completado

    ShowOnboarding --> OnboardingFlow
    OnboardingFlow --> Auth
    Auth --> ShowMain : login exitoso

    ShowMain --> Dashboard
    ShowMain --> SubscriptionList
    ShowMain --> People
    ShowMain --> Profile

    Dashboard --> CreateSubscription
    Dashboard --> SubscriptionDetail

    SubscriptionList --> SubscriptionDetail
    SubscriptionList --> CreateSubscription

    SubscriptionDetail --> AdminView : rol == ADMIN
    SubscriptionDetail --> MemberView : rol == MEMBER

    Profile --> AliasEdit
    Profile --> Templates
    Templates --> EditTemplate
    Profile --> Referral
    Profile --> Debug
```

---

## Árbol de pantallas y rutas

```mermaid
graph LR
    App["SubTrackApp"]

    App --> OB["Onboarding\n/onboarding"]
    App --> Auth["Auth\n/auth"]

    App --> BN["Bottom Nav"]
    BN --> DB["Dashboard\n/dashboard"]
    BN --> SL["SubscriptionList\n/subscriptions"]
    BN --> PP["People\n/people"]
    BN --> PF["Profile\n/profile"]

    DB --> CS["CreateSubscription\n/create?startShared=…"]
    DB --> SD["SubscriptionDetail\n/detail/{id}"]
    SL --> SD
    SL --> CS

    SD --> ADM["AdminDetailScreen"]
    SD --> MEM["MemberDetailScreen"]

    PF --> AE["AliasEdit\n/profile/alias"]
    PF --> TM["Templates\n/profile/templates"]
    TM --> ET["EditTemplate\n/profile/templates/{id}"]
    PF --> RF["Referral\n/profile/referral"]
    PF --> DG["Debug\n/profile/debug"]
```

---

## Módulos Hilt (DI)

```mermaid
graph TD
    subgraph AppModule ["AppModule"]
        UP["UserPreferences\n(DataStore)"]
    end

    subgraph RepositoryModule ["RepositoryModule"]
        SR["SubscriptionRepository → MockSubscriptionRepository"]
        MR["MemberRepository → MockMemberRepository"]
        PR["PaymentRepository → MockPaymentRepository"]
        TR["TemplateRepository → MockTemplateRepository"]
        UR["UserRepository → MockUserRepository"]
    end

    subgraph ViewModels ["ViewModels (HiltViewModel)"]
        APVM["AppStateViewModel"]
        DVM["DashboardViewModel"]
        SLVM["SubscriptionListViewModel"]
        SDVM["SubscriptionDetailViewModel"]
        CSVM["CreateSubscriptionViewModel"]
        PFM["ProfileViewModel"]
    end

    RepositoryModule -->|"injecta UC"| ViewModels
    AppModule -->|"injecta"| APVM
```

---

## Capas del Design System

```mermaid
graph BT
    subgraph Tokens ["Tokens (DESIGN_TOKENS.md)"]
        Colors["Colors\n(dark-first palette)"]
        Type["Typography\n(Space Grotesk · Geist · JetBrains Mono)"]
        Spacing["Spacing\n(8pt grid)"]
        Radius["Radius"]
        Elevation["Elevation"]
        Motion["Motion\n(spring curves)"]
    end

    subgraph Theme ["core/designsystem/theme/"]
        MaterialTheme["SubTrackTheme\n(MaterialTheme wrapper)"]
    end

    subgraph Components ["core/designsystem/components/"]
        Buttons["Buttons"]
        Cards["Cards"]
        Inputs["Inputs"]
        Badges["Badges"]
        Avatars["Avatars"]
        Indicators["Progress / Payment indicators"]
    end

    subgraph Features ["feature/*/"]
        Screens["Screens & Composables"]
    end

    Tokens --> Theme
    Theme --> Components
    Components --> Screens
```

---

## Roadmap de integración (Fase 2)

```mermaid
gantt
    title SubTrack — Backend Integration
    dateFormat YYYY-MM
    section Fase 1 (actual)
        Scaffolding & Design System   :done, 2024-10, 1M
        UI completa con MockData      :done, 2024-11, 2M
    section Fase 2
        Firebase Auth                 :2025-01, 1M
        Firestore CRUD                :2025-02, 2M
        WhatsApp Templates + Cobranza :2025-04, 1M
        FCM Notificaciones            :2025-05, 1M
        Pro/Freemium + Referidos      :2025-06, 1M
```
