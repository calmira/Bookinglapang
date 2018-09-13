package com.example.keviniswara.bookinglapang.admin.status.presenter

import com.example.keviniswara.bookinglapang.admin.status.AdminStatusDetailContract
import com.example.keviniswara.bookinglapang.model.Order
import com.example.keviniswara.bookinglapang.model.User
import com.example.keviniswara.bookinglapang.utils.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class AdminStatusDetailPresenter : AdminStatusDetailContract.Presenter {

    private var mView: AdminStatusDetailContract.View? = null

    override fun initOrderDetail(sport: String, startHour: String, endHour: String, customerEmail: String, status: String, date: String, fieldId: String) {

        if (startHour.toInt() < 10) mView?.setStartHour("0$startHour.00") else mView?.setStartHour("$startHour.00")
        if (endHour.toInt() < 10) mView?.setEndHour("0$endHour.00") else mView?.setEndHour("$endHour.00")
        mView?.setDate(date)
        mView?.setFieldId(fieldId)
        mView?.setSport(sport)
    }

    override fun bind(view: AdminStatusDetailContract.View) {
        mView = view
    }

    override fun unbind() {
        mView = null
    }

    override fun alreadyTransfer(orderId: String) {

        var userEmaiFromOrder: String

        val userRoot: DatabaseReference = Database.database.getReference("users")

        val orderRoot: DatabaseReference = Database.database.getReference("orders")

        orderRoot.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {
                mView?.makeToast("Terjadi kesalahan, silahkan coba lagi.")
            }

            override fun onDataChange(orderData: DataSnapshot?) {

                if (orderData != null) {
                    for (orderSnapshot in orderData.children) {

                        val order = orderSnapshot.getValue<Order>(Order::class.java)

                        if (order != null && order.orderId.equals(orderId)) {

                            orderRoot.child(orderSnapshot.key).child("status").setValue(2)

                            userEmaiFromOrder = order.customerEmail

                            var field = order.fieldId

                            var orderTime = order.startHour + " - " + order.endHour

                            var orderDate = order.date

                            userRoot.addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onCancelled(p0: DatabaseError?) {
                                    mView?.makeToast("Terjadi kesalahan, silahkan coba lagi.")
                                }

                                override fun onDataChange(userData: DataSnapshot?) {

                                    for (userSnapshot in userData!!.children) {

                                        val userEmail = userSnapshot.child("email").getValue<String>(String::class.java)
                                        val userField = userSnapshot.child("field").getValue<String>(String::class.java)

                                        if (userEmail.equals(userEmaiFromOrder)) {

                                            val userId = userSnapshot.key
                                            for (orderSnapshot in userData.child(userSnapshot.key).child("orders").children) {

                                                val order = orderSnapshot.getValue<Order>(Order::class.java)
                                                val orderKey = orderSnapshot.key

                                                if (order != null && order.orderId.equals(orderId)) {
                                                    userRoot.child(userId).child("orders").child(orderKey)
                                                            .child("status").setValue(2)
                                                    mView?.makeToast("Sukses mengubah status pesanan menjadi booked.")
                                                    mView?.finish()
                                                }
                                            }
                                            sendNotificationToUser(userId, 0)
                                        }

                                        if (field.equals(userField)) {
                                            val userId = userSnapshot.key
                                            sendNotificationToKeeper(userId, "Lapangan telah " +
                                                    "dibooking pada tanggal " + orderDate + ", pukul " +
                                                    orderTime)
                                        }
                                    }
                                }
                            })
                        }
                    }
                } else {
                    mView?.makeToast("Terjadi kesalahan, silahkan coba lagi.")
                }
            }

        })
    }

    override fun failed(orderId: String) {

        var userEmaiFromOrder: String

        val userRoot: DatabaseReference = Database.database.getReference("users")

        val orderRoot: DatabaseReference = Database.database.getReference("orders")

        orderRoot.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {
                mView?.makeToast("Terjadi kesalahan, silahkan coba lagi.")
            }

            override fun onDataChange(orderData: DataSnapshot?) {

                if (orderData != null) {
                    for (orderSnapshot in orderData.children) {

                        val order = orderSnapshot.getValue<Order>(Order::class.java)

                        if (order != null && order.orderId.equals(orderId)) {

                            orderRoot.child(orderSnapshot.key).child("status").setValue(3)

                            userEmaiFromOrder = order.customerEmail

                            userRoot.addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onCancelled(p0: DatabaseError?) {
                                    mView?.makeToast("Terjadi kesalahan, silahkan coba lagi.")
                                }

                                override fun onDataChange(userData: DataSnapshot?) {

                                    for (userSnapshot in userData!!.children) {

                                        val userEmail = userSnapshot.child("email").getValue<String>(String::class.java)

                                        if (userEmail.equals(userEmaiFromOrder)) {

                                            val userId = userSnapshot.key
                                            for (userOrderSnapshot in userData.child(userSnapshot.key).child("orders").children) {

                                                val order = userOrderSnapshot.getValue<Order>(Order::class.java)
                                                val orderKey = userOrderSnapshot.key

                                                if (order != null && order.orderId.equals(orderId)) {
                                                    userRoot.child(userId).child("orders").child(orderKey)
                                                            .child("status").setValue(3)
                                                    mView?.makeToast("Sukses mengubah status pesanan menjadi gagal.")
                                                    mView?.finish()

                                                    Database.addOneDayDeadline(orderSnapshot.key, userId, orderKey)
                                                }
                                            }
                                            sendNotificationToUser(userId, 1)
                                        }
                                    }
                                }
                            })
                        }
                    }
                } else {
                    mView?.makeToast("Terjadi kesalahan, silahkan coba lagi.")
                }
            }

        })
    }


    // type = 0, already paid, type = 1, not paid
    override fun sendNotificationToUser(userId: String, type: Int) {

        val usersReference: DatabaseReference = Database.database.getReference("users")

        var message: String = ""

        when (type) {
            0 -> message = "Pembayaran sukses"
            1 -> message = "Pembayaran gagal"
        }

        val notification = User.Notification(FirebaseAuth.getInstance().currentUser!!.uid, message)

        Database.addNotification(userId, notification)
    }

    override fun sendNotificationToKeeper(userId: String, message: String) {

        val usersReference: DatabaseReference = Database.database.getReference("users")

        val notification = User.Notification(FirebaseAuth.getInstance().currentUser!!.uid, message)

        Database.addNotification(userId, notification)
    }
}