package com.eaglenews.app.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.ui.screens.selection.SelectionChipFlow
import com.eaglenews.app.ui.screens.selection.SelectionFilterChip
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DefaultGray


@Composable
fun AddArticleView(
    viewModel: AddArticleViewViewModel = viewModel(),
    selectedRoles: Set<RoleItem>,
    onSelectedRolesChange: (Set<RoleItem>) -> Unit,
    onShowRolePicker: () -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }
    var summary by remember {
        mutableStateOf("")
    }
    var selectedImages by remember {
        mutableStateOf(listOf<Uri>())
    }
    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            selectedImages = uris.take(10)

        }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 40.dp
                )
        ) {
            Spacer(
                Modifier.height(75.dp)
            )

            Text(
                text = "Create an Article",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text("Title")
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,

                    focusedIndicatorColor = DefaultGray,
                    unfocusedIndicatorColor = DefaultGray,

                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,

                    cursorColor = Color.Black,
                    errorCursorColor = Color.Black,

                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(
                Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = summary,
                onValueChange = {
                    summary = it
                },
                label = {
                    Text("Summary")
                },
                modifier = Modifier
                    .fillMaxWidth(),
                minLines = 3,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,

                    focusedIndicatorColor = DefaultGray,
                    unfocusedIndicatorColor = DefaultGray,

                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,

                    cursorColor = Color.Black,
                    errorCursorColor = Color.Black,

                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(
                Modifier.height(20.dp)
            )

            Text(
                text = "Select Roles:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Button(
                onClick = onShowRolePicker,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkNavy
                ),
                modifier = Modifier
                    .width(160.dp)
                    .height(45.dp)

            ) {
                Text(
                    text = "Select Roles",
                    color = Color.White
                )
            }

            if (selectedRoles.isNotEmpty()) {
                Spacer(
                    Modifier.height(12.dp)
                )
                Text(
                    text = "Selected Roles:"
                )
                Spacer(
                    Modifier.height(8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(25.dp)
                        )
                        .background(Color.White)
                        .padding(12.dp)

                ) {
                    SelectionChipFlow {
                        selectedRoles.forEach { role ->
                            SelectionFilterChip(
                                text = role.name.formattedRole(),
                                onRemove = {
                                    onSelectedRolesChange(selectedRoles - role)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(20.dp)
            )

            Button(
                onClick = {
                    imagePicker.launch(
                        "image/*"
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkNavy
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(
                    Modifier.width(8.dp)
                )
                Text(
                    "Attach Photos",
                    color = Color.White
                )
            }
            if (selectedImages.isNotEmpty()) {
                Spacer(
                    Modifier.height(15.dp)
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(
                            rememberScrollState()
                        )
                ) {
                    selectedImages.forEach { image ->
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                        ) {
                            AsyncImage(
                                model = image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(
                                        width = 150.dp,
                                        height = 250.dp
                                    )
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
                            )

                            IconButton(
                                onClick = {
                                    selectedImages =
                                        selectedImages - image
                                },
                                modifier = Modifier
                                    .align(
                                        Alignment.TopEnd
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(30.dp)
            )

            Button(
                onClick = {
                    viewModel.addArticle(
                        title = title,
                        summary = summary,
                        tags = selectedRoles.map {
                            it.name
                        },
                        images = selectedImages
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkNavy
                )
            ) {
                Text(
                    text = "Publish",
                    color = Color.White
                )
            }
        }

        viewModel.activeAlert?.let { alert ->
            AlertDialog(
                onDismissRequest = {
                    viewModel.clearAlert()
                },
                title = {
                    Text(
                        if (alert == ActiveAlert.ERROR)
                            "Error"
                        else
                            "Success"
                    )
                },
                text = {
                    Text(
                        if (alert == ActiveAlert.ERROR)
                            viewModel.errorMessage
                        else
                            "Article saved successfully."
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        ),
                        onClick = {
                            viewModel.clearAlert()
                        }
                    ) {
                        Text(
                            text = "OK",
                            color = Color.White
                        )
                    }
                }
            )
        }
    }
}
