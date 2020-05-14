package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.DonationDao
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.db.ProfileDao
import app.roaim.dtbazar.db.StoreDao
import app.roaim.dtbazar.model.*
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
class InfoRepository @Inject constructor(
    private val ipInfoDao: IpInfoDao,
    private val storeDao: StoreDao,
    private val donationDao: DonationDao,
    private val profileDao: ProfileDao,
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource
) {

    fun getProfile(): LiveData<Result<Profile>> =
        prefDataSource.getUid()?.let { uid ->
            profileDao.findById(uid).map {
                success(it)
            }
        } ?: liveData {
        emit(loading())
        val result = try {
            apiService.getProfile().getResult {
                profileDao.insert(it)
                prefDataSource.saveUid(it.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            failed<Profile>(e.message)
        }
        emit(result)
    }

    fun getIpInfo(): LiveData<Result<IpInfo>> =
        prefDataSource.getIp()?.let { ip ->
            ipInfoDao.findByIp(ip).map { ipInfo ->
                success(ipInfo)
            }
        } ?: liveData {
            emit(loading())
            val result = try {
                apiService.getIpInfo().getResult { saveIpInfo(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                failed<IpInfo>(e.message)
            }
            emit(result)
        }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        ipInfoDao.insert(ipInfo)
        prefDataSource.saveIp(ipInfo.ip)
    }

    //    TODO move to another repository

    fun getMyCachedDonations() = donationDao.findAll()

    fun getMyDonations(): LiveData<Result<List<Donation>>> =
        liveData {
            emit(loading())
            val result = try {
                apiService.getMyDonations().getResult { donationDao.insert(*it.toTypedArray()) }
            } catch (e: Exception) {
                e.printStackTrace()
                failed<List<Donation>>(e.message)
            }
            emit(result)
        }

    fun getMyCachedStores() = storeDao.findAll()

    fun getMyStores(): LiveData<Result<List<Store>>> =
        liveData {
            emit(loading())
            val result = try {
                apiService.getMyStores().getResult { storeDao.insert(*it.toTypedArray()) }
            } catch (e: Exception) {
                e.printStackTrace()
                failed<List<Store>>(e.message)
            }
            emit(result)
        }

    fun saveStore(storeBody: StorePostBody): LiveData<Result<Store>> = liveData {
        emit(loading())
        val result = try {
            apiService.saveStore(storeBody).getResult { storeDao.insert(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            failed<Store>(e.message)
        }
        emit(result)
    }
}