package net.techandgraphics.quantcal.data.local.database.account

enum class AccountTitle(val title: String) {
  MR("Mr."),
  MRS("Mrs."),
  MISS("Miss."),
  MS("Ms."),
  DR("Dr."),
  PROF("Prof."),
  REV("Rev."),
  SIR("Sir."),
  Pastor("Pastor."),
  HON("Hon."),
  Na("Na"),
  ;

  override fun toString(): String {
    return title
  }
}
