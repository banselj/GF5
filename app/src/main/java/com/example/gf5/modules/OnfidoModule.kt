package com.example.gf5.modules


import android.content.Context
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.OnfidoFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide Onfido instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object OnfidoModule {

    /**
     * Provides a singleton instance of Onfido.
     *
     * @param context The application context.
     */
    @Provides
    @Singleton
    fun provideOnfido(@ApplicationContext context: Context): Onfido =
        OnfidoFactory.create(context).client
}
