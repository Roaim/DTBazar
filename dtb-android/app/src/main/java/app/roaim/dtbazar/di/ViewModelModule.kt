package app.roaim.dtbazar.di;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.roaim.dtbazar.ui.DtbViewModelFactory
import app.roaim.dtbazar.ui.home.HomeViewModel
import app.roaim.dtbazar.ui.login.LoginViewModel
import app.roaim.dtbazar.ui.food.FoodViewModel
import app.roaim.dtbazar.ui.store.StoreViewModel
import app.roaim.dtbazar.ui.store_details.StoreDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(userViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(userViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoreViewModel::class)
    abstract fun bindStoreViewModel(storeViewModel: StoreViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoreDetailsViewModel::class)
    abstract fun bindStoreDetailsViewModel(storeDetailsViewModel: StoreDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FoodViewModel::class)
    abstract fun bindFoodViewModel(repoViewModel: FoodViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DtbViewModelFactory): ViewModelProvider.Factory
}
