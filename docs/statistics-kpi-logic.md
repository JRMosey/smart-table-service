# Statistics Dashboard KPI Logic

## Purpose

This document defines the calculation logic for the Statistics Dashboard before the final UI/UX is ready.

The dashboard will use Firebase Firestore data from:

- orders
- payments
- tables
- menuItems
- users

The logic is based on the Firebase structure and status values confirmed by the team.

## Sprint 2 Priority Metrics

For Sprint 2, the Statistics Dashboard will focus first on:

- Total Revenue
- Total Orders
- Average Order Value
- Occupied Tables
- Payment Method Totals

If time permits, the dashboard can also prepare:

- Tips Collected
- Tax Collected
- Top-Selling Items

## KPI Calculations

### Total Revenue

Collection: payments

Logic:

Revenue should be calculated from successful payments only.

Formula:

totalRevenue = sum(amountPaid) where payment status is paid or completed

Required fields:

- amountPaid
- status
- paidAt

Valid payment statuses for revenue:

- paid
- completed

---

### Total Orders

Collection: orders

Logic:

Count all valid orders in the selected date range.

Formula:

totalOrders = count(orderId) where order status is not cancelled

Required fields:

- orderId
- status
- createdAt

Excluded status:

- cancelled

---

### Average Order Value

Collections: payments and orders

Logic:

Average amount earned per valid order.

Formula:

averageOrderValue = totalRevenue / totalOrders

Required fields:

From payments:

- amountPaid
- status
- orderId

From orders:

- orderId
- status
- createdAt

Note:

If totalOrders is 0, averageOrderValue should be 0 to avoid division errors.

---

### Occupied Tables

Collection: tables

Logic:

Count tables where status is occupied.

Formula:

occupiedTables = count(tableId) where status is occupied

Required fields:

- tableId
- status

---

### Payment Method Totals

Collection: payments

Logic:

Group successful payments by payment method and sum amountPaid.

Formula:

paymentMethodTotal = sum(amountPaid) grouped by method where status is paid or completed

Required fields:

- method
- amountPaid
- status
- paidAt

Example methods:

- cash
- card
- online

---

## Optional KPI Calculations

### Daily Orders

Collection: orders

Logic:

Count how many orders were created each day.

Formula:

dailyOrders = count(orderId) grouped by createdAt date

Required fields:

- orderId
- createdAt
- status

Note:

Cancelled orders can be excluded if the dashboard is showing business activity only.

---

### Available Tables

Collection: tables

Logic:

Count tables where status is available.

Formula:

availableTables = count(tableId) where status is available

Required fields:

- tableId
- status

---

### Tips Collected

Collection: payments

Logic:

Sum all tip values from successful payments.

Formula:

tipsCollected = sum(tip) where payment status is paid or completed

Required fields:

- tip
- status
- paidAt

Note:

If tip is missing or null, it should be treated as 0.

---

### Tax Collected

Collection: orders

Logic:

Sum tax amount from valid paid orders.

Formula:

taxCollected = sum(taxAmount) where order status is paid

Required fields:

- taxAmount
- status
- createdAt

---

### Top-Selling Items

Collection: orders

Nested data:

orders.items

Logic:

Group ordered items by itemId or name and sum their quantities.

Formula:

quantitySold = sum(quantity) grouped by itemId

Required fields inside orderItem:

- itemId
- name
- quantity
- unitPrice

Optional future field:

- itemTotal

Note:

The team structure does not currently require itemTotal inside orderItem. If needed later, itemTotal can be added to make item-level revenue calculations easier.

---

### Sales by Category

Collections:

- orders
- menuItems

Logic:

The dashboard can read item category from menuItems using itemId.

Formula:

categorySales = sum(quantity * unitPrice) grouped by category

Required fields:

From orders.items:

- itemId
- quantity
- unitPrice

From menuItems:

- itemId
- category

Optional future improvement:

The team can copy category into each orderItem when the order is created to make category-wise reports faster and easier.

---

## Confirmed Status Values

### Order Status

- pending
- preparing
- ready
- served
- paid
- cancelled

### Payment Status

- pending
- paid
- completed
- refunded
- failed

### Table Status

- available
- occupied
- reserved
- cleaning

## Important Notes

Revenue should be calculated from payments.amountPaid only when payment status is paid or completed.

Order status should use paid for paid/completed orders because completed is not part of the confirmed order status list.

Cancelled orders should not be included in completed business statistics.

The final UI and chart layout will be added after the UI/UX design is ready.

Janvi will work on the Statistics Dashboard UI. This logic can be connected to the dashboard layout once the UI is available.