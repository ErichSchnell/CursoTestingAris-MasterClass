package com.erichschnell.testingapp.productList.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.productList.presentation.models.ProductListEvent
import com.erichschnell.testingapp.productList.presentation.models.ProductListUiState

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect {event ->
            when(event){
                is ProductListEvent.showMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold( snackbarHost = { SnackbarHost(snackbarHostState) } ) { padding ->

        when(val state = uiState) {
            ProductListUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(padding))
            }
            is ProductListUiState.Error -> {
                ErrorContent(modifier = Modifier.padding(padding), error = state.message)
            }
            is ProductListUiState.Success -> {
                SuccessContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    state = state
                )
            }
        }

    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier, error:String) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(error, fontSize = 32.sp, color = Color.Red)
    }
}

@Composable
private fun SuccessContent(
    modifier: Modifier = Modifier,
    state:ProductListUiState.Success,
    onEvent: (ProductListEvent) -> Unit = {}
) {
    Column (modifier){
        LazyColumn {
            items(state.products) {product ->
                Box(Modifier.fillMaxWidth().height(50.dp).background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(product.name)
                }
            }
        }
    }
}

