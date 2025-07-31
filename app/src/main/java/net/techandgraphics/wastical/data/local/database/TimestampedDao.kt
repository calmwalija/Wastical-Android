package net.techandgraphics.wastical.data.local.database

interface TimestampedDao {
  suspend fun getLastUpdatedTimestamp(): Long
}
