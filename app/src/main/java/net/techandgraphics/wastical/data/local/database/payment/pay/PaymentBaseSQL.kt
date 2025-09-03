package net.techandgraphics.wastical.data.local.database.payment.pay

import org.intellij.lang.annotations.Language

@Language("RoomSql")
const val PAYMENT_QUERY_BASE = """
    SELECT
      payment.id AS paymentId,
      payment.screenshot_text AS screenshotText,
      payment.transaction_id AS transactionId,
      payment.payment_reference AS paymentReference,
      payment.payment_method_id AS paymentMethodId,
      payment.account_id AS accountId,
      payment.payment_status AS paymentStatus,
      payment.created_at AS paymentCreatedAt,
      payment.updated_at AS paymentUpdatedAt,
      payment.company_id AS paymentCompanyId,
      payment.executed_by_id AS executedById,
      payment.proof_ext AS proofExt,

      account.id AS accId,
      account.uuid AS uuid,
      account.role AS role,
      account.title AS title,
      account.firstname AS firstname,
      account.lastname AS lastname,
      account.username AS username,
      account.email AS email,
      account.latitude AS latitude,
      account.longitude AS longitude,
      account.status AS accStatus,
      account.company_location_id AS companyLocationId,
      account.company_id AS accCompanyId,
      account.leaving_reason AS leavingReason,
      account.leaving_timestamp AS leavingTimestamp,
      account.updated_at AS accUpdatedAt,
      account.created_at AS accCreatedAt,

      method.id AS methodId,
      method.account AS methodAccount,
      method.isSelected AS isSelected,
      method.payment_plan_id AS paymentPlanId,
      method.payment_gateway_id AS paymentGatewayId,
      method.created_at AS methodCreatedAt,
      method.updated_at AS methodUpdatedAt,

      gateway.id AS gatewayId,
      gateway.name AS gatewayName,
      gateway.type AS gatewayType,
      gateway.created_at AS gatewayCreatedAt,
      gateway.updated_at AS gatewayUpdatedAt,

      plans.id AS planId,
      plans.fee AS planFee,
      plans.name AS planName,
      plans.period AS planPeriod,
      plans.status AS planStatus,
      plans.company_id AS planCompanyId,
      plans.created_at AS planCreatedAt,
      plans.created_at AS planUpdatedAt,

      (SELECT COUNT(*) FROM payment_month_covered covered WHERE covered.payment_id = payment.id ) as coveredSize

    FROM payment
    INNER JOIN account ON account.id = payment.account_id
    INNER JOIN payment_method method ON method.id = payment.payment_method_id
    INNER JOIN payment_gateway gateway ON gateway.id = method.payment_gateway_id
    INNER JOIN payment_plan plans ON plans.id = method.payment_plan_id
"""
