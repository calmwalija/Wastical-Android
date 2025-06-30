package net.techandgraphics.wastemanagement

object AppUrl {
  const val DEV_API_URL = "http://${BuildConfig.DEV_API_DOMAIN}:8080/"
  const val PROD_API_URL = "https://${BuildConfig.PROD_API_DOMAIN}/"
  const val FILE_URL = "${DEV_API_URL}file/"
}

fun appUrl(): EnvConfig {
  return EnvConfig(
    apiDomain = if (BuildConfig.DEBUG) {
      AppUrl.DEV_API_URL
    } else {
      AppUrl.PROD_API_URL
    },
  )
}
