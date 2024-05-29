package com.example.shoppinglistappp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Data class created for Shopping Item parameters
data class ShoppingItem(val id:Int,
                        var name: String,
                        var quantity: Int,
                        var isEditing: Boolean = false
)

@Composable
fun ShoppingListApp(){// Add Item Column
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }   // List of Shopping Item objects of the Data Class type ShoppingItem
    var showDialog by remember { mutableStateOf(false) }    // Show dialog is false =  hide, true = show
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { showDialog = true },    // Show dialog when Add Item button is clicked
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(48.dp)
        ){
            Text(text = "Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItems){  // list of items
                item ->
                if(item.isEditing) {    // if current item is in editing mode
                    ShoppingItemEditor(item =item, onEditComplete = {
                        editedName, editedQuantity ->
                        sItems = sItems.map{ it.copy(isEditing = false) }   // New sItems will be old sItems, also set editing to false
                        val editedItem = sItems.find { it.id == item.id }   // Find the item to be edited within sItems list by ID, store in editedItem
                        editedItem?.let{    // Get editedItem and set the name and quantity, even if null
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    })
                }
                else{
                    // Finding out which item we are editing and changing the editing status to true
                    ShoppingListItem(item = item,
                        onEditClick = {
                        sItems = sItems.map{ it.copy(isEditing = it.id==item.id) }  // New sItems will be old sItems, also set editing to true
                    }, onDeleteCLick = {
                        sItems = sItems - item  // Remove item from the list
                    })
                }
            }
        }

    }
    // Alert Dialog when Add Item button is clicked
    if(showDialog){ // if showDialog is true
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = {
                                    if(itemName.isNotBlank() && itemQuantity.isNotBlank()){
                                        val newItem = ShoppingItem(
                                            id = sItems.size + 1,   // Create unique ID for each item
                                            name = itemName,    // item name
                                            quantity = itemQuantity.toInt() // item quantity from string to int
                                        )
                                        sItems = sItems + newItem   // Add item to the list
                                        showDialog = false  // Hide dialog when Add button is clicked
                                        itemName = ""   // set dialog box to empty
                                        itemQuantity = ""   // set dialog box to empty
                                    }
                                }) {
                                    Text(text = "Add")
                                }
                                Button(onClick = {showDialog = false}) {    // Hide dialog when Cancel button is clicked
                                    Text(text = "Cancel")
                                }

                            }

            },
            title = { Text(text = "Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },  // "it" is the value of the text field at any given time
                        singleLine = true,  // only one line of text in text field
                        modifier = Modifier.padding(bottom = 8.dp)  // padding at the bottom of the text field
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

        )
    }
}

// Function for the editor UI elements
@Composable
fun ShoppingItemEditor(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit
    ) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp)
            )
        }
        Button(
            onClick = {
                isEditing = false
                onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)   // Call the function to update the item, if null set to 1
            }
        ){
            Text(text = "Save")
        }
    }

}


@Composable
fun ShoppingListItem(
    item: ShoppingItem, // Shopping Item object
    onEditClick: () -> Unit,    // Edit button click function, Lambda expression
    onDeleteCLick: () -> Unit,  // Delete button click function, Lambda expression
    ) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                // Add border to the row
                border = BorderStroke(2.dp, Color.Gray),
                shape = RoundedCornerShape(percent = 20),
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))   // Display item name
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))    // Display item quantity
        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteCLick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }

}