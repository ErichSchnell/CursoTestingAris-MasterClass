package com.erichschnell.testingapp.presentation.cart.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.presentation.cart.model.CartEvent
import com.erichschnell.testingapp.presentation.cart.model.CartUiState
import com.erichschnell.testingapp.presentation.cart.screen.content.SuccesssContent
import com.erichschnell.testingapp.presentation.core.components.MarketTopAppBar

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when(event){
                is CartEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = { MarketTopAppBar(title = "Carrito", onBackSelected = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState)}
    ) { paddings ->
        when(val state = uiState){
            is CartUiState.Error -> ErrorContent(paddings, state.message)
            CartUiState.Loading -> LoadingContent(paddings)
            is CartUiState.Success -> SuccesssContent(paddings, state, viewModel::onAction)
        }


    }
}

@Composable
private fun ErrorContent(
    paddings: PaddingValues,
    message: String,
    onRetry: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddings)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onRetry() }) {
            Text(
                text = "Reintentar"
            )
        }
    }
}

@Composable
private fun LoadingContent(paddings: PaddingValues) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(paddings)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}