package app.roaim.dtbazar.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.db.DonationDao
import app.roaim.dtbazar.model.Donation
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
class DonationRepository @Inject constructor(
    private val donationDao: DonationDao,
    private val apiService: ApiService
) : Loggable {

    fun getMyCachedDonations() = donationDao.findAll()

    fun getMyDonations(): LiveData<Result<List<Donation>>> =
        liveData {
            emit(loading())
            val result = try {
                apiService.getMyDonations().getResult { donationDao.insert(*it.toTypedArray()) }
            } catch (e: Exception) {
                log("getMyDonations", e)
                failed<List<Donation>>(e.message)
            }
            emit(result)
        }
}