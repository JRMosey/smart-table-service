# Statistics Dashboard Sample Firestore Data

## Purpose

This document defines sample Firestore data that can be used to test the Statistics Dashboard before the final UI/UX is ready.

The sample data follows the shared Firebase collection structure:

- users
- tables
- menuItems
- orders
- payments
- notifications
- restaurantSettings

## users Collection

### users/user_001

```json
{
  "userId": "user_001",
  "name": "John Manager",
  "email": "manager@smarttable.com",
  "phone": "5140000001",
  "pin": null,
  "role": "manager",
  "isActive": true,
  "createdAt": "2026-06-06T10:00:00"
}
```

### users/user_002

```json
{
  "userId": "user_002",
  "name": "Sarah Waiter",
  "email": "waiter@smarttable.com",
  "phone": "5140000002",
  "pin": null,
  "role": "waiter",
  "isActive": true,
  "createdAt": "2026-06-06T10:05:00"
}
```

## tables Collection

### tables/table_001

```json
{
  "tableId": "table_001",
  "tableNumber": 1,
  "name": "Table 1",
  "capacity": 4,
  "status": "occupied",
  "currentOrderId": "order_001",
  "assignedWaiterId": "user_002"
}
```

### tables/table_002

```json
{
  "tableId": "table_002",
  "tableNumber": 2,
  "name": "Table 2",
  "capacity": 2,
  "status": "available",
  "currentOrderId": null,
  "assignedWaiterId": null
}
```

## menuItems Collection

### menuItems/item_001

```json
{
  "itemId": "item_001",
  "name": "Veggie Burger",
  "description": "Vegetarian burger with fries",
  "price": 12.99,
  "category": "Food",
  "imageUrl": "",
  "isAvailable": true,
  "addOns": [
    {
      "addOnId": "addon_001",
      "name": "Extra Cheese",
      "isRequired": false,
      "maxSelections": 1
    }
  ]
}
```

### menuItems/item_002

```json
{
  "itemId": "item_002",
  "name": "Iced Coffee",
  "description": "Cold coffee drink",
  "price": 4.99,
  "category": "Drink",
  "imageUrl": "",
  "isAvailable": true,
  "addOns": []
}
```

## orders Collection

### orders/order_001

```json
{
  "orderId": "order_001",
  "tableId": "table_001",
  "waiterId": "user_002",
  "orderType": "dine_in",
  "status": "completed",
  "items": [
    {
      "itemId": "item_001",
      "name": "Veggie Burger",
      "quantity": 2,
      "unitPrice": 12.99,
      "addOns": [
        {
          "addOnId": "addon_001",
          "name": "Extra Cheese",
          "isRequired": false,
          "maxSelections": 1
        }
      ],
      "notes": "",
      "kitchenStatus": "served"
    },
    {
      "itemId": "item_002",
      "name": "Iced Coffee",
      "quantity": 2,
      "unitPrice": 4.99,
      "addOns": [],
      "notes": "",
      "kitchenStatus": "served"
    }
  ],
  "subTotal": 35.96,
  "taxAmount": 5.39,
  "discountAmount": 0.00,
  "total": 41.35,
  "notes": "",
  "createdAt": "2026-06-06T12:00:00",
  "updatedAt": "2026-06-06T12:45:00"
}
```

### orders/order_002

```json
{
  "orderId": "order_002",
  "tableId": null,
  "waiterId": "user_002",
  "orderType": "takeout",
  "status": "completed",
  "items": [
    {
      "itemId": "item_001",
      "name": "Veggie Burger",
      "quantity": 1,
      "unitPrice": 12.99,
      "addOns": [],
      "notes": "",
      "kitchenStatus": "served"
    }
  ],
  "subTotal": 12.99,
  "taxAmount": 1.95,
  "discountAmount": 0.00,
  "total": 14.94,
  "notes": "",
  "createdAt": "2026-06-06T14:00:00",
  "updatedAt": "2026-06-06T14:20:00"
}
```

## payments Collection

### payments/payment_001

```json
{
  "paymentId": "payment_001",
  "orderId": "order_001",
  "processedBy": "user_002",
  "method": "card",
  "status": "paid",
  "amountPaid": 41.35,
  "changeDue": 0.00,
  "tip": 5.00,
  "paidAt": "2026-06-06T12:50:00"
}
```

### payments/payment_002

```json
{
  "paymentId": "payment_002",
  "orderId": "order_002",
  "processedBy": "user_002",
  "method": "cash",
  "status": "paid",
  "amountPaid": 14.94,
  "changeDue": 0.06,
  "tip": 2.00,
  "paidAt": "2026-06-06T14:25:00"
}
```

## notifications Collection

### notifications/notification_001

```json
{
  "notificationId": "notification_001",
  "recipientId": "user_002",
  "orderId": "order_001",
  "tableId": "table_001",
  "isRead": false,
  "type": "order_update",
  "message": "Order order_001 has been completed.",
  "priority": "normal",
  "createdAt": "2026-06-06T12:45:00",
  "readAt": null
}
```

## restaurantSettings Collection

### restaurantSettings/main

```json
{
  "restaurantName": "Smart Table Restaurant",
  "logoUrl": "",
  "address": "123 Main Street, Montreal, QC",
  "phone": "5140009999",
  "currency": "CAD",
  "taxRate": 0.15,
  "serviceChargeRate": 0.00,
  "openingHours": {
    "monday": "09:00-22:00",
    "tuesday": "09:00-22:00",
    "wednesday": "09:00-22:00",
    "thursday": "09:00-22:00",
    "friday": "09:00-23:00",
    "saturday": "10:00-23:00",
    "sunday": "10:00-21:00"
  },
  "receiptFooter": "Thank you for visiting Smart Table Restaurant.",
  "printerConfig": null,
  "kitchenDisplayEnabled": true,
  "tableManagementEnabled": true
}
```

## Expected Dashboard Results From Sample Data

| Metric | Expected Result |
|---|---|
| Total Revenue | 56.29 |
| Total Orders | 2 |
| Average Order Value | 28.15 |
| Occupied Tables | 1 |
| Available Tables | 1 |
| Tips Collected | 7.00 |
| Tax Collected | 7.34 |
| Top Selling Item | Veggie Burger |
| Top Selling Quantity | 3 |
| Card Revenue | 41.35 |
| Cash Revenue | 14.94 |

## Dashboard Notes

This sample data follows the shared Firebase structure.

The timestamp values are written as readable ISO strings in this document, but in Firestore they should be stored as timestamp fields.

The pin values are sample-only and are written as null. Real PINs should not be stored in plain text.

The addOns field is used instead of addOn because it represents a list of add-on items.

The restaurantSettings document uses restaurantSettings/main as the document path, so settingsId is not required inside the document.

Receipt and printer fields are optional because printing may be a later or bonus feature.

For dashboard analytics, category can be read from menuItems using itemId.

If the team wants easier category-wise sales reports later, we can suggest copying category into each orderItem when the order is created.

If the team wants easier item-level revenue reports later, we can suggest adding itemTotal inside each orderItem.