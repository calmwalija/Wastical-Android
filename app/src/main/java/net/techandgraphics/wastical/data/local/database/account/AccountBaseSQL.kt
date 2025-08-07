package net.techandgraphics.wastical.data.local.database.account

import org.intellij.lang.annotations.Language

@Language("RoomSql")
const val ACCOUNT_QUERY_EXPORT = """
   SELECT
      a.firstname,
      a.lastname,
      a.title,
      a.username,
      a.created_at as createdAt,
      pp.fee,
      ds.name as demographicStreet,
      da.name as demographicArea
    FROM
      account a
      JOIN account_payment_plan app ON app.account_id = a.id
      JOIN payment_plan pp ON pp.id = app.payment_plan_id
      JOIN company_location cl ON cl.id = a.company_location_id
      JOIN demographic_street ds ON cl.demographic_street_id = ds.id
      JOIN demographic_area da ON cl.demographic_area_id = da.id
"""
