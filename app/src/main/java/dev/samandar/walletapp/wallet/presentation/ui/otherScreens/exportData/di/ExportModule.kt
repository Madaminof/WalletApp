package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.pdf.PdfExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.factory.ExporterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExportModule {

    @Provides
    @Singleton
    fun provideExporterFactory(
        @ApplicationContext context: Context
    ): ExporterFactory {
        return ExporterFactory(context = context)
    }



    @Provides
    @Singleton
    fun providePdfExporter(
        @ApplicationContext context: Context
    ): PdfExporter {
        return PdfExporter(context)
    }

}