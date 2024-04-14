package com.example.trackpennyapps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class CategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        // Get the string array of categories
        val categoriesArray = resources.getStringArray(R.array.categories_array)

        // Find the LinearLayout to add TextViews dynamically
        val linearLayout = view.findViewById<LinearLayout>(R.id.categoriesLinearLayout)

        // Iterate through each category and create a TextView for it
        for (category in categoriesArray) {
            val textView = TextView(requireContext())
            textView.text = category
            textView.textSize = 16f // Change text size if needed
            textView.setTextColor(resources.getColor(android.R.color.black))
            textView.setBackgroundResource(R.drawable.edit_text_background) // Set background drawable

            // Add margin between TextViews
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.category_margin_bottom))
            textView.layoutParams = params

            // Add TextView to LinearLayout
            linearLayout.addView(textView)
        }

        return view
    }
}
