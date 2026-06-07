# Statistics Dashboard Plan

## Module Owner

Dhruv - Statistics Dashboard

## Purpose

The Statistics Dashboard will help restaurant managers monitor restaurant performance using Firebase Firestore data.

Since the final UI/UX is not ready yet, this document defines the dashboard metrics, required collections, calculation logic, and Firebase fields needed for development.

Janvi will work on the Statistics Dashboard UI and dashboard layouts. This document focuses on the non-UI statistics logic and data structure.

## Collections Needed

The Statistics Dashboard will mainly use:

- users
- tables
- menuItems
- orders
- payments
- notifications
- restaurantSettings

## Confirmed Firebase Structure Notes

The team confirmed the shared Firebase structure for:

- users
- tables
- menuItems
- orders
- payments
- notifications
- restaurantSettings

The team also confirmed that `addOns` should be used instead of `addOn` because it represents a list.

Revenue should be calculated from `payments.amountPaid` only when payment status is `paid` or `completed`.

## Sprint 2 Priority Metrics

For Sprint 2, the Statistics Dashboard will focus first on:

| Metric | Collection | Logic |
|---|---|---|
| Total Revenue | payments | Sum amountPaid where payment status is paid or completed |
| Total Orders | orders | Count orders where status is not cancelled |
| Average Order Value | payments/orders | totalRevenue divided by totalOrders |
| Occupied Tables | tables | Count tables where status is occupied |
| Payment Method Totals | payments | Group successful payments by method and sum amountPaid |

## Optional Metrics If Time Permits

| Metric | Collection | Logic |
|---|---|---|
| Tips Collected | payments | Sum tip values where payment status is paid or completed |
| Tax Collected | orders | Sum taxAmount where order status is paid |
| Top Selling Items | orders.items | Group items by itemId/name and sum quantity |
| Daily Orders | orders | Count orders grouped by createdAt date |
| Available Tables | tables | Count tables where status is available |
| Sales by Category | orders/menuItems | Use itemId to read category from menuItems |

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
- notes
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
- name
- capacity
- status
- currentOrderId
- assignedWaiterId

### menuItems

- itemId
- name
- description
- price
- category
- imageUrl
- isAvailable
- addOns

### orderItem

Required fields based on the shared structure:

- itemId
- name
- quantity
- unitPrice
- addOns
- notes
- kitchenStatus

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

## Suggested Future Improvements

For easier dashboard analytics later, the team can consider adding optional fields inside `orderItem`:

- category
- itemTotal

`category` would make category-wise sales reports easier without repeatedly reading from `menuItems`.

`itemTotal` would make item-level revenue calculations easier without recalculating quantity multiplied by unit price.

These fields are not required for the current confirmed structure, but they can improve dashboard performance and reporting later.

## Sprint 2 Scope

For Sprint 2, the Statistics Dashboard work will focus on:

- defining dashboard KPIs
- confirming required Firebase fields
- preparing calculation logic
- preparing sample Firestore data
- preparing non-UI data structure
- waiting for UI/UX before building the final screen layout

The final dashboard UI and charts can be connected after Janvi’s UI/UX design is ready.