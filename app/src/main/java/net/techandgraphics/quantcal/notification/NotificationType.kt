package net.techandgraphics.quantcal.notification

enum class NotificationType(val id: String, val description: String) {
  PaymentVerification(id = "10000", description = "Payment Verification"),
  PaymentFailed(id = "20000", description = "Payment Failure"),
  PaymentRecorded(id = "30000", description = "Payment Recorded"),
}
