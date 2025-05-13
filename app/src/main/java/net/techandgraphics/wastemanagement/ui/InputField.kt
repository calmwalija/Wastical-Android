package net.techandgraphics.wastemanagement.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R


@Composable fun InputField(
  value: String,
  prompt: String,
  onValueChange: (String) -> Unit,
  keyboardType: KeyboardType = KeyboardType.Text,
  imageVector: ImageVector? = null,
  @DrawableRes painterResource: Int = -1,
  hidePassword: Boolean = true,
  trailingView: @Composable () -> Unit ={},
  togglePasswordVisual: () -> Unit = {}
) {

  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(modifier = Modifier.size(32.dp)) {
      if (imageVector == null) Icon(
        painter = painterResource(painterResource),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
      ) else Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    Column {
      BasicTextField(
        modifier = Modifier
          .padding(4.dp)
          .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        decorationBox = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f)
            ) {
              it.invoke()
              if (value.trim().isEmpty())
                Text(
                  text = prompt,
                  color = LocalContentColor.current.copy(alpha = 0.5f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
            }
            if (keyboardType == KeyboardType.Password) {
              var resId =
                if (hidePassword) R.drawable.ic_hide_password else R.drawable.ic_show_password
              IconButton(onClick = { togglePasswordVisual() }) {
                Icon(
                  painter = painterResource(resId),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            trailingView()
          }

        },
        visualTransformation =
          if (keyboardType != KeyboardType.Password)
            VisualTransformation.None else {
            if (!hidePassword) VisualTransformation.None else {
              PasswordVisualTransformation('*')
            }
          },
        keyboardOptions = KeyboardOptions(
          keyboardType = keyboardType,
          autoCorrectEnabled = true,
        ),
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
      )
      Spacer(
        modifier = Modifier
          .fillMaxWidth()
          .padding(end = 8.dp)
          .height(1.dp)
          .background(Color.Gray)
      )
    }
  }
}
