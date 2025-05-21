package net.techandgraphics.wastemanagement.data.remote.payment

enum class PaymentStatus() {
  Retry,
  Pending,
  Processing,
  Approved,
  Failed,
  Cancelled,
}
