package it.danielmilano.beers.ui.filter

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import it.danielmilano.beers.R
import it.danielmilano.beers.data.BeerRequest
import it.danielmilano.beers.databinding.FragmentBeerFiltersBinding
import it.danielmilano.beers.ui.beerlist.SearchBeerViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class BeerFiltersFragment : BottomSheetDialogFragment() {

    private lateinit var mBinding: FragmentBeerFiltersBinding
    private val mViewModel by activityViewModels<SearchBeerViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beer_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentBeerFiltersBinding.bind(view)

        mBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            close.setOnClickListener { dismiss() }
            reset.setOnClickListener {
                mViewModel.setBrewedBefore(null)
                mViewModel.setBrewedAfter(null)
            }
            brewedBeforeDatePicker.setOnClickListener { onClickDatePicker(BrewedDate.BREWED_BEFORE) }
            brewedAfterDatePicker.setOnClickListener { onClickDatePicker(BrewedDate.BREWED_AFTER) }
            buttonApply.setOnClickListener {
                mViewModel.searchBeers()
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // showing and dismissing dialog multiple times continuously in a short time
        // causes nav controller to doesn't update properly its current destination
        findNavController().popBackStack()
    }

    private val brewedBeforeDateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, _ ->
                mViewModel.setBrewedBefore("${month + 1}/$year")
            }
    private val brewedAfterDateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, _ ->
                mViewModel.setBrewedAfter("${month + 1}/$year")
            }

    private fun onClickDatePicker(brewedDate: BrewedDate) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
                requireContext(), when (brewedDate) {
            BrewedDate.BREWED_BEFORE -> brewedBeforeDateListener
            BrewedDate.BREWED_AFTER -> brewedAfterDateListener
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        datePickerDialog.datePicker.maxDate = DateTime.now().millis
        datePickerDialog.show()
    }

    enum class BrewedDate {
        BREWED_BEFORE,
        BREWED_AFTER,
    }
}