package it.danielmilano.beers.ui.filter

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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

    private val args by navArgs<BeerFiltersFragmentArgs>()

    private lateinit var mBinding: FragmentBeerFiltersBinding
    private val viewModel by activityViewModels<SearchBeerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_Demo_BottomSheetDialog)
    }

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
            close.setOnClickListener { dismiss() }
            reset.setOnClickListener {
                mBinding.brewedBeforeDate = null
                mBinding.brewedAfterDate = null
            }
            brewedBeforeDatePicker.setOnClickListener { onClickDatePicker(BrewedDate.BREWED_BEFORE) }
            brewedAfterDatePicker.setOnClickListener { onClickDatePicker(BrewedDate.BREWED_AFTER) }
            buttonApply.setOnClickListener {
                val request = BeerRequest(
                    args.beerName,
                    mBinding.brewedBeforeDate?.toString("MM/yyyy"),
                    mBinding.brewedAfterDate?.toString("MM/yyyy")
                )
                viewModel.searchBeers(request)
                dismiss()
            }
        }

        //Restoring state from viewmodel
        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("MM/yyyy")
        viewModel.getCurrentRequest()?.brewedBefore?.let {
            mBinding.brewedBeforeDate = formatter.parseDateTime(it)
        }
        viewModel.getCurrentRequest()?.brewedAfter?.let {
            mBinding.brewedAfterDate = formatter.parseDateTime(it)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // showing and dismissing dialog multiple times continuously in a short time
        // causes nav controller to doesn't update properly its current destination
        findNavController().popBackStack()
    }

    private val brewedBeforeDateListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            mBinding.brewedBeforeDate = DateTime(year, month + 1, dayOfMonth, 0, 0)
        }
    private val brewedAfterDateListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            mBinding.brewedAfterDate = DateTime(year, month + 1, dayOfMonth, 0, 0)
        }

    private fun onClickDatePicker(brewedDate: BrewedDate) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(), when (brewedDate) {
                BrewedDate.BREWED_BEFORE -> brewedBeforeDateListener
                BrewedDate.BREWED_AFTER -> brewedAfterDateListener
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }

    enum class BrewedDate {
        BREWED_BEFORE,
        BREWED_AFTER,
    }
}