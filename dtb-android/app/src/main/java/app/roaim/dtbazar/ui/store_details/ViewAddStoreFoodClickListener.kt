package app.roaim.dtbazar.ui.store_details

interface ViewAddStoreFoodClickListener {
    fun onCancelClick()
    fun onAddStoreFoodClick(stockQty: String, unitPrice: String)
}