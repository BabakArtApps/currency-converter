package com.babak.rates.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.babak.rates.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_rates.*
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RatesFragment : DaggerFragment(), RatesAdapter.AdapterInterface {
    private var param1: String? = null
    private var param2: String? = null
    private var listenerRates: OnRatesFragmentInteractionListener? = null
    private val rateData = mutableListOf<Rate>()
    private val adapter = RatesAdapter(rateData)


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val ratesViewModel by viewModels<RatesViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ratesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter.listener = this
        ratesRecyclerView.adapter = adapter

        ratesViewModel.items.observe(viewLifecycleOwner, Observer { data ->
            rateData.clear()
            rateData.addAll(data)
            adapter.notifyItemRangeChanged(1, rateData.size - 1)

        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRatesFragmentInteractionListener) {
            listenerRates = context
        } else {
            throw RuntimeException("$context must implement OnRatesFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerRates = null
    }

    override fun onItemClick(rate: Rate, position: Int) {

        ratesViewModel.changeBaseCurrency(rate.currency)
        adapter.notifyItemMoved(position, 0)
        adapter.notifyItemChanged(0)
    }

    override fun onTextChange(newValue: String) {
        ratesViewModel.valueChanged(newValue)
    }


    interface OnRatesFragmentInteractionListener {
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RatesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
