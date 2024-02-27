package com.example.myapplication

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import okhttp3.*
import java.io.IOException
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.RetrofitClient
import com.example.myapplication.LoginRequest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider

import java.io.File


import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.net.URI
import java.net.URLDecoder


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                LoginScreen()
            }
        }
    }
}
fun performLogin(userName: String, password: String,callback: (Boolean,LoginResponse?) -> Unit) {
    RetrofitClient.apiService.login(LoginRequest(userName, password)).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            if (response.isSuccessful) {
                // Handle successful response
                val loginResponse = response.body()
                // Do something with the response, e.g., navigate to another activity
                callback(true,loginResponse)

            } else {
                // Handle error response, e.g., show error message
                callback(false,null)
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            // Handle call failure, e.g., no internet connection
        }
    })
}
@Composable fun LoginScreen() {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialog2 by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    var loginResult by remember { mutableStateOf<Boolean?>(null) }
    var access_toke   by remember { mutableStateOf("") }

    var showDialogfail by remember { mutableStateOf(false) }



    // State for error messages
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    Surface() {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black), // Set the background color to black
                contentAlignment = Alignment.Center // Center the content inside the Box
            ) {
                Text(
                    "Σύνδεση",
                    style = TextStyle(color = Color.White, fontSize = 30.sp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("UserID", style = TextStyle(color = Color.White, fontSize = 15.sp))
                        Spacer(modifier = Modifier.width(4.dp)) // Spacing between label and icon text
                        Image(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Info",
                            modifier = Modifier
                                .clickable { showDialog  = true }
                        )
                    }
                }
            )

            if (showDialog) {
                InfoDialog("UserID field accepts only numbers & letters.UserID must start with two capital letters and afterwards 4 numbers.", { showDialog = false })}
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },

                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Password", style = TextStyle(color = Color.White, fontSize = 15.sp))
                        Spacer(modifier = Modifier.width(4.dp)) // Spacing between label and icon text
                        Image(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Info",
                            modifier = Modifier
                                .clickable { showDialog2  = true }
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    // Using Text as a toggle button, but you can customize it as needed
                    Text(
                        text = if (passwordVisible) "Κρυψε" else "Προβολή",
                        color = Color(0xFF00C853),
                        fontSize = 10.sp,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                }
            )
            if (showDialog2) {
                InfoDialog("τουλάχιστον 8 χαρακτήρες (2 κεφαλαία, 3 πεζά, 1 ειδικός χαρακτήρας, 2 νούμερα)", { showDialog2 = false })}
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Add padding as needed
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {  performLogin(userId,password) { success , response->
                        loginResult = success

                        if(loginResult == true){
                           // Toast.makeText(context," ${response?.access_token}", Toast.LENGTH_SHORT).show()
                            access_toke= response?.access_token.toString()
                            RetrofitClient2.setBearerToken(access_toke)

                        }
                        else{
                            showDialogfail=true
                        }

                    }  }
                    ,   modifier = Modifier
                        .size(width = 200.dp, height = 80.dp)
                        .padding(bottom = 32.dp),
                    border = BorderStroke(1.dp, Color(0xFF00C853)), // Here is where you set the border color
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0xFF1E1E1E), // Gray background color
                        contentColor = Color(0xFF00C853)
                    )
                ) {
                    Text("Σύνδεση")
                }

            }
        }
    }
    if(loginResult==true){ MainPage()}
if( showDialogfail){CustomDialog(onDismissRequest = {  showDialogfail = false }

)
 }

    Spacer(modifier = Modifier.height(20.dp))
}


@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    val customTypography = Typography(
        bodyLarge = TextStyle(color = Color.White),
        // Define other text styles as needed, setting the color to White or any other desired color
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            // Define other theme colors as needed p
            primary = Color(0xFF00C853),
            secondary = Color(0xFF757575), // Dark gray
            tertiary = Color(0xFFFFFFFF), // White
            onPrimary = Color.White, // Assuming the text on the primary colored button is white
            // Define other onColors as needed
            background = Color(0xFF121212), // A dark grey color for the background
            surface = Color(0xFF1E1E1E), // A slightly lighter grey for cards or surface components
            error = Color(0xFFCF6679), // Assuming a default Material Design error color
            onBackground = Color.White, // Text color for the background, assuming it's white for visibility
            onSurface = Color.White,
        ),

        content = content
    )
}

@Composable
fun MainPage() {
    val context = LocalContext.current
    var books by remember { mutableStateOf<List<Book>?>(null) }
    var gotboks by remember { mutableStateOf(false) }
    val booksByMonthState = remember { mutableStateOf<Map<String, List<Book>>>(emptyMap()) }
    var flag by remember { mutableStateOf(false) }

    // Define the heights of the header and bottom navigation for padding
    val headerHeight = 74.dp
    val bottomNavigationHeight = 104.dp

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1E1E1E) // Uses the background color from the theme
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top header
            Surface(
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth(),
                color = Color(0xFF101417) // Primary color for the header
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Περιοδικά",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }

            // Main content area with padding to prevent overlap
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 0.dp, bottom = 0.dp)
            ) {
                if (gotboks) {
                    val booksByYearThenMonth = books?.sortedByDescending { it.date_released }
                        ?.groupBy { book ->
                            val (year, month, _) = book.date_released.split("-")
                            "$year-$month"
                        }?.toSortedMap(Comparator { o1, o2 ->
                            val (year1, month1) = o1.split("-").let { it[0] to it[1] }
                            val (year2, month2) = o2.split("-").let { it[0] to it[1] }
                            if (year1 == year2) month2.toInt().compareTo(month1.toInt()) else year2.toInt().compareTo(year1.toInt())
                        })
                    booksByYearThenMonth?.let { BookList(booksByMonth = it) }
                }
            }

            // Bottom navigation
            Surface(
                modifier = Modifier
                    .height(bottomNavigationHeight)
                    .fillMaxWidth(),
                color = Color(0xFF1E1E1E)
            ) {
                // Your bottom navigation content here
                Image(
                    painter = painterResource(R.drawable.img),
                    contentDescription = "Background",
                    contentScale = ContentScale.FillWidth
                )
                Image(painter = painterResource(R.drawable.img_1),
                    contentDescription = "Background2",
                    contentScale = ContentScale.FillWidth)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Using images from drawable as icons
                    Image(painter = painterResource(id = R.drawable.ic_book_sel), contentDescription = "Home", modifier = Modifier
                        .size(28.dp)
                        .clickable { })
                    Image(painter = painterResource(id = R.drawable.ic_misc), contentDescription = "Search", modifier = Modifier
                        .size(28.dp)
                        .clickable { } )
                    Image(painter = painterResource(id = R.drawable.btn_play), contentDescription = "Favorites", modifier = Modifier
                        .size(76.dp)
                        .clickable {
                            RetrofitClient2.apiService
                                .getBooks()
                                .enqueue(object : Callback<List<Book>> {
                                    override fun onResponse(
                                        call: Call<List<Book>>,
                                        response: Response<List<Book>>
                                    ) {
                                        if (response.isSuccessful) {
                                            // Handle successful response
                                            gotboks = true
                                            books = response.body()
                                            // Use your list of books
                                            books?.let {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Fetched ${it.size} books",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                            }
                                        } else {
                                            // Handle error response
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Error fetching books: ${response.message()}",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    }
                                    override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                                        // Handle failure to execute the call
                                    }
                                })
                        } )
                    Image(painter = painterResource(id = R.drawable.ic_link), contentDescription = "Notifications", modifier = Modifier
                        .size(28.dp)
                        .clickable { } )
                    Image(painter = painterResource(id = R.drawable.ic_settings), contentDescription = "Profile", modifier = Modifier
                        .size(28.dp)
                        .clickable { } )
                }
            }
        }
    }
}
@Composable
fun CustomDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        // Use a Surface for background and elevation
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .size(width = 268.dp, height = 124.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Λάθος στοιχεία",style =  TextStyle(fontSize = 18.sp)// Increase the font size
                )
                Text(
                    text = "Λάθος στοιχεία",style =  TextStyle(fontSize = 14.sp)

                )


                Divider(modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth() )

                // Add more content below the divider if needed
                Text(
                    text = "Eπιστροφή",style =  TextStyle(fontSize = 19.sp), color = Color.Green,
                            modifier = Modifier.clickable { onDismissRequest()}

                )
            }
        }
    }
}

@Composable
fun InfoDialog(message: String,onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        // Custom box with specific dimensions and some text inside
        Surface(
            modifier = Modifier
                .size(width = 300.dp, height = 75.dp),
            color =  Color(0xE6101417),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(message,  color = Color(0xFFF2F2F2))
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookList(booksByMonth: Map<String, List<Book>>) {

    LazyColumn {
        booksByMonth.forEach { (month, books) ->
            // Sticky header for each month
            stickyHeader {
                Text(
                    text = month,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E))
                        .padding(8.dp)
                )
            }
            // Manually handling the organization of books into rows of two under their respective headers.
            val groupedBooks = books.chunked(2) // This creates a List of Lists, where each inner list has up to two books.
            groupedBooks.forEach { bookPair ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        bookPair.forEachIndexed { index, book ->
                            BookItem(book)
                            // Add a spacer after the first book but not after the last one in a pair
                            if (index == 0 && bookPair.size == 2) {
                                Spacer(modifier = Modifier.width(20.dp)) // Adjust the width as needed for spacing
                            }
                        }
                        // If there's only one book in the pair, add an empty space to keep the alignment.
                        if (bookPair.size < 2) {
                            Spacer(modifier = Modifier.size(130.dp, 170.dp)) // Adjust size according to your BookItem card size
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book) {
    var isDownloaded by remember { mutableStateOf(false) }
    var displayText by remember { mutableStateOf("Hello, Compose!") }
    var flag by remember { mutableStateOf(false) }
    var state by remember { mutableStateOf(1) }
    var fileUriString by remember { mutableStateOf("Hello, Compose!") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(8.dp)) {
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.size(130.dp, 170.dp), // Size of the Card
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(model = ensureHttps(book.img_url)),
                    contentDescription = "Book Cover",
                    modifier = Modifier
                        .size(130.dp, 170.dp).clickable(){
                            if  (state==2){
                            openDownloadedPdf(context,fileUriString)}
                                                         },
                    contentScale = ContentScale.Crop
                )
                // Small image overlay

                val overlayImage = if (isDownloaded) {
                    R.drawable.ic_check_w
                } else {
                    R.drawable.ic_download
                }
                if (state==1){
                Image(
                    painter = painterResource(id = R.drawable.ic_download), // Replace smallImageURL with your small image source
                    contentDescription = "Overlay Image",
                    modifier = Modifier
                        .size(50.dp, 50.dp) // Adjust the size as needed
                        .align(Alignment.Center) // Position the small image; adjust as needed
                        .clickable(){val downloadRequest = DownloadManager.Request(Uri.parse(book.pdf_url))
                            .setTitle(book.title)
                            .setDescription("Downloading...")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS," ${book.title}.pdf")

                            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            val downloadId = downloadManager.enqueue(downloadRequest)
                            val receiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {
                                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
                                    if (id == downloadId) {
                                        // Handle download completion
                                        val query: DownloadManager.Query = DownloadManager.Query().setFilterById(downloadId)
                                        val cursor = downloadManager.query(query)
                                        if (cursor.moveToFirst()) {

                                            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                                            if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(statusIndex)) {

                                                // Download is successful, get the file URI and handle it
                                                val fileUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                                fileUriString = cursor.getString(fileUriIndex)

                                                val fileUri = Uri.parse(fileUriString)
                                                Log.d("MyTag", fileUri.toString())
                                                Log.d("d",fileUriString)
                                                state=2
                                                //openDownloadedPdf(context,fileUriString)
                                            }
                                        }
                                    }
                                }
                            }
                            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                            state=3



                        }
                )}
                if(state==2){
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Align the wrapping Box to the bottom right
                        .size(40.dp, 46.dp) // Set the size of the wrapping Box
                ) {
                    DiagonalHalfTransparentGreenBox()
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_check_w), // Replace with your image resource
                    contentDescription = "Top Right Image",
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Align to the top-end corner
                        .size(width = 16.dp, height = 11.11.dp) // Set the custom size
                        .padding(bottom = 4.dp)
                )}
                if(state==3){Image(
                    painter = painterResource(id = R.drawable.progres), // Replace with your image resource
                    contentDescription = "progress",
                    modifier = Modifier
                        .align(Alignment.BottomEnd).size(130.dp,12.dp))

                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        val processedTitle = if (book.title.length > 18) {
            book.title.chunked(15).joinToString("\n")
        } else {
            book.title
        }

        Text(
            text = processedTitle,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { //state=2

                },maxLines = 5,
            overflow = TextOverflow.Ellipsis

        )
    }
   }
fun openDownloadedPdf(context: Context?, fileUriString: String) {
    val file = File(URI.create(fileUriString))
    val contentUri = FileProvider.getUriForFile(context!!, "${context.applicationContext.packageName}.provider", file)

    val openPdfIntent = Intent(Intent.ACTION_VIEW)
    openPdfIntent.setDataAndType(contentUri, "application/pdf")
    openPdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION

    try {
        context.startActivity(openPdfIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No application found to open this PDF.", Toast.LENGTH_LONG).show()
    }
}


@Composable
fun DiagonalHalfTransparentGreenBox() {
    val boxWidth = 40.dp
    val boxHeight = 46.dp

    Canvas(modifier = Modifier.size(width = boxWidth, height = boxHeight)) {

        val canvasWidth = size.width
        val canvasHeight = size.height


        val path = Path().apply {
            moveTo(canvasWidth, 0f)
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        // Draw the green triangle
        drawPath(
            path = path,
            color =Color(0xFF50A235)
        )
    }
}
fun ensureHttps(url: String): String {
    return if (url.startsWith("http://")) {
        url.replaceFirst("http://", "https://")
    } else {
        url
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        MainPage()
    }
}

