package net.techandgraphics.wastical.notification

enum class NotificationType(val id: String, val description: String) {

  PROOF_OF_PAYMENT_SUBMITTED(
    id = "174b8ee0-64a8-4c15-86f4-c93cab824c2f",
    description = "Proof Of Payment Submitted",
  ),

  PROOF_OF_PAYMENT_RECEIVED(
    id = "1f0ea987-171d-493d-8fe0-873772d6bf66",
    description = "Received Proof Of Payment",
  ),

  PROOF_OF_PAYMENT_APPROVED(
    id = "ef02538d-d8fe-4692-a67a-54eeec16e1bb",
    description = "Proof Of Payment Approved",
  ),

  PROOF_OF_PAYMENT_DECLINED(
    id = "a4c30371-431f-4c73-9e60-ab4d569797d0",
    description = "Proof Of Payment Declined",
  ),

  PROOF_OF_PAYMENT_SUBMITTED_BY_COMPANY(
    id = "4e13b8c5-4076-4313-87d4-3dda81edbfe4",
    description = "Proof Of Payment Submitted By Company",
  ),

  PROOF_OF_PAYMENT_SUCCESSFUL_BY_COMPANY(
    id = "2d3cd90e-ca20-4724-a50b-24f2a3596aed",
    description = "Proof Of Payment Was Successful By Company",
  ),

  PROOF_OF_PAYMENT_COMPANY_VERIFY(
    id = "3366f023-eede-452c-8743-3d8e8373eefa",
    description = "Proof Of Payment Company Verification",
  ),

  ACCOUNT_BASED_NOTIFICATION(
    id = "638c651b-6339-4da1-9aae-be12096595c6",
    description = "New message received",
  ),

  COMPANY_BROADCAST_NOTIFICATION(
    id = "d5d7d9f3-5b32-4098-abad-e95a511621a0",
    description = "Company Broadcast Notification",
  ),

  LOCATION_BASED_NOTIFICATION(
    id = "3a0da5dd-2c15-4971-811d-45153fadf3c8",
    description = "Location-based notification",
  ),

  WASTE_COLLECTION_REMINDER(
    id = "7541db40-3e46-4bd4-8bc3-5b9362c9cdc8",
    description = "Upcoming waste collection reminder",
  ),

  COMPANY_NOTIFICATION_SUCCESSFUL(
    id = "3a0da5dd-2c15-4971-811d-45153fadf3c8",
    description = "Notification Was Successful",
  ),

  PAYMENT_DUE_REMINDER(
    id = "663d67ac-f4ed-496d-9bd9-5abcf51d3d72",
    description = "Payment is due",
  ),
}
