package com.forthgreen.app.views.dialogfragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle

/**
 * Created by ShrayChona on 03-10-2018.
 * @description this class is used for DatePicker dialog
 */
class DatePickerDialogFragment : androidx.fragment.app.DialogFragment() {

    private lateinit var onDateSetListener: DatePickerDialog.OnDateSetListener

    companion object {

        const val BUNDLE_EXTRAS_YEAR = "year"
        const val BUNDLE_EXTRAS_MONTH = "month"
        const val BUNDLE_EXTRAS_DAY = "day"
        const val BUNDLE_EXTRAS_MIN_DATE = "minDate"
        const val BUNDLE_EXTRAS_MAX_DATE = "maxDate"

        fun newInstance(year: Int, month: Int, day: Int,
                        minDate: Long?, maxDate: Long?): DatePickerDialogFragment {
            val datePickerDialogFragment = DatePickerDialogFragment()
            val bundle = Bundle()
            bundle.putInt(BUNDLE_EXTRAS_YEAR, year)
            bundle.putInt(BUNDLE_EXTRAS_MONTH, month)
            bundle.putInt(BUNDLE_EXTRAS_DAY, day)
            bundle.putLong(BUNDLE_EXTRAS_MIN_DATE, minDate!!)
            bundle.putLong(BUNDLE_EXTRAS_MAX_DATE, maxDate!!)
            datePickerDialogFragment.arguments = bundle
            return datePickerDialogFragment
        }
    }

    fun setCallBack(onDateSetListener: DatePickerDialog.OnDateSetListener) {
        this.onDateSetListener = onDateSetListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val datePickerDialog: DatePickerDialog
        if (null != arguments) {
            datePickerDialog = DatePickerDialog(requireContext(),
                    onDateSetListener, arguments!!.getInt(BUNDLE_EXTRAS_YEAR),
                    arguments!!.getInt(BUNDLE_EXTRAS_MONTH), arguments!!
                    .getInt(BUNDLE_EXTRAS_DAY))

            // get and set min date
            var date: Long? = arguments!!.getLong(BUNDLE_EXTRAS_MIN_DATE, 0)
            datePickerDialog.datePicker.minDate = date!!

            // get and set max date
            date = arguments!!.getLong(BUNDLE_EXTRAS_MAX_DATE, 0)
            datePickerDialog.datePicker.maxDate = date
        } else {
            datePickerDialog = DatePickerDialog(requireContext(),
                    onDateSetListener, 1, 1, 1951)
        }
        return datePickerDialog
    }

}