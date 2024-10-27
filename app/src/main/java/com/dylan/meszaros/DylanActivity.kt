package com.dylan.meszaros

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.text.isDigitsOnly
import com.dylan.meszaros.data.Contact
import com.dylan.meszaros.di.appModules
import com.dylan.meszaros.ui.theme.MyTestTheme
import com.dylan.meszaros.viewmodel.ContactViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin
import com.google.android.material.snackbar.Snackbar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

var onStartup = false;

class DylanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (onStartup == false){
            onStartup = true;
            startKoin {
                modules(appModules)
            }
        }
        enableEdgeToEdge()
        setContent {
            MyTestTheme {
                DylanActivity_Main(onContactWindow = {
                    startActivity(Intent(this@DylanActivity, MeszarosActivity::class.java));
                    finish();
                });
            }
        }
    }
}

class MeszarosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTestTheme {
                val layout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                }

                MeszarosActivity_Main(layout);
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DylanActivity_Main(onContactWindow: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.contactslogo),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .size(256.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.contactsbutton),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .size(208.dp)
                    .clickable {
                        onContactWindow();
                    }
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeszarosActivity_Main(view: View) {
    val contactViewModel: ContactViewModel = koinViewModel();
    val isFABVisible = remember { mutableStateOf(true) };

    val scaffoldState = rememberBottomSheetScaffoldState();
    val coroutineScope = rememberCoroutineScope();

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Contacts")
                },
                colors =  TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            if (isFABVisible.value){
                FloatingActionButton(onClick = {
                    isFABVisible.value = false;
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact");
                }
            }
        },
        content =  { paddingValues ->
            val name = null;
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                if (isFABVisible.value){
                    ContactList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp),
                    );
                }
                else{
                    ContactCreator(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp),
                    view,
                        onAddContact = { contact ->
                            isFABVisible.value = true;
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(message = "Added " + contact.name + "!", duration = SnackbarDuration.Short)
                            }
                    });
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = scaffoldState.snackbarHostState)
        }
    )
}

@Composable
fun ContactList(modifier: Modifier) {
    val contactViewModel: ContactViewModel = koinViewModel();

    LazyColumn(
        modifier = modifier
    ) {
        items(contactViewModel.getContacts()) { contact ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Display_ContactCard(contact);
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Display_ContactCard(contact: Contact) {
    val displayColor: Color = when {
        contact.contactType == "Friend" -> Color.Green
        contact.contactType == "Family" -> Color.Magenta
        contact.contactType == "Work" -> Color.Blue
        else -> Color.Gray // Default color for other types
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(3.dp, displayColor), shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(

        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = contact.name, style = MaterialTheme.typography.headlineLarge);
            }
            Spacer(modifier = Modifier.height(4.dp));

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactCreator(modifier: Modifier, view: View, onAddContact: (Contact) -> Unit){
    val contactViewModel: ContactViewModel = koinViewModel();

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Make a New Contact")
                },
                colors =  TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        content =  { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                var name by remember { mutableStateOf("") };

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                );
                Spacer(modifier = Modifier.height(8.dp));

                var phoneNumber by remember { mutableStateOf("") };
                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                );
                Spacer(modifier = Modifier.height(16.dp));

                var email by remember { mutableStateOf("") };
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    maxLines = 1
                );
                Spacer(modifier = Modifier.height(16.dp));

                var selectedContactType by remember { mutableStateOf("") }
                Row {
                    val contactTypes = listOf("Friend", "Family", "Work");
                    contactTypes.forEach { contactType ->
                        Row(
                            modifier = Modifier
                                .clickable { selectedContactType = contactType }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = (selectedContactType == contactType),
                                onCheckedChange = { selectedContactType = if (it) contactType else "Friend" }
                            )
                            Text(text = contactType, modifier = Modifier.padding(start = 4.dp));
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp));

                Button(onClick = {
                    val newContact = Contact(contactViewModel.getContacts().count() + 1, name, phoneNumber, email, selectedContactType);
                    contactViewModel.addContact(newContact)

                    onAddContact(newContact);
                }) {
                    Text("Add Contact");
                }
            }
        }
    )
}