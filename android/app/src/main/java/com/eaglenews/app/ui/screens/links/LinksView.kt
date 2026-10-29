package com.eaglenews.app.ui.screens.links

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.Link
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.theme.DarkRed
import com.eaglenews.app.viewmodel.ContentViewModel

@Composable
fun LinksView(
    appState: AppState,
    userId: String,
    contentViewModel: ContentViewModel,
    viewModel: LinksViewViewModel = viewModel(),
) {
    val links by viewModel.links.collectAsState()
    var linkToDelete by remember {
        mutableStateOf<Link?>(null)
    }
    val context = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.startListeningForLinks(userId)
    }

    DefaultView(
        title = "Links",
        showMenu = appState.showMenu,
        showMenuButton = true,
        onMenuClick = {
            appState.showMenu = true
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = 20.dp,
                    top = 20.dp,
                    start = 20.dp,
                    end = 20.dp
                )
        ) {
            items(
                items = links,
                key = { it.id ?: it.name }
            ) { link ->
                LinkRow(
                    link = link,
                    isAdmin = contentViewModel.isAdmin,
                    isFirst = links.first() == link,
                    isLast = links.last() == link,
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(link.link)
                        )
                        context.startActivity(intent)
                    },
                    onDelete = {
                        linkToDelete = link
                    }
                )
            }
        }
    }

    if(linkToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                linkToDelete = null
            },
            title = {
                Text("Delete Link?")
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
                        viewModel.deleteLink(
                            linkToDelete!!
                        )
                        linkToDelete = null
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
                        linkToDelete = null
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