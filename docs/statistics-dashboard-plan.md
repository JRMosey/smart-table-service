# Statistics Dashboard Plan

## Module Owner

Dhruv - Statistics Dashboard

## Purpose

The Statistics Dashboard will help restaurant managers monitor restaurant performance using Firebase Firestore data.

Since the final UI/UX is not ready yet, this document defines the dashboard metrics, required collections, calculation logic, and Firebase fields needed for development.

## Collections Needed

The Statistics Dashboard will mainly use:

- users
- tables
- menuItems
- orders
- payments

## Dashboard Metrics

| Metric | Collection | Logic |
|---|---|---|
| Total Revenue | payments | Sum amountPaid where status is paid/completed |
| Daily Orders | orders | Count orders grouped by createdAt date |
| Total Orders | orders | Count all orders in selected date range |
| Average Order Value | payments/orders | totalRevenue divided by totalOrders |
| Occupied Tables | tables | Count tables where status is occupied |
| Available Tables | tables | Count tables where status is available |
| Top Selling Items | orders.items | Group items by itemId/name and sum quantity |
| Sales by Category | orders.items | Group by category and sum itemTotal |
| Payment Method Report | payments | Group payments by method |
| Tips Collected | payments | Sum tip values |
| Tax Collected | orders | Sum taxAmount |

## Required Fields

### orders

- orderId
- tableId
- waiterId
- orderType
- status
- items
- subTotal
- taxAmount
- discountAmount
- total
- createdAt
- updatedAt

### payments

- paymentId
- orderId
- processedBy
- method
- status
- amountPaid
- changeDue
- tip
- paidAt

### tables

- tableId
- tableNumber
- capacity
- status
- currentOrderId
- assignedWaiterId

### orderItem

Recommended fields:

- itemId
- name
- category
- quantity
- unitPrice
- itemTotal
- addOns
- notes
- kitchenStatus

## Suggested Improvements

For better analytics, `orderItem` should include `category` and `itemTotal`.

`category` is useful for category-wise sales reports.

`itemTotal` is useful for calculating item sales without recalculating quantity multiplied by price every time.

Revenue should be calculated mainly from successful payments, not only from orders, because cancelled or unpaid orders should not count as real revenue.

## Sprint 2 Scope

For Sprint 2, the Statistics Dashboard work will focus on:

- defining dashboard KPIs
- confirming required Firebase fields
- preparing calculation logic
- preparing data model structure
- waiting for UI/UX before building the final screen layout

The final dashboard UI and charts can be connected after the UI/UX design is ready.