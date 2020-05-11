package app.roaim.dtbazar.di;

import app.roaim.dtbazar.ui.dashboard.DashboardFragment
import app.roaim.dtbazar.ui.home.HomeFragment
import app.roaim.dtbazar.ui.notifications.NotificationsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): NotificationsFragment
}
