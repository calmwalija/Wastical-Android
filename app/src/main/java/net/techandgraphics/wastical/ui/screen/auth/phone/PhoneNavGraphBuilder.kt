@file:Suppress("FunctionName")
@file:JvmName("PhoneRouteKt")

package net.techandgraphics.wastical.ui.screen.auth.phone

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import net.techandgraphics.wastical.ui.screen.auth.phone.otp.OtpNav
import net.techandgraphics.wastical.ui.screen.auth.phone.verify.VerifyPhoneNav

fun NavGraphBuilder.PhoneNavGraphBuilder(navController: NavHostController) {
  OtpNav(navController)
  VerifyPhoneNav(navController)
}
