package net.techandgraphics.wastemanagement

data class EnvConfig(
  val apiDomain: String,
  val apiUrl: String = "",
  val webSocketUrl: String = "",
)
