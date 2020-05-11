package app.roaim.dtbazar.di;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.roaim.dtbazar.ui.DtbViewModelFactory
import app.roaim.dtbazar.ui.dashboard.DashboardViewModel
import app.roaim.dtbazar.ui.home.HomeViewModel
import app.roaim.dtbazar.ui.notifications.NotificationsViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindUserViewModel(userViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun bindRepoViewModel(repoViewModel: NotificationsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DtbViewModelFactory): ViewModelProvider.Factory
}
