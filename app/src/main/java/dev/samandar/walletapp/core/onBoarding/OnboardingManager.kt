package dev.samandar.walletapp.core.onBoarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Context orqali DataStore yaratish (extension property)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

class OnboardingManager(private val context: Context) {

    companion object {
        // Kalit so'zni aniqlaymiz
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    /**
     * Onboarding holatini kuzatish (Flow qaytaradi)
     * true - birinchi marta kirgan
     * false - onboarding tugatilgan
     */
    val isOnboardingRequired: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            // Ma'lumot o'qishda xato bo'lsa (masalan fayl buzilgan bo'lsa)
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Agar qiymat topilmasa, demak hali ko'rmagan (true qaytaramiz)
            val isCompleted = preferences[ONBOARDING_COMPLETED] ?: false
            !isCompleted
        }

    /**
     * Onboarding tugagandan so'ng holatni saqlash
     */
    suspend fun saveOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }
}