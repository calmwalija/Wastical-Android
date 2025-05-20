package net.techandgraphics.wastemanagement.data.remote.payment

enum class PaymentStatus(val description: String) {

  Pending("Payment has been initiated but not yet completed."),
  Processing("Payment is currently being processed."),
  Approved("Payment completed successfully."),
  Failed("Payment failed due to an error."),
  Cancelled("Payment was cancelled by the user or system."),
  ;

  override fun toString(): String = name.lowercase()
}
