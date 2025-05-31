package net.techandgraphics.wastemanagement.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.techandgraphics.wastemanagement.data.local.database.account.AccountDao
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactDao
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.account.plan.AccountPaymentPlanDao
import net.techandgraphics.wastemanagement.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenDao
import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyDao
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactDao
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule.TrashCollectionScheduleDao
import net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule.TrashCollectionScheduleEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.AreaDao
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.AreaEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DistrictDao
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.StreetDao
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.StreetEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.collection.PaymentCollectionDayDao
import net.techandgraphics.wastemanagement.data.local.database.payment.collection.PaymentCollectionDayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayDao
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayEntity
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
    CompanyContactEntity::class,
    PaymentEntity::class,
    PaymentPlanEntity::class,
    PaymentMethodEntity::class,
    DistrictEntity::class,
    StreetEntity::class,
    AreaEntity::class,
    TrashCollectionScheduleEntity::class,
    PaymentCollectionDayEntity::class,
    AccountPaymentPlanEntity::class,
    PaymentGatewayEntity::class,
    AccountFcmTokenEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {

  abstract val accountDao: AccountDao
  abstract val accountContactDao: AccountContactDao
  abstract val companyDao: CompanyDao
  abstract val paymentDao: PaymentDao
  abstract val paymentPlanDao: PaymentPlanDao
  abstract val paymentMethodDao: PaymentMethodDao
  abstract val companyContactDao: CompanyContactDao
  abstract val districtDao: DistrictDao
  abstract val streetDao: StreetDao
  abstract val areaDao: AreaDao
  abstract val trashScheduleDao: TrashCollectionScheduleDao
  abstract val paymentDayDao: PaymentCollectionDayDao
  abstract val accountPaymentPlanDao: AccountPaymentPlanDao
  abstract val paymentGatewayDao: PaymentGatewayDao
  abstract val accountFcmTokenDao: AccountFcmTokenDao

  companion object {
    const val NAME = "waste_management_db"
  }
}
