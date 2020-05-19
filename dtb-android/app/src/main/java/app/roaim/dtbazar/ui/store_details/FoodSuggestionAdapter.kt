package app.roaim.dtbazar.ui.store_details

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import app.roaim.dtbazar.R
import app.roaim.dtbazar.model.Food

class FoodSuggestionAdapter(context: Context) :
    ArrayAdapter<Food>(context, android.R.layout.simple_list_item_1, android.R.id.text1) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: super.getView(position, convertView, parent)
        val item = getItem(position)
        (view as TextView).text =
            context.getString(
                R.string.title_food_suggestion_spinner,
                item?.name,
                item?.currency,
                item?.unit
            )
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: super.getView(position, convertView, parent)
        val item = getItem(position)
        (view as TextView).text =
            context.getString(
                R.string.title_food_suggestion_spinner,
                item?.name,
                item?.currency,
                item?.unit
            )
        return view
    }
}