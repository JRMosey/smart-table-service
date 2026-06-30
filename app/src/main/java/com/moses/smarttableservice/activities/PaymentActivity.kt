package com.moses.smarttableservice.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.Payment
import com.moses.smarttableservice.repositories.OrderRepository
import com.moses.smarttableservice.repositories.PaymentRepository
import com.moses.smarttableservice.repositories.TableRepository

class PaymentActivity : AppCompatActivity() {

    private val orderRepository = OrderRepository()
    private val paymentRepository = PaymentRepository()
    private val tableRepository = TableRepository()

    private lateinit var tvPaymentDetails: TextView
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var btnPayOrder: Button

    private var orderId: String = ""
    private var tableId: String = ""
    private var total: Double = 0.0
    private var subtotal: Double = 0.0
    private var taxAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        tvPaymentDetails = findViewById(R.id.tvPaymentDetails)
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod)
        btnPayOrder = findViewById(R.id.btnPayOrder)

        orderId = intent.getStringExtra("orderId") ?: ""

        setupPaymentMethods()
        loadOrder()

        btnPayOrder.setOnClickListener {
            payOrder()
        }
    }

    private fun setupPaymentMethods() {
        val methods = listOf("cash", "card", "online")
        spinnerPaymentMethod.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            methods
        )
    }

    private fun loadOrder() {
        orderRepository.getOrderById(
            orderId = orderId,
            onSuccess = { order ->
                if (order == null) {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@getOrderById
                }

                tableId = order.tableId
                subtotal = order.subtotal
                taxAmount = order.taxAmount
                total = order.total

                tvPaymentDetails.text =
                    "Order: ${order.orderId.take(6)}\n" +
                            "Table: ${order.tableId}\n" +
                            "Subtotal: $${"%.2f".format(subtotal)}\n" +
                            "Tax: $${"%.2f".format(taxAmount)}\n" +
                            "Total: $${"%.2f".format(total)}"
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun payOrder() {
        val method = spinnerPaymentMethod.selectedItem.toString()

        val payment = Payment(
            orderId = orderId,
            tableId = tableId,
            amountPaid = total,
            subtotal = subtotal,
            taxAmount = taxAmount,
            tipAmount = 0.0,
            paymentMethod = method,
            status = "paid"
        )

        paymentRepository.createPayment(
            payment = payment,
            onSuccess = {
                orderRepository.updateOrderStatus(
                    orderId = orderId,
                    status = "paid",
                    onSuccess = {
                        tableRepository.updateTableStatus(
                            tableId = tableId,
                            status = "available",
                            onSuccess = {
                                Toast.makeText(this, "Payment completed", Toast.LENGTH_SHORT).show()
                                finish()
                            },
                            onFailure = { exception ->
                                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}