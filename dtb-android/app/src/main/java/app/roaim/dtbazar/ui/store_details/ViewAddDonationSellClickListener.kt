package app.roaim.dtbazar.ui.store_details

import app.roaim.dtbazar.model.StoreFood

interface ViewAddDonationSellClickListener {
    fun onDialogCancelClick()
    fun onAddDonationClick(storeFood: StoreFood?, quantity: String)
    fun onAddSellClick(storeFood: StoreFood?, qty: String, nid: String, name: String)
    fun onAddStockClick(storeFood: StoreFood?, qty: String, unitPrice: String)
}