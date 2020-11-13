package it.danielmilano.beers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import it.danielmilano.beers.api.PunkService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providePunkService(): PunkService {
        return PunkService.create()
    }
}
