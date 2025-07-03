package net.techandgraphics.quantcal.data.remote.payment

enum class PaymentStatus() {
  Waiting,
  Failed,
  Verifying,
  Approved,
  Declined,
}
