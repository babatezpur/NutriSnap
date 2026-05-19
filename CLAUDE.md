# NutriSnap — Android App

## Overview

A native Android app that lets users snap a photo of any food/meal and get
AI-generated nutrition estimates. The user provides context (food name, quantity,
optional notes) before the AI call, making estimates significantly more accurate.
All data is stored locally — no backend server, no auth.

> Point. Snap. Add context. Get nutrition info.

---

## Goals

- Learn CameraX + ML Kit integration (on-device, real-time food detection)
- Go deep on Compose UI — collapsing headers, carousels, bottom sheets, shimmer, animations
- Build clean MVVM architecture with Room
- Use Firebase AI Logic (Gemini) for multimodal AI calls (image + text)
- Ship something portfolio-worthy that is genuinely useful

---

## Tech Stack

| Layer | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + Repository) |
| Camera | CameraX |
| On-device ML | ML Kit — Object Detection & Labeling |
| AI nutrition cards | Firebase AI Logic (Gemini 2.0 Flash) |
| Local storage | Room |
| DI | Hilt |
| Image loading | Coil |
| Navigation | Jetpack Navigation Compose |
| Serialization | Kotlin Serialization |

---

## Firebase Setup

- Firebase project connected via `google-services.json`
- **Firebase AI Logic** enabled (formerly "Vertex AI in Firebase")
- API Provider: **Gemini Developer API** (has free tier)
- Billing: Blaze plan (pay-as-you-go, free within dev limits)
- No Firestore, no Auth, no Cloud Functions needed

---

## App Screens

### 1. Home Screen
Main feed. First screen on app open.

**Responsibilities:**
- Hero banner — today's total calories vs daily goal
- Horizontal carousel — today's logged meals
- Staggered grid — all past scans (newest first)
- Tap any card → Scan Detail Screen
- Collapsing top bar on scroll
- Search icon → Search Screen

**Compose techniques:**
- `LazyColumn` with `LazyRow` inside for carousel
- `LazyVerticalStaggeredGrid` for past scans
- `NestedScrollConnection` for collapsing top bar
- Shimmer skeleton on first load

---

### 2. Camera Screen
Core ML screen. Full screen camera with live detection.

**Responsibilities:**
- Full screen CameraX preview
- ML Kit runs on every frame — detects if food is in frame
- "Food detected" chip appears as overlay when confident
- Snap button (lights up when food detected)
- On snap → freezes frame → opens Quick Input Sheet

**Compose techniques:**
- `AndroidView` interop for CameraX `PreviewView`
- `Canvas` overlay for detection chip
- `rememberInfiniteTransition` for scanning animation
- Camera permission handling in Compose

---

### 3. Quick Input Sheet (Bottom Sheet — Step 1)
Slides up immediately after snap, **before** Gemini is called.
Lets user provide context to improve AI accuracy.

**Responsibilities:**
- Shows snapped photo thumbnail
- Food name field — pre-filled with ML Kit's detected label if available, editable
- Quantity / portion field — free text (e.g. "1 bowl", "2 pieces", "300g")
- Optional comment field — for extra context
    - e.g. "no cashews used"
    - e.g. "paneer is high protein — 40g per 200g"
    - e.g. "homemade, less oil"
- "Analyse →" button → triggers Gemini call → transitions to Info Card Sheet

**Compose techniques:**
- `ModalBottomSheet`
- `TextField` with focus management
- Keyboard handling (`ImeAction.Next` flow)

---

### 4. Info Card Sheet (Bottom Sheet — Step 2)
Slides up after user taps "Analyse". Gemini is called at this moment.

**What is sent to Gemini:**
```
Image: [captured photo]
Food name: [user input]
Quantity: [user input]
Notes: [optional comment]

Identify this food and return accurate nutrition 
info as JSON based on all provided context.
```

**Responsibilities:**
- Shimmer placeholders while Gemini responds
- Food name + category badge
- Estimated calories (big, prominent)
- Serving size chips — [ Half ] [ 1x ] [ 2x ] — calories update live
- Macro bars — Protein, Carbs, Fat, Fibre (animated fill on load)
- Highlights — Good for / Watch out / Fun fact
- Auto-assigned meal type based on time (editable)
- **Log Meal** button → saves to Room
- **Discard** button → nothing saved

**Compose techniques:**
- `ModalBottomSheet` with `SheetState`
- Shimmer placeholders
- Staggered `AnimatedVisibility` per section
- Animated calorie count-up on serving size change

---

### 5. Daily Log Screen
Food diary. Accessible via bottom nav.

**Responsibilities:**
- Date navigation (← prev day / next day →)
- Circular progress ring — calories consumed vs daily goal
- Macro summary — Protein, Carbs, Fat progress bars
- Meals grouped by: Breakfast / Lunch / Dinner / Snack
- Auto-assignment based on time of logging:
    - 6am–10am → Breakfast
    - 11am–2pm → Lunch
    - 3pm–5pm → Snack
    - 6pm–10pm → Dinner
    - Otherwise → Snack
- User can change meal type if auto-assignment is wrong
- Delete any logged meal

**Compose techniques:**
- `Canvas` for circular progress ring
- `LinearProgressIndicator` for macros
- `LazyColumn` with sticky meal type headers

---

### 6. Scan Detail Screen
Full view of any saved scan. Opened from Home feed.

**Responsibilities:**
- Large photo with collapsing header on scroll
- Full info card content
- User's original input (name, qty, comment they typed)
- Date, time, meal type
- Delete option

**Compose techniques:**
- Collapsing header via `NestedScrollConnection`
- Parallax scroll on photo

---

### 7. Search Screen
Search past scans by food name.

**Responsibilities:**
- Auto-focused search bar on open
- Recent searches
- Results filtered from Room by food name

---

### 8. Settings Screen
Lightweight user preferences.

**Responsibilities:**
- Set display name
- Set daily calorie goal (default: 2000 kcal)
- Clear all data
- App version

---

## Data Models

### MealEntity (Room)
```kotlin
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodName: String,           // from user input
    val quantity: String,           // e.g. "1 bowl"
    val userNotes: String?,         // optional comment
    val mealType: String,           // breakfast/lunch/dinner/snack
    val calories: Int,              // estimated, after serving multiplier
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fibre: Float,
    val aiSummary: String,          // full Gemini JSON response
    val photoPath: String,          // local file path
    val date: String,               // "2026-04-05"
    val timestamp: Long
)
```

---

## Gemini Prompt Design

### Input sent to Gemini
```
The user has snapped a photo of their food.

Food name (user provided): {foodName}
Quantity: {quantity}
Additional notes: {userNotes}

Based on the image and the context above, estimate 
the nutrition info. Return ONLY a JSON object, no 
other text.
```

### Expected JSON response
```json
{
  "food_name": "Butter Chicken",
  "category": "meal",
  "serving_description": "1 bowl (~300g)",
  "calories_per_serving": 420,
  "macros": {
    "protein_g": 28,
    "carbs_g": 18,
    "fat_g": 22,
    "fibre_g": 2
  },
  "highlights": {
    "good_for": "Protein, iron, calcium",
    "watch_out": "High in saturated fat",
    "fun_fact": "Butter chicken was invented in Delhi in the 1950s"
  }
}
```

---

## Phases

### Phase 1 — Foundation
- Project setup: Hilt, Navigation, Room, Coil
- Firebase AI Logic connected and verified
- Theme + design system (colors, typography)
- Navigation graph with bottom nav
- All screen skeletons (empty composables)

### Phase 2 — Camera + ML Kit
- CameraX preview screen
- ML Kit food detection — live overlay chip
- Snap to capture frame
- Photo saved to local storage

### Phase 3 — Quick Input + Gemini
- Quick Input bottom sheet (name, qty, comment)
- Firebase AI Logic call with image + user context
- JSON response parsing → InfoCard data class
- Info Card bottom sheet UI with shimmer + animations
- Save meal to Room

### Phase 4 — Home Feed (Design Phase)
- Hero banner with today's calories
- Today's meals carousel (LazyRow)
- Past scans staggered grid
- Shimmer skeletons
- Collapsing top bar
- Scan Detail screen

### Phase 5 — Daily Log Screen
- Circular calorie progress ring
- Macro bars
- Meals grouped by type
- Date navigation
- Delete meal

### Phase 6 — Polish
- Search screen
- Settings screen
- Animations and transitions
- Empty states + error states
- Edge cases (Gemini failure, no food detected, etc.)

---

## Compose Design Components

| Component | Compose API |
|---|---|
| Shimmer skeleton | `Canvas` + `LinearGradient` + `InfiniteTransition` |
| Collapsing top bar | `NestedScrollConnection` |
| Horizontal meal carousel | `LazyRow` + `snapFlingBehavior` |
| Live camera overlay | `AndroidView` + `Canvas` |
| Staggered photo grid | `LazyVerticalStaggeredGrid` |
| Quick input sheet | `ModalBottomSheet` |
| Info card sheet | `ModalBottomSheet` + staggered `AnimatedVisibility` |
| Calorie progress ring | `Canvas` arc drawing |
| Macro progress bars | `LinearProgressIndicator` |
| Serving size chips | `FilterChip` row |
| Swipe to delete | `SwipeToDismiss` |

---

## Bottom Navigation

```
[ 🏠 Home ]  [ 📷 Snap ]  [ 📊 Log ]  [ ⚙️ Settings ]
```

---

## Intentionally Out of Scope (v1)

- User accounts / login
- Cloud sync or backup
- Barcode / QR scanning
- Weekly / monthly charts
- Manual food entry without a photo
- Social sharing
- Custom daily macro goals (only calorie goal in v1)