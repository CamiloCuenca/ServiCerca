package com.servicerca.app.ui.services.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.input.AppTextField

@Composable
fun CreateServiceScreen(
){

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("")}

    Column{
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.create_service_add_image_label),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(5.dp))

        Image(
            painter = painterResource(id = R.drawable.create_service_add_image),
            contentDescription = "Create Service Add Image",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            value = title,
            onValueChange = {title = it},
            label = stringResource(R.string.title_service_label)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box{
            AppTextField(
                value = description,
                onValueChange = {description = it},
                label = stringResource(R.string.detailed_description_label)

            )

        }

    }
}

@Preview (showBackground = true , showSystemUi = true)
@Composable
fun CreateServicePreview() {
    CreateServiceScreen()
}