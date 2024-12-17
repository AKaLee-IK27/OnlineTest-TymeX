package com.example.simplecurrencyconverter.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.simplecurrencyconverter.R
import com.example.simplecurrencyconverter.databinding.ActivityMainBinding
import com.example.simplecurrencyconverter.helper.Resource
import com.example.simplecurrencyconverter.helper.Utils
import com.example.simplecurrencyconverter.helper.toFlagEmoji
import com.example.simplecurrencyconverter.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.materialspinner.MaterialSpinner
import dagger.hilt.android.AndroidEntryPoint
import java.util.Currency
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var selectedItem1: String = "USD"
    private var selectedItem2: String = "VND"
    private var exchangeRatesFromUSD: Map<String, Double> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinner()
        setUpClickListener()
        initRates()
    }


    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun initRates() {
        togglePrgLoading(true)
        mainViewModel.fetchLatestRates()
        binding.etFirstCurrency.setText("1")
        binding.txtFrom.text = "${"US".toFlagEmoji()} - USD"
        binding.txtTo.text = "${"VN".toFlagEmoji()} - VND"

        mainViewModel.data.observe(this, Observer { result ->
            when (result.status) {
                Resource.Status.SUCCESS -> {
                    result.data?.rates?.let {
                        exchangeRatesFromUSD = it
                        doConversion()
                    } ?: showErrorSnackbar()
                    togglePrgLoading(false)
                }

                Resource.Status.ERROR -> {
                    showErrorSnackbar()
                    togglePrgLoading(false)
                }

                Resource.Status.LOADING -> togglePrgLoading(true)
            }
        })
    }

    private fun initSpinner() {
        val countries = getAllCountries()
        setupSpinner(
            binding.spnFirstCountry,
            binding.txtFrom,
            countries,
            "United States"
        ) { selectedItem1 = it }

        setupSpinner(
            binding.spnSecondCountry,
            binding.txtTo,
            countries,
            "Vietnam"
        ) { selectedItem2 = it }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSpinner(
        spinner: MaterialSpinner,
        currency: TextView,
        items: List<String>,
        defaultItem: String,
        onItemSelected: (String) -> Unit
    ) {
        spinner.setItems(items)
        spinner.setOnClickListener { Utils.hideKeyboard(this) }
        spinner.setOnItemSelectedListener { _, _, _, item ->
            val countryCode = getCountryCode(item.toString()) ?: ""
            val selectedCountry = getSymbol(countryCode) ?: ""
            onItemSelected(selectedCountry)
            currency.text = "${countryCode.toFlagEmoji()} - $selectedCountry"
            doConversion()
        }
        spinner.text = defaultItem
    }

    private fun getSymbol(countryCode: String): String? {
        return Locale.getAvailableLocales().find { it.country == countryCode }?.let {
            Currency.getInstance(it).currencyCode
        }
    }

    private fun getCountryCode(countryName: String): String? {
        return Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }
    }

    private fun getAllCountries(): List<String> {
        return Locale.getAvailableLocales()
            .mapNotNull { it.displayCountry.takeIf { it.isNotBlank() } }.distinct().sorted()
    }

    private fun setUpClickListener() {
        binding.btnExchange.setOnClickListener {
            if (binding.etFirstCurrency.text.isNullOrEmpty() || binding.etFirstCurrency.text.toString() == "0") {
                showSnackbar("Input a value in the first text field, result will be shown in the second text field")
            } else if (!Utils.isNetworkAvailable(this)) {
                showSnackbar("You are not connected to the internet")
            } else {
                doConversion()
            }
        }

        binding.btnSwapExchange.setOnClickListener {
            selectedItem1 = selectedItem2.also { selectedItem2 = selectedItem1 }
            binding.spnFirstCountry.text = binding.spnSecondCountry.text.also {
                binding.spnSecondCountry.text = binding.spnFirstCountry.text
            }
            doConversion()
        }

        binding.etFirstCurrency.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.etFirstCurrency.setSelection(binding.etFirstCurrency.length())
        }

        binding.etFirstCurrency.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (binding.etFirstCurrency.text.isNullOrEmpty() || binding.etFirstCurrency.text.toString() == "0") {
                    showSnackbar("Input a value in the first text field, result will be shown in the second text field")
                } else if (!Utils.isNetworkAvailable(this)) {
                    showSnackbar("You are not connected to the internet")
                } else {
                    doConversion()
                }
                true
            } else false
        }
    }

    private fun exchangeRate(from: String, to: String): Double {
        return exchangeRatesFromUSD[to]!! / exchangeRatesFromUSD[from]!!
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun doConversion() {
        Utils.hideKeyboard(this)


        val amount = binding.etFirstCurrency.text.toString().toDouble()
        val rate = exchangeRate(selectedItem1, selectedItem2)
        val convertedAmount = rate * amount
        binding.tvSecondCurrency.text = "${formatCurrency(convertedAmount)} $selectedItem2"
        binding.tvConvertRates.text =
            "1 $selectedItem1 = ${formatCurrency(rate)} $selectedItem2"
    }

    @SuppressLint("DefaultLocale")
    private fun formatCurrency(amount: Double): String {
        if (amount < 1000) {
            return String.format("%,.2f", amount)
        }
        return String.format("%,.0f", amount)
    }

    private fun togglePrgLoading(show: Boolean) {
        binding.prgLoading.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnExchange.visibility = if (show) View.GONE else View.VISIBLE
        binding.btnSwapExchange.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_LONG)
            .withColor(ContextCompat.getColor(this, R.color.dark_red))
            .setTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    private fun showErrorSnackbar() {
        showSnackbar("Ooops! something went wrong, Try again")
    }

    private fun Snackbar.withColor(colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }
}