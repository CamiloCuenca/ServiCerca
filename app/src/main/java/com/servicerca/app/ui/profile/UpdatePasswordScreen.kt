package com.servicerca.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.input.AppPasswordField

@Composable
fun UpdatePasswordScreen(
    onBack: () -> Unit
    ){

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // Header: ícono a la izquierda, título centrado
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { onBack() },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }

                        Text(
                            text = stringResource(R.string.change_password),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)
                }
                Box(
                    Modifier.fillMaxWidth()
                        .padding( vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.message_change_password),
                        style =MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    AppPasswordField(
                        password = currentPassword,
                        onPasswordChange = { currentPassword = it },
                        label = stringResource(R.string.current_password),
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                    AppPasswordField(
                        password = newPassword,
                        onPasswordChange = { newPassword = it },
                        label = stringResource(R.string.new_password),
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                    AppPasswordField(
                        password = confirmNewPassword,
                        onPasswordChange = { confirmNewPassword = it },
                        label = stringResource(R.string.confirm_new_password),
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                }
                Box(
                    Modifier.fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    PrimaryButton(
                        text = stringResource(R.string.btn_update_password),
                        onClick = {}
                    )
                }
            }
        }
    }
@Preview
@Composable
fun UpdatePasswordScreenPreview(){
    UpdatePasswordScreen(
        onBack = {}
    )
}