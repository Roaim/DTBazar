package app.roaim.dtbazar.ui.food

interface ViewAddFoodClickListener {
    fun onCancelClick()
    fun onAddFoodClick(
        name: String,
        currency: CharSequence,
        unit: String,
        sPrice: String,
        ePrice: String
    )
}