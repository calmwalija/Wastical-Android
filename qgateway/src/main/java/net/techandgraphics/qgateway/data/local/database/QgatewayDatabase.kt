package net.techandgraphics.qgateway.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.techandgraphics.qgateway.data.local.database.account.AccountDao
import net.techandgraphics.qgateway.data.local.database.account.AccountEntity
import net.techandgraphics.qgateway.data.local.database.otp.OtpDao
import net.techandgraphics.qgateway.data.local.database.otp.OtpEntity
import net.techandgraphics.qgateway.data.local.database.token.FcmTokenDao
import net.techandgraphics.qgateway.data.local.database.token.FcmTokenEntity

@Database(
  entities = [
    FcmTokenEntity::class,
    OtpEntity::class,
    AccountEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
@TypeConverters()
abstract class QgatewayDatabase : RoomDatabase() {

  abstract val fcmTokenDao: FcmTokenDao
  abstract val optDao: OtpDao
  abstract val accountDao: AccountDao

  companion object {
    const val NAME = "qgateway_db"
  }
}
