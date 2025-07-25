package net.techandgraphics.wastical.data.remote.payment

enum class PaymentStatus() {
  Waiting,
  Failed,
  Verifying,
  Approved,
  Declined,
}
