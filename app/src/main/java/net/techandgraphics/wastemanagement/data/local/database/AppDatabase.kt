package net.techandgraphics.wastemanagement.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.techandgraphics.wastemanagement.data.local.database.account.AccountDao
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyDao
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.convertor.StatusConvertor
import net.techandgraphics.wastemanagement.data.local.database.convertor.TitleConvertor

@Database(
  entities = [
    AccountEntity::class,
    CompanyEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
@TypeConverters(
  StatusConvertor::class,
  TitleConvertor::class,
)
abstract class AppDatabase : RoomDatabase() {

  abstract val accountDao: AccountDao
  abstract val companyDao: CompanyDao

  companion object {
    const val NAME = "waste_management_db"
  }
}
