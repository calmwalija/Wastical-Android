package net.techandgraphics.wastemanagement.notification

enum class NotificationType(val id: String, val description: String) {
  PaymentVerification(id = "10000", description = "Payment Verification"),
  PaymentFailed(id = "20000", description = "Payment Failure"),
}
