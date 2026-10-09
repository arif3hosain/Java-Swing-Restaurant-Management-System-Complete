# Requirements Document

## Introduction

This feature redesigns the billing UI of the Restaurant Management System (RMS) from the current 3-panel table-driven layout to a modern Point of Sale (POS) style interface. The redesign replaces the `GenerateBill.java` UI with a two-panel layout: a left panel containing category filter tabs and an image-based item card grid, and a right cart panel with inline quantity controls, totals breakdown, and a Pay button that opens a payment modal dialog. All existing business logic (saveTransaction, saveBillDetails, print receipt) is preserved unchanged.

## Glossary

- **BillingUI**: The redesigned `GenerateBill.java` window that replaces the current 3-panel layout.
- **CategoryTab**: A clickable tab at the top of the left panel that filters the item grid by food category.
- **ItemCard**: A visual card component displaying a food item's image (or placeholder), name, and price in the item grid.
- **ItemGrid**: The scrollable grid of ItemCards below the CategoryTabs.
- **Cart**: The right-side panel listing all items the cashier has added, with quantity controls and pricing.
- **CartRow**: A single line in the Cart representing one item — shows name, size, quantity controls, line total, and a remove button.
- **QuantityControl**: The minus (−) and plus (+) buttons on each CartRow that adjust item quantity.
- **Subtotal**: The sum of all CartRow line totals before discount and VAT.
- **Discount**: A configurable reduction applied to the Subtotal, expressed as a percentage (default from `Utils.DISCOUNT`).
- **VAT**: Value-added tax percentage applied after discount (from `Utils.VAT`).
- **Total**: The final payable amount: `(Subtotal − Discount) + VAT`.
- **PayButton**: The primary action button at the bottom of the Cart panel, labelled "Pay ৳ X.XX".
- **PaymentDialog**: A modal JDialog triggered by the PayButton for completing payment entry.
- **AppService**: The existing service class providing `getUniqueItems()`, `getFoodTypes()`, and `getCategory()`.
- **Utils**: The existing utility class providing `VAT`, `DISCOUNT`, `TITLE`, `authority`, and helper methods.
- **PrinterService**: The existing service used to send formatted receipt text to a thermal printer.
- **Placeholder**: A solid colour block rendered in place of a missing or unloadable item image.

## Requirements

### Requirement 1: Two-Panel POS Layout

**User Story:** As a cashier, I want a clear two-panel POS layout, so that I can see available items on the left and the active cart on the right simultaneously.

#### Acceptance Criteria

1. THE BillingUI SHALL render a left panel occupying at least 60% of the window width and a right Cart panel of approximately 300 px fixed width.
2. THE BillingUI SHALL be displayed in a single `JFrame` sized to fill the screen at 1380×730 (or maximised), centred on screen.
3. WHEN the BillingUI opens, THE BillingUI SHALL load category tabs and item cards before the window becomes interactive.
4. THE BillingUI SHALL NOT render the legacy centre combo-box panel, item-selection spinner, or the legacy right-side item-list table.

---

### Requirement 2: Category Filter Tabs

**User Story:** As a cashier, I want category tabs at the top of the item grid, so that I can quickly filter items to a specific food type.

#### Acceptance Criteria

1. WHEN the BillingUI loads, THE CategoryTab bar SHALL display an "All Items" tab followed by one tab per active category returned by `AppService.getCategory()`.
2. WHEN a cashier clicks a CategoryTab, THE ItemGrid SHALL display only ItemCards whose category matches the selected tab.
3. WHEN the "All Items" tab is selected, THE ItemGrid SHALL display ItemCards for all active items regardless of category.
4. THE CategoryTab bar SHALL highlight the currently selected tab to distinguish it from unselected tabs.
5. IF no items exist for a selected category, THEN THE ItemGrid SHALL display an empty state message such as "No items in this category".

---

### Requirement 3: Item Card Grid

**User Story:** As a cashier, I want a scrollable grid of item image cards, so that I can visually identify and add items to the cart quickly.

#### Acceptance Criteria

1. THE ItemGrid SHALL arrange ItemCards in a responsive grid with a fixed card width of 160 px and at least 3 columns.
2. EACH ItemCard SHALL display the item's image scaled to 120×90 px, the item name, and the item's base price formatted as "৳ X.XX".
3. IF an item's image file cannot be loaded, THEN THE ItemCard SHALL display a coloured Placeholder block of the same dimensions (120×90 px).
4. WHEN a cashier clicks an ItemCard, THE Cart SHALL add one unit of that item using the item's default size and base price.
5. IF the clicked item is already present in the Cart, THEN THE Cart SHALL increment that item's quantity by 1 instead of creating a duplicate row.
6. THE ItemGrid SHALL be wrapped in a `JScrollPane` to allow scrolling when items exceed the visible area.

---

### Requirement 4: Cart Panel

**User Story:** As a cashier, I want a right-side cart panel, so that I can review and adjust the order before payment.

#### Acceptance Criteria

1. THE Cart SHALL display a scrollable list of CartRows, one per distinct item-size combination added.
2. EACH CartRow SHALL show the item name, size, current quantity, line total (quantity × unit price), and a remove button.
3. WHEN a cashier clicks the plus (+) QuantityControl on a CartRow, THE Cart SHALL increment that item's quantity by 1 and recalculate totals.
4. WHEN a cashier clicks the minus (−) QuantityControl on a CartRow, THE Cart SHALL decrement that item's quantity by 1; IF the quantity reaches 0, THEN THE Cart SHALL remove that CartRow.
5. WHEN a cashier clicks the remove button on a CartRow, THE Cart SHALL immediately remove that row and recalculate totals.
6. WHILE the Cart contains no items, THE PayButton SHALL be disabled.

---

### Requirement 5: Totals Display

**User Story:** As a cashier, I want a live totals breakdown below the cart list, so that I can confirm the subtotal, discount, VAT, and grand total before charging the customer.

#### Acceptance Criteria

1. THE Cart panel SHALL display Subtotal, Discount amount, VAT amount, and Total as labelled rows below the CartRow list.
2. WHEN any CartRow quantity changes or a CartRow is added or removed, THE BillingUI SHALL recalculate and refresh Subtotal, Discount, VAT, and Total immediately.
3. THE Discount row SHALL include an editable text field pre-populated with the percentage from `Utils.DISCOUNT`; WHEN a cashier changes this value, THE BillingUI SHALL recalculate Discount, VAT, and Total.
4. THE VAT row SHALL display the percentage from `Utils.VAT` as a read-only label; THE VAT amount SHALL be calculated as `(Subtotal − Discount) × (VAT% / 100)`.
5. THE Total SHALL be calculated as `(Subtotal − Discount) + VAT`.

---

### Requirement 6: Pay Button

**User Story:** As a cashier, I want a prominent Pay button at the bottom of the cart, so that I can initiate payment when the order is ready.

#### Acceptance Criteria

1. THE PayButton SHALL be displayed at the bottom of the Cart panel, labelled "Pay ৳ X.XX" where X.XX reflects the current Total.
2. WHEN the Total changes, THE PayButton label SHALL update to reflect the new Total amount.
3. WHEN a cashier clicks the PayButton and the Cart contains at least one item, THE BillingUI SHALL open the PaymentDialog.
4. WHILE the Cart is empty, THE BillingUI SHALL disable the PayButton and prevent the PaymentDialog from opening.

---

### Requirement 7: Payment Dialog

**User Story:** As a cashier, I want a payment modal dialog, so that I can record the payment method and confirm the transaction.

#### Acceptance Criteria

1. THE PaymentDialog SHALL be a modal `JDialog` titled "Complete Payment" that blocks interaction with the BillingUI while open.
2. THE PaymentDialog SHALL display the total due amount prominently.
3. THE PaymentDialog SHALL provide a payment method dropdown pre-populated with the values from `PaymentType` (Cash, MFS, Credit).
4. THE PaymentDialog SHALL provide an "Amount Received" numeric input field.
5. THE PaymentDialog SHALL provide an optional "Reference" text input field.
6. THE PaymentDialog SHALL display a "Confirm Payment" button and a "Cancel" button.
7. WHEN a cashier clicks "Confirm Payment", THE PaymentDialog SHALL call `saveTransaction()` then `saveBillDetails()`, print the receipt via `PrinterService`, show a confirmation message, close the dialog, and clear the cart.
8. WHEN a cashier clicks "Cancel", THE PaymentDialog SHALL close without saving any data or modifying the cart.
9. IF `saveTransaction()` returns null (indicating an error), THEN THE PaymentDialog SHALL display an error message and remain open.

---

### Requirement 8: Cancel / Clear Cart

**User Story:** As a cashier, I want a Cancel button that clears the current order, so that I can start fresh if a customer changes their mind.

#### Acceptance Criteria

1. THE Cart panel SHALL include a "Cancel" button separate from the PaymentDialog.
2. WHEN a cashier clicks the Cancel button, THE BillingUI SHALL display a confirmation prompt before clearing the cart.
3. WHEN the cashier confirms the cancel action, THE BillingUI SHALL remove all CartRows, reset Subtotal, Discount, VAT, and Total to zero, and re-enable the discount field to its default value.
4. WHEN the cashier declines the cancel prompt, THE BillingUI SHALL leave the cart unchanged.

---

### Requirement 9: Business Logic Preservation

**User Story:** As a system owner, I want all existing transaction and printing logic preserved, so that financial records and receipt output remain unchanged.

#### Acceptance Criteria

1. THE BillingUI SHALL invoke the existing `saveTransaction()` method to persist bill and payment data to the database.
2. THE BillingUI SHALL invoke the existing `saveBillDetails()` method to persist line-item details to the `bill_details` table.
3. THE BillingUI SHALL use the existing `PrinterService.printString()` and `PrinterService.printBytes()` methods to print receipts on the thermal printer.
4. THE BillingUI SHALL format the printed receipt using the same layout and content as the current implementation (title, address, invoice number, item lines, totals, footer).
5. THE BillingUI SHALL pass the note text from the optional Reference field to the receipt note section.

---

### Requirement 10: Image Handling

**User Story:** As a cashier, I want item cards to show food images where available, so that I can recognise items at a glance.

#### Acceptance Criteria

1. WHEN loading an ItemCard, THE BillingUI SHALL attempt to load the item's image from the `src/image/` directory using the item name as a filename hint (e.g., `burger.jpg`).
2. IF the image file is not found or fails to load, THEN THE BillingUI SHALL render a Placeholder of a distinct background colour derived from the item name (so different items get different colours).
3. THE BillingUI SHALL scale all loaded images to 120×90 px using smooth scaling (`Image.SCALE_SMOOTH`).
4. THE BillingUI SHALL cache loaded images in memory to avoid repeated disk reads during a session.
