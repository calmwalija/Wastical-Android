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
import net.techandgraphics.wastemanagement.data.local.database.account.plan.request.AccountPaymentPlanRequestDao
import net.techandgraphics.wastemanagement.data.local.database.account.plan.request.AccountPaymentPlanRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.account.request.AccountRequestDao
import net.techandgraphics.wastemanagement.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenDao
import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyDao
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.bin.collection.CompanyBinCollectionDao
import net.techandgraphics.wastemanagement.data.local.database.company.bin.collection.CompanyBinCollectionEntity
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactDao
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.location.CompanyLocationDao
import net.techandgraphics.wastemanagement.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.AccountIndicatorDao
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.PaymentIndicatorDao
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.StreetIndicatorDao
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.DemographicAreaDao
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DemographicDistrictDao
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DemographicDistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.DemographicStreetDao
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.DemographicStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.collection.PaymentCollectionDayDao
import net.techandgraphics.wastemanagement.data.local.database.payment.collection.PaymentCollectionDayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayDao
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodDao
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentDao
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.month.covered.PaymentMonthCoveredDao
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.month.covered.PaymentMonthCoveredEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.request.PaymentRequestDao
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanDao
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.search.tag.SearchTagDao
import net.techandgraphics.wastemanagement.data.local.database.search.tag.SearchTagEntity

@Database(
  entities = [
    AccountEntity::class,
    AccountContactEntity::class,
    CompanyEntity::class,
    CompanyContactEntity::class,
    PaymentEntity::class,
    PaymentPlanEntity::class,
    PaymentMethodEntity::class,
    DemographicDistrictEntity::class,
    DemographicStreetEntity::class,
    DemographicAreaEntity::class,
    CompanyBinCollectionEntity::class,
    PaymentCollectionDayEntity::class,
    AccountPaymentPlanEntity::class,
    PaymentGatewayEntity::class,
    AccountFcmTokenEntity::class,
    PaymentMonthCoveredEntity::class,
    CompanyLocationEntity::class,
    SearchTagEntity::class,

    PaymentRequestEntity::class,
    AccountRequestEntity::class,
    AccountPaymentPlanRequestEntity::class,
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
  abstract val demographicDistrictDao: DemographicDistrictDao
  abstract val demographicStreetDao: DemographicStreetDao
  abstract val demographicAreaDao: DemographicAreaDao
  abstract val companyBinCollectionDao: CompanyBinCollectionDao
  abstract val paymentDayDao: PaymentCollectionDayDao
  abstract val accountPaymentPlanDao: AccountPaymentPlanDao
  abstract val paymentGatewayDao: PaymentGatewayDao
  abstract val accountFcmTokenDao: AccountFcmTokenDao
  abstract val companyLocationDao: CompanyLocationDao

  abstract val streetIndicatorDao: StreetIndicatorDao
  abstract val accountIndicatorDao: AccountIndicatorDao
  abstract val paymentIndicatorDao: PaymentIndicatorDao
  abstract val paymentMonthCoveredDao: PaymentMonthCoveredDao
  abstract val searchTagDao: SearchTagDao

  abstract val paymentRequestDao: PaymentRequestDao
  abstract val accountRequestDao: AccountRequestDao
  abstract val accountPaymentPlanRequestDao: AccountPaymentPlanRequestDao

  companion object {
    const val NAME = "waste_management_db"
  }
}
