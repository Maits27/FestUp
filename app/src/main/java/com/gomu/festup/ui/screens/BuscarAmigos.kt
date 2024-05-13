package com.gomu.festup.ui.screens

import android.content.ContentResolver
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.vm.MainVM
import java.time.Instant
import java.util.Date

@Composable
fun BuscarAmigos(
    mainVM: MainVM,
    navController: NavController
) {

    // Esto se incluiría en el parámetro "projection" de "query"
    /*val FROM_COLUMNS: Array<String> = arrayOf(
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        } else {
            ContactsContract.Contacts.DISPLAY_NAME
        }
    )*/

    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    val cursor = contentResolver.query(uri, null, null, null, null)

    if (cursor != null) {
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                Log.d("Contact", "Name: $contactName, Number: $contactNumber")
            }
        }
    }
    cursor?.close()

    LazyColumn {
        item {
            UsuarioCard(
                usuario = Usuario(username = "pepe", "a@a", "Pepe", Date.from(Instant.now())),
                mainVM = mainVM,
                navController = navController
            )
        }
    }
}