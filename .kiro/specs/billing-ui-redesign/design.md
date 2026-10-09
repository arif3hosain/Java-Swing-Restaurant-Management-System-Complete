# Design Document: Billing UI Redesign

## Overview

The billing UI redesign replaces the current `GenerateBill.java` 3-panel layout with a modern POS-style two-panel window. The left panel shows a category tab bar and a scrollable image card grid. The right panel is a fixed-width cart with inline quantity controls, a live totals breakdown, and a Pay button that opens a modal payment dialog. All business logic (database writes, receipt printing) is preserved exactly as-is.

The implementation is a single new class, `POSBillingPanel`, that replaces the UI construction code in `GenerateBill`. The class is still a `JFrame`-based window, uses Java Swing with `BorderLayout` and `GridLayout` managers (replacing the legacy `null` layout), and integrates with the existing `AppService`, `Utils`, and `PrinterService` classes.

---

## Architecture

```
GenerateBill (JFrame host)
├── POSBillingPanel (new, replaces all UI construction)
│   ├── LeftPanel (BorderLayout)
│   │   ├── CategoryTabBar (JPanel + JToggleButton tabs)
│   │   └── ItemGridScroll (JScrollPane wrapping ItemGridPanel)
│   │       └── ItemGridPanel (WrapLayout / GridLayout of ItemCards)
│   └── CartPanel (fixed 300px, BoxLayout Y)
│       ├── CartListScroll (JScrollPane wrapping CartListPanel)
│       │   └── CartListPanel (BoxLayout Y of CartRows)
│       ├── TotalsPanel (labels + editable discount field)
│       ├── PayButton
│       └── CancelButton
└── PaymentDialog (modal JDialog, opened by PayButton)
```

Data flow:
1. On open: `AppService.getCategory()` → render tabs; `loadItemsForCategory("ALL")` queries DB → render cards.
2. Card click / quantity change → update `cartItems` list → re-render `CartListPanel` → `recalculateTotals()` → update labels + PayButton text.
3. PayButton → open `PaymentDialog` → on confirm: `saveTransaction()` + `saveBillDetails()` + `PrinterService` → `cleanCart()`.

---

## Components and Interfaces

### CategoryTabBar

- Renders one `JToggleButton` per category plus an "All Items" button.
- Uses a `ButtonGroup` so only one tab is selected at a time.
- On selection fires `loadItemsForCategory(categoryName)` which repopulates `ItemGridPanel`.

### ItemCard (inner class or small JPanel subclass)

```java
class ItemCard extends JPanel {
    String itemName;
    String size;       // default size (first from getFoodTypes())
    double unitPrice;
    // renders: image (120×90), name label, price label
    // MouseListener: calls addToCart(itemName, size, unitPrice)
}
```

### ItemGridPanel

- Uses `WrapLayout` (a standard Swing FlowLayout variant that wraps rows) or a fixed-column `GridLayout`.
- Repopulated by `loadItemsForCategory(String category)` which queries the same SQL as the existing `loadItems()`.

### CartItem (data class)

```java
class CartItem {
    String name;
    String size;
    double unitPrice;
    int quantity;
    double lineTotal() { return unitPrice * quantity; }
}
```

### CartRow (JPanel)

- Displays: item name + size | "−" JButton | quantity label | "+" JButton | line total label | "✕" remove button.
- Buttons call `decrementCart(name, size)`, `incrementCart(name, size)`, `removeFromCart(name, size)`.

### TotalsPanel

- Read-only labels: Subtotal, VAT%.
- Editable `JTextField` for Discount %; initialised from `Utils.DISCOUNT`.
- Computed read-only labels: Discount amount, VAT amount, Total.
- Recalculated via `recalculateTotals()` on every cart change.

### PaymentDialog

```java
class PaymentDialog extends JDialog {
    JComboBox<PaymentType> methodCombo;
    JTextField amountReceivedField;
    JTextField referenceField;
    JButton confirmBtn;
    JButton cancelBtn;
    // Confirm triggers: saveTransaction() → saveBillDetails() → print → cleanCart()
}
```

---

## Data Models

### In-memory cart

```java
List<CartItem> cartItems = new ArrayList<>();
```

Cart operations:
- `addToCart(name, size, price)` — finds existing CartItem by (name, size); increments quantity or adds new.
- `incrementCart(name, size)` — increments quantity by 1.
- `decrementCart(name, size)` — decrements; removes if quantity hits 0.
- `removeFromCart(name, size)` — removes immediately.
- `clearCart()` — empties the list.

### Image cache

```java
Map<String, ImageIcon> imageCache = new HashMap<>();
```

Key is the item name (lowercased). Missing/failed images map to a generated `BufferedImage` colour placeholder derived from `itemName.hashCode()`.

### Totals calculation

```java
double subtotal  = cartItems.stream().mapToDouble(CartItem::lineTotal).sum();
double discPct   = parseDouble(discountField.getText());   // from editable field
double discAmt   = (subtotal * discPct) / 100.0;
double vatPct    = parseDouble(Utils.VAT);
double vatAmt    = ((subtotal - discAmt) * vatPct) / 100.0;
double total     = subtotal - discAmt + vatAmt;
```

---

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system — essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

Property 1: Cart deduplication — same item accumulates, not duplicates
*For any* cart state and any item (name, size, price), adding the same (name, size) combination twice should produce exactly one CartRow with quantity 2, not two separate rows.
**Validates: Requirements 3.5**

Property 2: Quantity decrement to zero removes row
*For any* CartItem with quantity 1, calling decrementCart should result in that CartItem being absent from the cart list.
**Validates: Requirements 4.4**

Property 3: Totals formula correctness
*For any* list of CartItems and any discount percentage d (0 ≤ d ≤ 100), all three derived values should satisfy:
- `subtotal = Σ (qty × unitPrice)`
- `discountAmt = subtotal × d / 100`
- `total = (subtotal − discountAmt) × (1 + VAT/100)`

(This consolidates Requirements 5.1, 5.2, 5.4, 5.5.)
**Validates: Requirements 5.1, 5.2, 5.4, 5.5**

Property 4: PayButton disabled when cart is empty
*For any* UI state where the cart contains zero items, the PayButton should be disabled and the PaymentDialog should not open.
**Validates: Requirements 4.6, 6.4**

Property 5: Discount field drives recalculation
*For any* non-empty cart and any two distinct discount percentages d1 and d2, setting the discount field to d1 and then d2 should yield totals consistent with the formula for d2 (the last value set).
**Validates: Requirements 5.3**

Property 6: Cancel decline preserves cart state
*For any* non-empty cart, declining the cancel confirmation prompt should leave the cart's items, quantities, and computed totals bit-for-bit identical to the state before the prompt was shown.
**Validates: Requirements 8.4**

Property 7: Category filter completeness and soundness
*For any* backing item list and any selected category C (not "All Items"), the set of displayed ItemCards should be exactly the subset of items whose category equals C — no items from other categories included, and no matching items omitted.
**Validates: Requirements 2.2, 2.3**

Property 8: Category tab count matches category list
*For any* list of N categories returned by `AppService.getCategory()`, the CategoryTab bar should display exactly N + 1 tabs (the N category tabs plus "All Items").
**Validates: Requirements 2.1**

Property 9: Image scaling correctness
*For any* successfully loaded item image, after processing by the image loader, the resulting `ImageIcon` should have width = 120 and height = 90.
**Validates: Requirements 10.3**

Property 10: Image cache idempotence
*For any* item name that has already been loaded once, calling the image-load function a second time should return the identical cached `ImageIcon` object (same reference), without performing another disk read.
**Validates: Requirements 10.4**

---

## Error Handling

- **DB load failure** (tabs/cards): Show a `JOptionPane` error dialog and render an empty panel; do not crash the window.
- **Image load failure**: Render the colour Placeholder silently (no dialog). Log to `System.err`.
- **`saveTransaction()` returns null**: Keep PaymentDialog open, show `JOptionPane.showMessageDialog` with an error message.
- **Empty cart pay attempt**: PayButton is disabled; guard check in PaymentDialog confirm handler as a second line of defence.
- **Invalid discount input** (non-numeric): Treat as 0% discount; show no error (silent fallback).

---

## Testing Strategy

### Unit tests (JUnit 5)

Focus on pure logic that has no Swing dependency:

- `CartItem.lineTotal()` — spot checks for zero quantity and fractional prices.
- `recalculateTotals()` — verify Subtotal, Discount, VAT, Total for known inputs.
- Image cache — verify that a second call for the same item name returns the cached instance.
- `addToCart` deduplication — verify two adds of the same (name, size) yield quantity 2.
- `decrementCart` to zero — verify row removal.

### Property-based tests (jqwik, minimum 100 iterations each)

Each property test must reference its design property and run against randomly generated inputs.

**Feature: billing-ui-redesign, Property 1: Cart deduplication**
- Generate random (name, size, price) pairs; add each twice; assert `cartItems` has exactly 1 row per pair with quantity 2.

**Feature: billing-ui-redesign, Property 2: Quantity decrement to zero removes row**
- Generate a cart with a single CartItem at quantity 1; call `decrementCart`; assert list is empty.

**Feature: billing-ui-redesign, Property 3: Totals formula correctness**
- Generate random lists of CartItems and random discount % (0–100); assert subtotal, discountAmt, and total all match the formula.

**Feature: billing-ui-redesign, Property 4: PayButton disabled when cart is empty**
- Generate a fresh empty cart state; assert PayButton `isEnabled()` == false.

**Feature: billing-ui-redesign, Property 5: Discount field drives recalculation**
- Generate a random non-empty cart and two random discount values d1, d2; set d1 then d2; assert totals match formula for d2.

**Feature: billing-ui-redesign, Property 6: Cancel decline preserves cart state**
- Generate a random non-empty cart; simulate decline; assert cart is unchanged (same items, quantities, totals).

**Feature: billing-ui-redesign, Property 7: Category filter correctness**
- Generate a random item list with random categories; filter by a chosen category; assert all returned items have that category and no matching items are missing.

**Feature: billing-ui-redesign, Property 8: Category tab count matches category list**
- Generate a random list of N categories; assert rendered tab count == N + 1.

**Feature: billing-ui-redesign, Property 9: Image scaling correctness**
- Generate random BufferedImages of various sizes; run through the image loader; assert resulting icon is 120×90.

**Feature: billing-ui-redesign, Property 10: Image cache idempotence**
- Load the same item name twice; assert the second call returns the cached reference (object identity).

### Integration / manual checks

- Open BillingUI, click each category tab — verify grid updates.
- Add items, adjust quantities, verify totals update live.
- Open PaymentDialog, confirm payment — verify DB row inserted and receipt prints.
- Cancel with items in cart — verify confirmation prompt and cart cleared on yes.
