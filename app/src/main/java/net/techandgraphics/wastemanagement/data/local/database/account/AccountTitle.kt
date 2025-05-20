package net.techandgraphics.wastemanagement.data.local.database.account

enum class AccountTitle(val title: String) {
  MR("Mr."),
  MRS("Mrs."),
  MISS("Miss"),
  MS("Ms."),
  DR("Dr."),
  PROF("Prof."),
  REV("Rev."),
  SIR("Sir"),
  DAME("Dame"),
  LORD("Lord"),
  LADY("Lady"),
  Pastor("Pastor."),
  HON("Hon."),
  ;

  override fun toString(): String {
    return title
  }
}
