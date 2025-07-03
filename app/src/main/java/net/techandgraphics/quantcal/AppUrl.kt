package net.techandgraphics.quantcal

object AppUrl {
  val devApi = "http://${BuildConfig.DEV_API_DOMAIN}:8080/"
  val prodApi = "https://${BuildConfig.PROD_API_DOMAIN}/"
  val FILE_URL = "${devApi}file/"
}

fun appUrl(): EnvConfig {
  return EnvConfig(
    apiDomain = if (BuildConfig.DEBUG) {
      AppUrl.devApi
    } else {
      AppUrl.prodApi
    },
  )
}
