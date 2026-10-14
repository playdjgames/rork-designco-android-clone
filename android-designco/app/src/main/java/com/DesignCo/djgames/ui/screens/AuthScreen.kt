package com.DesignCo.djgames.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.DesignCo.djgames.data.UserProfile
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.components.dcFieldColors
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily

@Composable
fun AuthScreen(initialMode: String, vm: AppViewModel, nav: AppNavigator) {
    var isCreate by rememberSaveable { mutableStateOf(initialMode == "create") }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var signInError by rememberSaveable { mutableStateOf<String?>(null) }

    val emailValid = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val nameValid = !isCreate || name.trim().length >= 2
    val passwordValid = password.length >= 6
    val formValid = emailValid && nameValid && passwordValid

    Column(
        Modifier
            .fillMaxSize()
            .background(DC.CanvasGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        ScreenTopBar(if (isCreate) "Create Account" else "Sign In", onBack = nav::back)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                if (isCreate) "Join DesignCo" else "Welcome back",
                fontFamily = SerifFamily,
                fontSize = 32.sp,
                color = DC.Ivory
            )
            Text(
                if (isCreate) "Save your details, track orders and manage your appointments."
                else "Sign in to see your membership, bookings and orders.",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Muted,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )
            if (isCreate) {
                AuthField(name, { name = it }, "Full name", Icons.Outlined.Person,
                    error = if (showErrors && !nameValid) "Enter your name" else null)
            }
            AuthField(email, { email = it }, "Email", Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                error = if (showErrors && !emailValid) "Enter a valid email" else null)
            if (isCreate) {
                AuthField(phone, { phone = it }, "Mobile (for reminders)", Icons.Outlined.Phone,
                    keyboardType = KeyboardType.Phone)
            }
            AuthField(password, { password = it; signInError = null }, "Password", Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                imeAction = ImeAction.Done,
                error = when {
                    showErrors && !passwordValid -> "At least 6 characters"
                    else -> signInError
                })

            Spacer(Modifier.height(12.dp))
            GoldButton(
                if (isCreate) "Create Account" else "Sign In",
                onClick = {
                    showErrors = true
                    if (formValid) {
                        if (isCreate) {
                            vm.createAccount(UserProfile(name.trim(), email.trim(), phone.trim()))
                            nav.back()
                        } else if (vm.signIn(email, password)) {
                            nav.back()
                        } else {
                            signInError = "Incorrect email or password"
                        }
                    }
                }
            )
            Text(
                if (isCreate) "Already have an account? Sign in" else "New to DesignCo? Create an account",
                style = MaterialTheme.typography.labelLarge,
                color = DC.Gold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable {
                        isCreate = !isCreate
                        showErrors = false
                    }
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    error: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        shape = RoundedCornerShape(18.dp),
        colors = dcFieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    )
}
