package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.samandar.walletapp.core.MainActivity
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.SettingsViewModel
import kotlinx.coroutines.launch

data class AppLanguage(
    val code: String,
    val displayName: String
)

val availableLanguages = listOf(
    AppLanguage("en", "English"),
    //AppLanguage("ru", "Русский"),
    AppLanguage("uz", "O'zbekcha")
)


@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentLanguageCode by viewModel.currentLanguageCode.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedLanguageCode by remember { mutableStateOf(currentLanguageCode) }

    val isLanguageChanged = selectedLanguageCode != currentLanguageCode

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Strings.setting_choose_language_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableLanguages.forEach { lang ->
                        val isChecked = selectedLanguageCode == lang.code

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedLanguageCode = lang.code }
                                .background(if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lang.displayName,
                                fontSize = 16.sp,
                                fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                            )
                            Box(
                                modifier = Modifier.size(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Tanlangan til",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    enabled = isLanguageChanged,
                    onClick = {
                        scope.launch {
                            // 1. Fonda saqlash
                            viewModel.setLanguage(selectedLanguageCode)

                            // 2. Tizimga tilni o'zgartirish haqida buyruq berish
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(selectedLanguageCode)
                            AppCompatDelegate.setApplicationLocales(appLocale)

                            // 3. TITLE TEZ O'ZGARISHI UCHUN MAJBURIY RECREATE
                            // setApplicationLocales ba'zan sekin ishlashi mumkin,
                            // shuning uchun biz activityni qo'lda yangilaymiz
                            (context as? MainActivity)?.let { activity ->
                                val intent = activity.intent
                                activity.finish() // Activityni yopish
                                activity.startActivity(intent) // Qaytadan ochish (animatsiyasiz tez ochiladi)
                                activity.overridePendingTransition(0, 0) // Miltillashni yo'qotish
                            }
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
                        disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f)
                    )
                ) {
                    Text(
                        text = stringResource(Strings.btn_save),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}