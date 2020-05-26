package app.roaim.dtbazar.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.dao.DonationDao
import app.roaim.dtbazar.model.Donation
import app.roaim.dtbazar.model.DonationPostBody
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
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource
) : Loggable {

    fun getMyCachedDonations(uid: String) = donationDao.findAllByUid(uid)

    fun getMyDonations(): LiveData<Result<List<Donation>>> =
        liveData {
            emit(loading())
            val result = try {
                apiService.getMyDonations().getResult { donationDao.refresh(*it.toTypedArray()) }
            } catch (e: Exception) {
                log("getMyDonations", e)
                failed<List<Donation>>(e.message)
            }
            emit(result)
        }

    fun addDonation(donationPostBody: DonationPostBody) = liveData<Result<Donation>> {
        emit(loading())
        val result = try {
            apiService.saveDonation(donationPostBody)
                .getResult { if (prefDataSource.getUid() == it.donorId) donationDao.insert(it) }
        } catch (e: Exception) {
            log("addDonation", e)
            failed<Donation>(e.message)
        }
        emit(result)
    }
}