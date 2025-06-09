package net.techandgraphics.wastemanagement.data.remote.payment

enum class PaymentStatus() {
  Waiting,
  Failed,
  Verifying,
  Approved,
  Declined,
}
