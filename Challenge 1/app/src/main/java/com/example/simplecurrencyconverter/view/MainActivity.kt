package com.example.simplecurrencyconverter.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.simplecurrencyconverter.R
import com.example.simplecurrencyconverter.databinding.ActivityMainBinding
import com.example.simplecurrencyconverter.helper.Resource
import com.example.simplecurrencyconverter.helper.Utils
import com.example.simplecurrencyconverter.model.ApiResponse
import com.example.simplecurrencyconverter.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Currency
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var selectedItem1: String? = ""
    private var selectedItem2: String? = ""
    private var exchangeRatesFromUSD: Map<String, Double> = emptyMap()

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState)

        Utils.makeStatusBarTransparent(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        initSpinner()

        setUpClickListener()

        initRates()

        binding.etFirstCurrency.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etFirstCurrency.setSelection(binding.etFirstCurrency.length())
            }
        }

        binding.etFirstCurrency.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                doConversion()
                true
            } else {
                false
            }
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun initRates() {
        mainViewModel.fetchLatestRates()
        selectedItem1 = "USD"
        selectedItem2 = "VND"
        binding.etFirstCurrency.setText("1")

        mainViewModel.data.observe(this, Observer<Resource<ApiResponse>> { result ->
            when (result.status) {
                Resource.Status.SUCCESS -> {
                    if (result.data?.rates != null) {
                        exchangeRatesFromUSD = result.data.rates

                        val formattedString = String.format(
                            "%,.2f", exchangeRatesFromUSD[selectedItem2]
                        )

                        binding.tvSecondCurrency.text = "$formattedString $selectedItem2"

                        binding.tvConvertRates.text = "1 USD = $formattedString VND"

                        togglePrgLoading()
                    } else {
                        val layout = binding.mainLayout
                        Snackbar.make(
                            layout, "Ooops! something went wrong, Try again", Snackbar.LENGTH_LONG
                        ).withColor(ContextCompat.getColor(this, R.color.dark_red))
                            .setTextColor(ContextCompat.getColor(this, R.color.white)).show()

                        togglePrgLoading()
                    }
                }

                Resource.Status.ERROR -> {
                    val layout = binding.mainLayout
                    Snackbar.make(
                        layout, "Oopps! Something went wrong, Try again", Snackbar.LENGTH_LONG
                    ).withColor(ContextCompat.getColor(this, R.color.dark_red))
                        .setTextColor(ContextCompat.getColor(this, R.color.white)).show()
                    togglePrgLoading()
                }

                Resource.Status.LOADING -> {
                    togglePrgLoading()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initSpinner() {

        val firstSpinner = binding.spnFirstCountry
        firstSpinner.setItems(getAllCountries())

        firstSpinner.setOnClickListener {
            Utils.hideKeyboard(this)
        }
        firstSpinner.setOnItemSelectedListener { view, position, id, item ->
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            selectedItem1 = currencySymbol
            binding.txtFrom.text = currencySymbol
            doConversion()
        }
        firstSpinner.setOnClickListener {
            Utils.hideKeyboard(this)
        }
        firstSpinner.text = "United States"

        val secondSpinner = binding.spnSecondCountry
        secondSpinner.setItems(getAllCountries())
        secondSpinner.setOnItemSelectedListener { view, position, id, item ->
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            selectedItem2 = currencySymbol
            binding.txtTo.text = currencySymbol
            doConversion()
        }
        secondSpinner.setOnClickListener {
            Utils.hideKeyboard(this)
        }
        secondSpinner.text = "Viet Nam"
    }

    private fun getSymbol(countryCode: String?): String? {
        val availableLocales = Locale.getAvailableLocales()
        for (i in availableLocales.indices) {
            if (availableLocales[i].country == countryCode) return Currency.getInstance(
                availableLocales[i]
            ).currencyCode
        }
        return ""
    }


    private fun getCountryCode(countryName: String) =
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }

    private fun getAllCountries(): ArrayList<String> {

        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()

        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()

        return countries
    }

    private fun setUpClickListener() {

        binding.btnExchange.setOnClickListener {

            //check if the input is empty
            val numberToConvert = binding.etFirstCurrency.text.toString()

            if (numberToConvert.isEmpty() || numberToConvert == "0") {
                Snackbar.make(
                    binding.mainLayout,
                    "Input a value in the first text field, result will be shown in the second text field",
                    Snackbar.LENGTH_LONG
                ).withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white)).show()
            }

            //check if internet is available
            else if (!Utils.isNetworkAvailable(this)) {
                Snackbar.make(
                    binding.mainLayout,
                    "You are not connected to the internet",
                    Snackbar.LENGTH_LONG
                ).withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white)).show()
            }

            //carry on and convert the value
            else {
                doConversion()
            }
        }

        binding.btnSwapExchange.setOnClickListener {
            val temp = selectedItem1
            selectedItem1 = selectedItem2
            selectedItem2 = temp

            val tmp = binding.spnFirstCountry.text
            binding.spnFirstCountry.text = binding.spnSecondCountry.text
            binding.spnSecondCountry.text = tmp

            doConversion()
        }
    }

    private fun exchangeRate(from: String, to: String): Double {
        return exchangeRatesFromUSD[to]!! / exchangeRatesFromUSD[from]!!
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun doConversion() {
        Utils.hideKeyboard(this)

        val from: String = selectedItem1!!;
        val to: String = selectedItem2!!;
        val amount: Double = binding.etFirstCurrency.text.toString().toDouble()

        val rate = exchangeRate(from, to)

        val convertedAmount = rate * amount

        val formattedRate = String.format("%,.2f", rate)
        val formattedAmount = String.format("%,.2f", convertedAmount)

        binding.tvSecondCurrency.text = "$formattedAmount $to"

        binding.tvConvertRates.text = "1 $from = $formattedRate $to"
    }

    private fun togglePrgLoading() {
        if (binding.prgLoading.visibility == View.GONE) {
            binding.prgLoading.visibility = View.VISIBLE
            binding.btnExchange.visibility = View.GONE
            binding.btnSwapExchange.visibility = View.GONE
        } else {
            binding.prgLoading.visibility = View.GONE
            binding.btnExchange.visibility = View.VISIBLE
            binding.btnSwapExchange.visibility = View.VISIBLE
        }
    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }
}