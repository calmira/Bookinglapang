package com.example.keviniswara.bookinglapang.order.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.keviniswara.bookinglapang.R
import com.example.keviniswara.bookinglapang.databinding.FragmentOrderDetailBinding
import com.example.keviniswara.bookinglapang.order.OrderDetailContract
import com.example.keviniswara.bookinglapang.order.presenter.OrderDetailPresenter

class OrderDetailFragment : Fragment(), OrderDetailContract.View {

    private lateinit var mPresenter: OrderDetailContract.Presenter
    private lateinit var mBinding: FragmentOrderDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)

        mPresenter = initPresenter()

        mPresenter.bind(this)

        val sport = arguments!!.getString("sport")
        val startHour = arguments!!.getString("startHour")
        val endHour = arguments!!.getString("endHour")
        val customerEmail = arguments!!.getString("customerEmail")
        val status = arguments!!.getString("status")
        val date = arguments!!.getString("date")
        val fieldId = arguments!!.getString("fieldId")

        mPresenter.initOrderDetail(sport, startHour, endHour, customerEmail, status, date, fieldId)

        return mBinding.root
    }

    override fun initPresenter(): OrderDetailContract.Presenter {
        return OrderDetailPresenter()
    }

    override fun setFieldId(fieldId: String) {
        mBinding.fieldId.text = fieldId
    }

    override fun setSport(sport: String) {
        mBinding.sport.text = sport
    }

    override fun setStartHour(startHour: String) {
        mBinding.startHour.text = startHour
    }

    override fun setEndHour(endHour: String) {
        mBinding.endHour.text = endHour
    }

    override fun setDate(date: String) {
        mBinding.date.text = date
    }

    override fun setStatusNotVerified() {
        Log.d("ORDER DETAIL FRAGMENT", "status not verified")
    }

    override fun setStatusNotPaid() {
        Log.d("ORDER DETAIL FRAGMENT", "status not paid")
        mBinding.verificationButton.text = "Sudah diverifikasi"
    }

    override fun setStatusDone() {
        Log.d("ORDER DETAIL FRAGMENT", "status done")
        mBinding.verificationButton.text = "Sudah diverifikasi"
        mBinding.paidButton.text = "Sudah dibayar"
    }

    override fun setStatusCancelled() {
        Log.d("ORDER DETAIL FRAGMENT", "status cancelled")
    }
}