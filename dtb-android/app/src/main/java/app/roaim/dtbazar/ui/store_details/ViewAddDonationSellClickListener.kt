package app.roaim.dtbazar.ui.store_details

interface ViewAddDonationSellClickListener {
    fun onCancelClick()
    fun onAddDonationClick(quantity: String)
    fun onAddSellClick(qty: String, nid: String, name: String)
}