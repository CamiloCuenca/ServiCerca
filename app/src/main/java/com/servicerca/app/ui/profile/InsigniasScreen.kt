package com.servicerca.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R

@Composable
fun InsigniasScreen(onBack: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Icono AutoMirrored
                        contentDescription = null,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(95.dp))
                    Text(
                        text = stringResource(R.string.tittle_insignias),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_verified),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_rapido), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_fast),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_top5), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_top5),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_verified),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_verified),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_verified),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_verified),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_verified),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.insignia_verified),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InsigniasScreenPreview() {
    InsigniasScreen(onBack = {})
}