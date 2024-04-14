package com.example.trackpennyapps

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class SettingFragment : Fragment() {

    // FAQ
    private val faqs = arrayOf(
        "How do I login/signup?",
        "How do I add a transaction?",
        "How can I add a budget?",
        "Can I edit a transaction?",
        "Can I delete a transaction?",
        "Are there any fees for using the app?"
    )

    private lateinit var answerTextView: TextView
    private lateinit var themeSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // Answer TextView
        answerTextView = view.findViewById(R.id.answerTextView)

        // Theme Switch
        themeSwitch = view.findViewById(R.id.themeSwitch)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        // Set up logout button
        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
        }

        // FAQ Spinner
        val faqSpinner: Spinner = view.findViewById(R.id.faqSpinner)
        val faqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, faqs)
        faqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        faqSpinner.adapter = faqAdapter
        faqSpinner.onItemSelectedListener = FAQSpinnerItemSelectedListener()

        // Set item selection listener for FAQ Spinner
        faqSpinner.onItemSelectedListener = FAQSpinnerItemSelectedListener()

        // Set click listener for the theme switch
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
        }

        return view
    }

    inner class FAQSpinnerItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedItem = faqs[position]
            val answer = getAnswer(selectedItem)
            displayAnswer(answer)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Do nothing
        }
    }

    private fun getAnswer(question: String): String {
        // Get answer based on the selected question
        return when (question) {
            "How do I login/signup?" -> "Simply tap on the 'Log In' or 'Sign Up' button on the app's main screen, then follow the prompts to enter your credentials."
            "How do I add a transaction?" -> "Navigate to the 'Records' page, tap the '+' icon, fill in the transaction details such as category, amount, date and description, then save."
            "How can I add a budget?" -> "Go to the 'Budget' page, enter the budget amount and save."
            "Can I edit a transaction?" -> "Yes, on the 'Records' page, tap on the transaction you want to edit, then make the necessary changes and save."
            "Can I delete a transaction?" -> "In the 'Records' page, tap on 'trash' icon below transaction you want to delete."
            "Are there any fees for using the app?" -> "No, all features of the app are free to use."
            else -> "Answer not available"
        }
    }

    private fun displayAnswer(answer: String) {
        answerTextView.text = answer
    }

    private fun logout() {
        // Clear login status from SharedPreferences
        sharedPreferences.edit().putBoolean("loggedIn", false).apply()
        // Redirect to the login page
        val intent = Intent(requireContext(), LoginPage::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check the current mode and update the switch accordingly
        themeSwitch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Save the selected theme preference when the fragment is destroyed
        val isDarkMode = themeSwitch.isChecked
        requireActivity().getPreferences(Context.MODE_PRIVATE).edit().putBoolean("isDarkMode", isDarkMode).apply()
    }
}
