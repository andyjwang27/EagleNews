package com.eaglenews.app.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.Article
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.theme.DarkRed
import com.eaglenews.app.viewmodel.ContentViewModel

@Composable
fun HomeView(
    appState: AppState,
    userId: String,
    contentViewModel: ContentViewModel,
    viewModel: HomeViewViewModel = viewModel()
) {
    val articles by viewModel.articles.collectAsState()
    var selectedArticle by remember { mutableStateOf<Article?>(null) }
    var articleToDelete by remember {
        mutableStateOf<Article?>(null)
    }

    LaunchedEffect(userId) {
        viewModel.startListening(userId)
    }

    DefaultView(
        title = "Home",
        showMenu = appState.showMenu,
        showMenuButton = true,
        onMenuClick = { appState.showMenu = true }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 20.dp),
        ) {

            itemsIndexed(
                items = articles,
                key = { _, article -> article.id ?: "" }
            ) { index, article ->

                ArticleRow(
                    article = article,
                    isAdmin = contentViewModel.isAdmin,
                    isFirst = index == 0,
                    isLast = index == articles.lastIndex,
                    onClick = {
                        selectedArticle = article
                    },
                    onDelete = {
                        articleToDelete = article
                    },
                    onTogglePin = {
                        viewModel.togglePin(article)
                    }
                )
            }
        }
    }

    selectedArticle?.let { article ->
        ArticleSheet(
            article = article,
            onDismiss = { selectedArticle = null }
        )
    }

    if (articleToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                articleToDelete = null
            },
            title = {
                Text("Delete Article?")
            },
            text = {
                Text(
                    "This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = DarkRed
                        ),
                    onClick = {
                        articleToDelete?.id?.let { id ->
                            viewModel.deleteArticle(id)
                        }
                        articleToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        articleToDelete = null
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White
                    )
                }
            }

        )
    }
}