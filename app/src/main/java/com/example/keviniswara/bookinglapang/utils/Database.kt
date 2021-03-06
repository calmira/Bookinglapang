package com.example.keviniswara.bookinglapang.utils

import android.util.Log
import com.example.keviniswara.bookinglapang.model.*
import com.example.keviniswara.bookinglapang.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


object Database {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val root: DatabaseReference = database.getReference("")

    fun setUsers(user: User) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        root.child("users").child(userId).setValue(user)
    }

    fun updateTokenId(newToken: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val tokenRef = root.child("users").child(userId)
        val tokenUpdate = HashMap<String, Any>()
        tokenUpdate["tokenId"] = newToken

        tokenRef.updateChildren(tokenUpdate)
    }

    fun addNewOrder(order: Order) {
        addNewOrderToUsersChild(order)
        addNewOrderToOrdersChild(order)
    }

    private fun addNewOrderToUsersChild(order: Order) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        root.child("users").child(userId).child("orders").push().setValue(order)
    }

    private fun addNewOrderToOrdersChild(order: Order) {
        root.child("orders").push().setValue(order)
    }

    fun addNotification(uid: String, notification: User.Notification) {
        root.child("users").child(uid).child("notifications").push().setValue(notification)
    }

    fun addTransaction(orderId: String, transaction: Transaction) {
        root.child("transactions").child(orderId).setValue(transaction)
    }

    fun addServerDate() {
        root.child("server_time").setValue(ServerValue.TIMESTAMP)
    }

    fun add15MinutesDeadline(orderId: String, userId: String, orderKey: String) {

        addServerDate()

        val timeRoot: DatabaseReference = database.getReference("server_time")
        val userRoot: DatabaseReference = database.getReference("users")
        val orderRoot: DatabaseReference = database.getReference("orders")

        timeRoot.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.d("ERROR", "failed to get server time")
            }

            override fun onDataChange(dateSnapshot: DataSnapshot?) {

                val dateInMillis: Long = dateSnapshot?.value as Long + 900000

                userRoot.child(userId).child("orders").child(orderKey)
                        .child("deadline").setValue(dateInMillis)

                orderRoot.child(orderId).child("deadline").setValue(dateInMillis)
            }
        })
    }

    fun addOneDayDeadline(orderId: String, userId: String, orderKey: String) {

        addServerDate()

        val timeRoot: DatabaseReference = database.getReference("server_time")
        val userRoot: DatabaseReference = database.getReference("users")
        val orderRoot: DatabaseReference = database.getReference("orders")

        timeRoot.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.d("ERROR", "failed to get server time")
            }

            override fun onDataChange(dateSnapshot: DataSnapshot?) {

                val dateInMillis: Long = dateSnapshot?.value as Long + 86400000

                userRoot.child(userId).child("orders").child(orderKey)
                        .child("deadline").setValue(dateInMillis)

                orderRoot.child(orderId).child("deadline").setValue(dateInMillis)
            }
        })
    }

    fun addBank(bank: Bank) {
        root.child("banks").push().setValue(bank)
    }

    fun addField(field: Field, fieldId: String) {
        root.child("fields").child(fieldId).setValue(field)
    }

    fun updateField(fieldName: String, address: String, contactPerson: String, phoneNumber: String, fieldId: String) {
        root.child("fields").child(fieldId).child("field_id").setValue(fieldName)
        root.child("fields").child(fieldId).child("address").setValue(address)
        root.child("fields").child(fieldId).child("contact_person").setValue(contactPerson)
        root.child("fields").child(fieldId).child("phone_number").setValue(phoneNumber)
    }

    fun addNewFindEnemy(findEnemy: FindEnemy) {
        root.child("find_enemy").push().setValue(findEnemy)
    }
}
