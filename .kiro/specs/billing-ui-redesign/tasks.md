# Implementation Plan: billing-ui-redesign

## Overview

Incrementally replace the `GenerateBill.java` null-layout 3-panel UI with a modern POS-style two-panel window. Work is split into: data model + cart logic (pure Java, testable in isolation), then UI panels built on top, then the payment dialog, and finally wiring everything back into the existing `GenerateBill` entry point.

## Tasks

- [x] 1. Create CartItem data class and cart operations
  - Create `src/com/rms/bill/CartItem.java` with fields: `name`, `size`, `unitPrice`, `quantity`, and a `lineTotal()` method.
  - Create `src/com/rms/bill/CartManager.java` with methods: `addToCart(name, size, price)`, `incrementCart(name, size)`, `decrementCart(name, size)`, `removeFromCart(name, size)`, `clearCart()`, `getItems()`.
  - `addToCart` must find an existing CartItem by (name, size) and increment quantity, or add a new one.
  - `decrementCart` must remove the row when quantity reaches 0.
  - _Requirements: 3.4, 3.5, 4.3, 4.4, 4.5_

  - [ ]* 1.1 Write property tests for CartManager (Property 1, 2)
    - **Property 1: Cart deduplication — same item accumulates, not duplicates**
    - **Property 2: Quantity decrement to zero removes row**
    - **Validates: Requirements 3.5, 4.4**

- [x] 2. Implement totals calculation logic
  - Add `recalculateTotals(double discountPct)` to `CartManager` (or a separate `TotalsCalculator` class) that computes subtotal, discountAmt, vatAmt, and total using the formula from the design.
  - Read VAT from `Utils.VAT`; accept discount percentage as a parameter.
  - Return a `TotalsResult` value object with all four fields.
  - _Requirements: 5.1, 5.2, 5.4, 5.5_

  - [ ]* 2.1 Write property tests for totals calculation (Property 3)
    - **Property 3: Totals formula correctness**
    - **Validates: Requirements 5.1, 5.2, 5.4, 5.5**

- [x] 3. Implement image loading and cache
  - Add `ItemImageLoader.java` in `src/com/rms/bill/` with a `loadImage(String itemName)` method.
  - Attempt to load `src/image/<itemName>.jpg` (lowercased, spaces replaced with underscores) via `ImageIO.read`.
  - Scale loaded images to 120×90 px with `Image.SCALE_SMOOTH`.
  - On failure, generate a 120×90 `BufferedImage` filled with a colour derived from `itemName.hashCode()`.
  - Cache results in a `Map<String, ImageIcon>` keyed by item name; return cached value on repeated calls.
  - _Requirements: 10.1, 10.2, 10.3, 10.4_

  - [ ]* 3.1 Write property tests for image loader (Properties 9, 10)
    - **Property 9: Image scaling correctness**
    - **Property 10: Image cache idempotence**
    - **Validates: Requirements 10.3, 10.4**

- [ ] 4. Checkpoint — Ensure unit and property tests pass
  - Run all tests written so far; fix any failures before continuing.
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Build CategoryTabBar component
  - Create `CategoryTabBar.java` in `src/com/rms/bill/` as a `JPanel` subclass.
  - Constructor accepts a `List<Category>` and a `Consumer<String>` callback for category selection.
  - Render one `JToggleButton` per category plus an "All Items" button; wire them into a `ButtonGroup`.
  - Highlight the selected button (background color or border change).
  - Fire the callback with the selected category name (or empty string for "All Items") on each click.
  - _Requirements: 2.1, 2.2, 2.3, 2.4_

  - [ ]* 5.1 Write property test for tab count (Property 8)
    - **Property 8: Category tab count matches category list**
    - **Validates: Requirements 2.1**

- [x] 6. Build ItemCard and ItemGridPanel components
  - Create `ItemCard.java` as a `JPanel` subclass; renders image icon (from `ItemImageLoader`), item name label, and price label ("৳ X.XX").
  - Add a `MouseListener` to `ItemCard` that calls a provided `Consumer<CartItem>` callback.
  - Create `ItemGridPanel.java` as a `JScrollPane`-wrapped panel using a `GridLayout` (3 columns, variable rows).
  - Add `loadItems(String categoryFilter)` method that queries the DB via the same SQL as `loadItems()` in the current code and repopulates the grid.
  - Show "No items in this category" label when the grid is empty.
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.6, 2.5_

  - [ ]* 6.1 Write property test for category filter (Property 7)
    - **Property 7: Category filter completeness and soundness**
    - **Validates: Requirements 2.2, 2.3**

- [x] 7. Build CartPanel component
  - Create `CartPanel.java` as a `JPanel` subclass with `BoxLayout.Y_AXIS`.
  - Top section: `JScrollPane` wrapping a `CartListPanel` (`BoxLayout.Y_AXIS`) that renders one `CartRow` per `CartItem`.
  - Each `CartRow` shows: item name + size, "−" button, quantity label, "+" button, line total, "✕" remove button.
  - Bottom section: `TotalsPanel` with Subtotal, editable Discount % field (pre-filled from `Utils.DISCOUNT`), VAT % (read-only), Discount amount, VAT amount, Total — all recalculated via `CartManager.recalculateTotals()` on every cart change.
  - `PayButton` at the bottom labelled "Pay ৳ X.XX" — disabled when cart is empty.
  - "Cancel" button below PayButton.
  - Wire all CartRow buttons to `CartManager` methods; after each mutation call `refresh()` to re-render rows and update TotalsPanel labels and PayButton text.
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 5.1, 5.2, 5.3, 6.1, 6.2, 8.1_

  - [ ]* 7.1 Write property test for PayButton disabled state (Property 4)
    - **Property 4: PayButton disabled when cart is empty**
    - **Validates: Requirements 4.6, 6.4**

  - [ ]* 7.2 Write property test for discount recalculation (Property 5)
    - **Property 5: Discount field drives recalculation**
    - **Validates: Requirements 5.3**

- [x] 8. Implement cancel flow in CartPanel
  - Wire the Cancel button to show `JOptionPane.showConfirmDialog` asking for confirmation.
  - On YES: call `CartManager.clearCart()`, reset the discount field to `Utils.DISCOUNT`, call `refresh()`.
  - On NO: do nothing.
  - _Requirements: 8.2, 8.3, 8.4_

  - [ ]* 8.1 Write property test for cancel decline (Property 6)
    - **Property 6: Cancel decline preserves cart state**
    - **Validates: Requirements 8.4**

- [x] 9. Build PaymentDialog
  - Create `PaymentDialog.java` extending `JDialog` (modal, titled "Complete Payment").
  - Display: total due amount label, payment method `JComboBox<PaymentType>`, Amount Received `JTextField`, Reference `JTextField` (optional), "Confirm Payment" and "Cancel" buttons.
  - "Confirm Payment" action: call the `onConfirm` callback provided at construction (which runs `saveTransaction()` + `saveBillDetails()` + print + `cleanCart()`); show success `JOptionPane`; close dialog.
  - If `saveTransaction()` returns null, show error `JOptionPane` and keep dialog open.
  - "Cancel" closes the dialog without any side effects.
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7, 7.8, 7.9_

- [ ] 10. Checkpoint — Ensure all tests pass
  - Run the full test suite; fix any failures.
  - Ensure all tests pass, ask the user if questions arise.

- [x] 11. Assemble POSBillingPanel and wire into GenerateBill
  - Create `POSBillingPanel.java` that builds the full two-panel layout:
    - Left: `CategoryTabBar` on top, `ItemGridPanel` below (uses `BorderLayout`).
    - Right: `CartPanel` at fixed 300 px width.
  - In the constructor, load categories from `AppService.getCategory()`, pass them to `CategoryTabBar`.
  - Wire `CategoryTabBar` selection callback → `ItemGridPanel.loadItems(category)`.
  - Wire `ItemCard` click callback → `CartManager.addToCart()` → `CartPanel.refresh()`.
  - Wire `CartPanel.onPayButtonClicked` → open `PaymentDialog` with current totals and cart data.
  - `PaymentDialog.onConfirm` calls the existing `saveTransaction()`, `saveBillDetails()`, print receipt logic (extracted verbatim from current `GenerateBill`), then `CartManager.clearCart()` + `CartPanel.refresh()`.
  - Replace the UI construction code in `GenerateBill` constructor with: `add(new POSBillingPanel(), BorderLayout.CENTER)`.
  - Remove all legacy null-layout panel construction, legacy JTable/DTM declarations, and the legacy centre panel.
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 6.3, 9.1, 9.2, 9.3, 9.4, 9.5_

- [x] 12. Final checkpoint — Ensure all tests pass
  - Run the full test suite.
  - Verify the application compiles and launches without errors.
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for a faster MVP.
- `CartManager` and `TotalsCalculator` are pure Java (no Swing), making them straightforward to unit and property test.
- `ItemImageLoader` can be tested without a display by running in headless mode (`System.setProperty("java.awt.headless","true")`).
- Property tests use jqwik; add `net.jqwik:jqwik:1.8.x` to the classpath.
- The existing `saveTransaction()`, `saveBillDetails()`, and print receipt logic should be moved verbatim into `POSBillingPanel` (or a helper method) — do not refactor the business logic.
