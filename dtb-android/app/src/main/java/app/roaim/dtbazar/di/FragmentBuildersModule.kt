package app.roaim.dtbazar.di;

import app.roaim.dtbazar.ui.home.HomeFragment
import app.roaim.dtbazar.ui.login.LoginFragment
import app.roaim.dtbazar.ui.food.FoodFragment
import app.roaim.dtbazar.ui.store.StoreFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeStoreFragment(): StoreFragment

    @ContributesAndroidInjector
    abstract fun contributeFoodFragment(): FoodFragment
}
