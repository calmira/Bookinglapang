package com.example.keviniswara.bookinglapang.home.presenter

import android.widget.ArrayAdapter
import com.example.keviniswara.bookinglapang.home.SearchFieldContract
import com.example.keviniswara.bookinglapang.model.Field
import com.google.firebase.database.*

class SearchFieldPresenter: SearchFieldContract.Presenter {

    private var mView: SearchFieldContract.View? = null

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val fieldReference: DatabaseReference = database.getReference("").child("fields")

    override fun bind(view: SearchFieldContract.View) {
        mView = view
    }

    override fun unbind() {
        mView = null
    }

    override fun retrieveListOfFieldFromFirebase() {
        var listOfField = mutableListOf<String>()
        fieldReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                fetchDataFieldName(p0, listOfField)

                mView!!.initListOfFieldDropdown(listOfField)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    private fun fetchDataFieldName(dataSnapshot: DataSnapshot?, listOfField: MutableList<String>) {
        listOfField.clear()
        for (ds in dataSnapshot!!.children) {
            val name = ds.getValue(Field::class.java)!!.field_id
            listOfField.add(name)
        }
    }

    override fun retrieveListOfSportFromFirebase(fieldName : String) {
        var listOfSport = mutableListOf<String>()
        fieldReference.child(fieldName).child("sports").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                fetchDataSportName(p0, listOfSport)

                mView!!.initListOfSportDropdown(listOfSport)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    private fun fetchDataSportName(dataSnapshot: DataSnapshot?, listOfSport: MutableList<String>) {
        listOfSport.clear()
        for (ds in dataSnapshot!!.children) {
            val name = ds.getValue(Field.Sport::class.java)!!.sport_name
            listOfSport.add(name)
        }
    }
}