package br.com.noartcode.theprice.data.local.datasource.settings

import br.com.noartcode.theprice.domain.model.AppSettings
import kotlin.concurrent.Volatile

// Settings resolved for the current process (filled on Splash). Changes apply after restart.
class AppSettingsCache {
    @Volatile
    var settings: AppSettings? = null
}
