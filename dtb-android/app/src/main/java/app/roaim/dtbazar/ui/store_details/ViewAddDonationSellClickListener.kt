package app.roaim.dtbazar.ui.store_details

interface ViewAddDonationSellClickListener {
    fun onCancelClick()
    fun onAddDonationClick(amount: String)
    fun onAddSellClick(qty: String, nid: String, name: String)
}