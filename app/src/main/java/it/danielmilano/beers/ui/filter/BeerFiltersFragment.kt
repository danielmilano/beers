package it.danielmilano.beers.ui.filter

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import it.danielmilano.beers.R
import it.danielmilano.beers.databinding.FragmentBeerFiltersBinding
import it.danielmilano.beers.databinding.FragmentSearchBeerBinding
import it.danielmilano.beers.ui.beerlist.SearchBeerViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class BeerFiltersFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBeerFiltersBinding? = null
    private val mBinding get() = _binding!!
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
        _binding = FragmentBeerFiltersBinding.bind(view)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // showing and dismissing dialog multiple times continuously in a short time
        // causes nav controller to doesn't update properly its current destination
        findNavController().popBackStack()
    }

    private val brewedBeforeDateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, _ ->
                val date = "${month + 1}/$year"
                mViewModel.setBrewedBefore(date)
            }
    private val brewedAfterDateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, _ ->
                val date = "${month + 1}/$year"
                mViewModel.setBrewedAfter(date)
            }

    private fun onClickDatePicker(brewedDate: BrewedDate) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
                requireContext(), when (brewedDate) {
            BrewedDate.BREWED_BEFORE -> brewedBeforeDateListener
            BrewedDate.BREWED_AFTER -> brewedAfterDateListener
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("MM/yyyy")
        when (brewedDate) {
            BrewedDate.BREWED_BEFORE -> {
                mViewModel.brewedAfterDate.value?.let {
                    val brewedAfterDate = formatter.parseDateTime(it)
                    datePickerDialog.datePicker.maxDate = brewedAfterDate.millis
                }
            }
            BrewedDate.BREWED_AFTER -> {
                mViewModel.brewedBeforeDate.value?.let {
                    val brewedBeforeDate = formatter.parseDateTime(it)
                    datePickerDialog.datePicker.minDate = brewedBeforeDate.millis
                }
            }
        }
        datePickerDialog.show()
    }

    enum class BrewedDate {
        BREWED_BEFORE,
        BREWED_AFTER,
    }
}