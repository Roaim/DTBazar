package app.roaim.dtbazar.ui.food

interface ViewAddFoodClickListener {
    fun onCancelClick()
    fun onAddFoodClick(
        ePrice: String,
        currency: CharSequence,
        unit: String,
        sPrice: String,
        name: String
    )
}