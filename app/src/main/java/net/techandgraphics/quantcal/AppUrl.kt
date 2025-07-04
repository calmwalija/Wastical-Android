package net.techandgraphics.quantcal

object AppUrl {
  val FILE_URL = "${appUrl().apiDomain}file/"
}

fun appUrl() =
  EnvConfig(if (BuildConfig.DEBUG) BuildConfig.DEV_API_DOMAIN else BuildConfig.PROD_API_DOMAIN)
