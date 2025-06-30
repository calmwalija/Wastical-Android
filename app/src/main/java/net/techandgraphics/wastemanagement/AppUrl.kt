package net.techandgraphics.wastemanagement

object AppUrl {
  private val API_DOMAIN = appUrl().apiDomain
  val API_URL = "https://$API_DOMAIN:8080/"
  val FILE_URL = "${API_URL}file/"
}

private fun appUrl(): EnvConfig {
  return EnvConfig(apiDomain = BuildConfig.API_DOMAIN)
}
