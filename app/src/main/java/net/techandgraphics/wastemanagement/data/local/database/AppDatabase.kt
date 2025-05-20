package net.techandgraphics.wastemanagement.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.techandgraphics.wastemanagement.data.local.database.account.AccountDao
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyDao
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactDao
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodDao
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentDao
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanDao
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity

@Database(
  entities = [
    AccountEntity::class,
    AccountContactEntity::class,
    CompanyEntity::class,
    PaymentEntity::class,
    PaymentPlanEntity::class,
    PaymentMethodEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {

  abstract val accountDao: AccountDao
  abstract val companyDao: CompanyDao
  abstract val paymentDao: PaymentDao
  abstract val paymentPlanDao: PaymentPlanDao
  abstract val paymentMethodDao: PaymentMethodDao
  abstract val companyContactDao: CompanyContactDao

  companion object {
    const val NAME = "waste_management_db"
  }
}
