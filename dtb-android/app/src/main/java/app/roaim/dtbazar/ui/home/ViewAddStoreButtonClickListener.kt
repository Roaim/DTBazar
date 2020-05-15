package app.roaim.dtbazar.ui.home

interface ViewAddStoreButtonClickListener {
    fun onCancelClick()
    // intentionally changed sequence for edittext error focus order
    fun onAddStoreClick(mobile: String, address: String, name: String)
}