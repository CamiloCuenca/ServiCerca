package com.servicerca.app.ui.profile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.servicerca.app.R

@Composable
fun InsigniasScreen(){
    Text(
        text = stringResource(R.string.statistic),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InsigniasScreenPreview() {
    InsigniasScreen()
}