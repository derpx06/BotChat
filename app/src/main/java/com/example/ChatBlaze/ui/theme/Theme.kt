import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ChatBlaze.ui.theme.*
import com.example.ChatBlaze.ui.theme.CloudWhite
import com.example.ChatBlaze.ui.theme.PureWhite
import com.example.ChatBlaze.ui.theme.Purple40
import com.example.ChatBlaze.ui.theme.RedAccent

val SleekDarkColorScheme = darkColorScheme(
    primary = DeepSlate,
    onPrimary = SilverMist,
    surface = CarbonGray,
    onSurface = PlatinumGray,
    background = DeepSlate,
    onBackground = PlatinumGray,
    secondary = AccentTeal,
    onSecondary = CarbonGray
)

val SleekLightColorScheme = lightColorScheme(
    primary = WhiteSmoke,
    onPrimary = DeepSlate,
    surface = LightGray,
    onSurface = Charcoal,
    background = WhiteSmoke,
    onBackground = Charcoal,
    secondary = AccentSkyBlue,
    onSecondary = WhiteSmoke
)

val VibrantAbstractDarkColorScheme = darkColorScheme(
    primary = CosmicIndigo,
    onPrimary = NeonMagenta,
    surface = GalacticBlack,
    onSurface = SolarFlare,
    background = CosmicIndigo,
    onBackground = StarDust,
    secondary = ElectricViolet,
    onSecondary = GalacticBlack
)

val VibrantAbstractLightColorScheme = lightColorScheme(
    primary = VividSky,
    onPrimary = CosmicIndigo,
    surface = CloudPink,
    onSurface = MellowYellow,
    background = CloudPink,
    onBackground = CosmicIndigo,
    secondary = MintBurst,
    onSecondary = VividSky
)

// --- EXISTING THEMES ARE HERE ---

val DarkColorScheme = darkColorScheme(
    primary = AccentAqua,
    onPrimary = Color.Black,
    surface = RichNavy,
    onSurface = PaleBlue,
    background = DeepMidnightBlue,
    onBackground = PaleBlue,
    error = ErrorRedVibrant,
    onError = PureWhite,
    secondary = SoftBlueGray,
    onSecondary = DeepMidnightBlue
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = PureWhite,
    surface = CloudWhite,
    onSurface = Color.Black,
    error = RedAccent,
    onError = PureWhite
)

@Composable
fun BotChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = AccentAqua,
            onPrimary = Color.Black,
            surface = RichNavy,
            onSurface = PaleBlue,
            background = DeepMidnightBlue,
            onBackground = PaleBlue,
            error = ErrorRedVibrant,
            onError = PureWhite,
            secondary = SoftBlueGray,
            onSecondary = DeepMidnightBlue
        )
    } else {
        lightColorScheme(
            primary = Purple40,
            onPrimary = PureWhite,
            surface = CloudWhite,
            onSurface = Color.Black,
            error = RedAccent,
            onError = PureWhite
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}