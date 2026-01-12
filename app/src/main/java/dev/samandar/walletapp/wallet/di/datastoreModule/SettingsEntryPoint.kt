package dev.samandar.walletapp.wallet.di.datastoreModule

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.samandar.walletapp.wallet.data.repository.SettingsRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsEntryPoint {
    // Repository'ni bu yerda exposure qilamiz
    fun settingsRepository(): SettingsRepository
}