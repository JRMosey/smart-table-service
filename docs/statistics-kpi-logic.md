# Statistics Dashboard KPI Logic

## Purpose

This document defines the calculation logic for the Statistics Dashboard before the final UI/UX is ready.

The dashboard will use Firebase Firestore data from orders, payments, tables, menuItems, and users.

## KPI Calculations

### Total Revenue

Collection: payments

Logic:

totalRevenue = sum(amountPaid) where status is paid or completed

Required fields:

- amountPaid
- status
- paidAt

### Daily Orders

Collection: orders

Logic:

dailyOrders = count(orderId) grouped by createdAt date

Required fields:

- orderId
- createdAt
- status

### Total Orders

Collection: orders

Logic:

totalOrders = count(orderId) in selected date range

Cancelled orders should not be counted if the dashboard is showing completed business activity.

Required fields:

- orderId
- status
- createdAt

### Average Order Value

Collections: payments and orders

Logic:

averageOrderValue = totalRevenue / totalOrders

Required fields:

- amountPaid
- orderId
- status

### Occupied Tables

Collection: tables

Logic:

occupiedTables = count(tableId) where status is occupied

Required fields:

- tableId
- status

### Available Tables

Collection: tables

Logic:

availableTables = count(tableId) where status is available

Required fields:

- tableId
- status

### Top Selling Items

Collection: orders

Nested data: orders.items

Logic:

Group order items by itemId or name and sum quantity.

quantitySold = sum(quantity) grouped by itemId

Required fields inside orderItem:

- itemId
- name
- quantity
- unitPrice
- itemTotal

### Sales by Category

Collection: orders

Nested data: orders.items

Logic:

categorySales = sum(itemTotal) grouped by category

Required fields inside orderItem:

- category
- itemTotal
- quantity

Note:

The category field should be copied into orderItem when the order is created. This makes dashboard reporting easier because the dashboard will not need to fetch menuItems again for every order.

### Payment Method Report

Collection: payments

Logic:

paymentMethodTotal = sum(amountPaid) grouped by method

Required fields:

- method
- amountPaid
- status
- paidAt

Example methods:

- cash
- card
- online

### Tips Collected

Collection: payments

Logic:

tipsCollected = sum(tip) where status is paid or completed

Required fields:

- tip
- status
- paidAt

### Tax Collected

Collection: orders

Logic:

taxCollected = sum(taxAmount) where order status is completed

Required fields:

- taxAmount
- status
- createdAt

## Status Values Needed

The team should confirm common status values.

### Order Status

- pending
- preparing
- ready
- served
- completed
- cancelled

### Payment Status

- pending
- paid
- completed
- failed
- refunded

### Table Status

- available
- occupied
- reserved
- cleaning

## Sprint 2 Scope

This document supports the Sprint 2 Statistics Dashboard task by defining:

- dashboard KPIs
- Firebase collections needed
- calculation formulas
- required fields
- status values needed for reporting

The final UI and chart layout will be added after the UI/UX design is ready.