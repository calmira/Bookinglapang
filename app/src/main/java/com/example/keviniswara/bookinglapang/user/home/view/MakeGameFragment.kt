package com.example.keviniswara.bookinglapang.user.home.view

import android.app.DatePickerDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.keviniswara.bookinglapang.R
import com.example.keviniswara.bookinglapang.databinding.FragmentUserMakeGameBinding
import com.example.keviniswara.bookinglapang.databinding.NumberPickerDialogBinding
import com.example.keviniswara.bookinglapang.user.home.MakeGameContract
import com.example.keviniswara.bookinglapang.user.home.presenter.MakeGamePresenter
import java.text.SimpleDateFormat
import java.util.*

class MakeGameFragment : Fragment(), MakeGameContract.View {

    private lateinit var mPresenter: MakeGameContract.Presenter

    private lateinit var mBinding: FragmentUserMakeGameBinding

    private lateinit var mCalendar: Calendar

    private val arrayOfString = arrayOf("00.00", "01.00", "02.00", "03.00", "04.00", "05.00",
            "06.00", "07.00", "08.00", "09.00", "10.00", "11.00", "12.00", "13.00", "14.00",
            "15.00", "16.00", "17.00", "18.00", "19.00", "20.00", "21.00", "22.00", "23.00")

    private var defaultTime = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_make_game,
                container, false)

        mPresenter = initPresenter()

        mPresenter.bind(this)

        mPresenter.retrieveListOfSportFromFirebase()

        mBinding.apply {
            date.setOnClickListener {
                initDatePicker()
            }

            time.setOnClickListener {
                initNumberPicker("Pilih jam", 0, 23, arrayOfString)
            }

            listOfSport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    mPresenter.retrieveListOfFieldFromFirebase(p0?.getItemAtPosition(p2).toString())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

            buttonBack.setOnClickListener {
                goBack()
            }

            buttonContinue.setOnClickListener {
                mPresenter.addFindEnemyToFirebase()
            }
        }

        return mBinding.root
    }

    override fun goBack() {
        fragmentManager?.popBackStack()
    }

    override fun initPresenter(): MakeGameContract.Presenter {
        return MakeGamePresenter()
    }

    override fun getFieldName(): String {
        return mBinding.listOfField.selectedItem.toString()
    }

    override fun getSport(): String {
        return mBinding.listOfSport.selectedItem.toString()
    }

    override fun getDate(): String {
        return mBinding.date.text.toString()
    }

    override fun getTime(): String {
        return defaultTime.toString()
    }

    private fun updateLabel() {
        val dateFormat = "dd/MM/yy"
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        mBinding.date.setText(sdf.format(mCalendar.getTime()))
    }

    override fun initDatePicker() {
        mCalendar = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, monthOfYear)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        DatePickerDialog(activity, date, mCalendar
                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    // type 0 = start, 1 = end
    override fun initNumberPicker(title: String, minValue: Int, maxValue: Int, array: Array<String>) {


        val mBottomSheetDialog: BottomSheetDialog = BottomSheetDialog(activity!!, R.style.BottomSheetDialogTheme)

        val mBindingNumber: NumberPickerDialogBinding = DataBindingUtil.inflate(activity!!.layoutInflater,
                R.layout.number_picker_dialog, null, false)
        mBottomSheetDialog.setContentView(mBindingNumber.root)

        mBindingNumber.tvDialogTitle.text = title

        val numberPicker = mBindingNumber.numberPicker

        numberPicker.minValue = minValue
        numberPicker.maxValue = maxValue
        numberPicker.displayedValues = array
        numberPicker.wrapSelectorWheel = false
        numberPicker.value = defaultTime

        mBindingNumber.buttonSave.setOnClickListener({
            defaultTime = numberPicker.value
            mBinding.time.setText(arrayOfString[numberPicker.value])
            mBottomSheetDialog.dismiss()
        })

        mBottomSheetDialog.show()
    }

    override fun initListOfFieldDropdown(listOfField: List<String>) {
        val adapter = ArrayAdapter(activity!!.applicationContext, R.layout.spinner_item, listOfField)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        mBinding.listOfField.adapter = adapter
    }

    override fun initListOfSportDropdown(listOfSport: List<String>) {
        val adapter = ArrayAdapter(activity!!.applicationContext, R.layout.spinner_item, listOfSport)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        mBinding.listOfSport.adapter = adapter
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}