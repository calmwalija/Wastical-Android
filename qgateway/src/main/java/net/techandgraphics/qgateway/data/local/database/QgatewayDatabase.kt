package net.techandgraphics.qgateway.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.techandgraphics.qgateway.data.local.database.otp.OtpDao
import net.techandgraphics.qgateway.data.local.database.otp.OtpEntity
import net.techandgraphics.qgateway.data.local.database.sms.SmsDao
import net.techandgraphics.qgateway.data.local.database.sms.SmsEntity
import net.techandgraphics.qgateway.data.local.database.token.FcmTokenDao
import net.techandgraphics.qgateway.data.local.database.token.FcmTokenEntity

@Database(
  entities = [
    SmsEntity::class,
    FcmTokenEntity::class,
    OtpEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
@TypeConverters()
abstract class QgatewayDatabase : RoomDatabase() {

  abstract val smsDao: SmsDao
  abstract val fcmTokenDao: FcmTokenDao
  abstract val optDao: OtpDao

  companion object {
    const val NAME = "qgateway_db"
  }
}
