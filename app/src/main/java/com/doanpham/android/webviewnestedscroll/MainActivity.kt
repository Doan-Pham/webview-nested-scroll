package com.doanpham.android.webviewnestedscroll

import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.doanpham.android.webviewnestedscroll.ui.theme.WebViewNestedScrollTheme
import rememberScrollState
import verticalScroll

class NoScrollWebView(
    context: Context,
) : WebView(context) {

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
        println(">>>David - NoScrollWebView scrollTo: x: $x, y: $y")

    }

    override fun computeScroll() {
//        super.computeScroll()
        println(">>>David - NoScrollWebView computeScroll")

    }

    override fun scrollBy(x: Int, y: Int) {
        super.scrollBy(x, y)
        println(">>>David - NoScrollWebView scrollBy: x: $x, y: $y")

    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        println(">>>David - NoScrollWebView onScrollChanged: t: $t, oldt: $oldt")

        // Force scroll back to (0, 0) to prevent internal scrolling
//        if (t != 0 || l != 0) {
//            scrollTo(0, 0)
//        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val html8000 = buildString {
                val header = """
                    <!DOCTYPE html>
                    <html>
                      <head>
                        <meta charset="utf-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">                        <title>Editable WebView</title>
                        <style>
                          body { font-family: sans-serif; padding: 1rem; }
                          h1 { color: darkblue; }
                          .editable { border: 1px solid #ccc; padding: 1rem; min-height: 400px; }
                        </style>
                      </head>
                      <body contenteditable="true">
                        <h1>This is the starting line</h1>
                        <div class="editable">
                          <p>You can type here…</p>
                """.trimIndent()

                val footer = """
                        </div>
                      </body>
                    </html>
                """.trimIndent()

                append(header)

                // Generate repeated paragraphs to reach ~8000 characters
                val filler =
                    "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec eget.</p>"
                var i = 1
                while ((length + footer.length) < 8000) {
                    append("LINE ${i++} $filler")
                }

                append(footer)
            }

            val scrollState = rememberScrollState()

            Column {
                Spacer(Modifier.height(80.dp))
                Column(
                    modifier = Modifier
                        .border(3.dp, Color.Red)
                        .onSizeChanged {
                            println("7453 - Column - size: $it")
                        }
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                ) {
                    Text("Big Title", modifier = Modifier.height(50.dp))

                    // WebView inside Compose
                    AndroidView(
                        modifier = Modifier
                            .border(2.dp, Color.Blue)
                            .fillMaxSize(),
                        factory = { context ->
                            NoScrollWebView(context).apply {
//                                settings.builtInZoomControls = false  // Disables pinch/zoom gestures
//                                settings.displayZoomControls = false  // Hides zoom buttons
//                                settings.setSupportZoom(false)


                                loadDataWithBaseURL(
                                    null,
                                    html8000,
                                    "text/html",
                                    "utf-8",
                                    null
                                )
                            }
                        }
                    )
                }
                Spacer(Modifier.height(80.dp))
            }



            LaunchedEffect(Unit) {
                snapshotFlow { scrollState.value }.collect { println("7453 - scrollState value: $it") }
            }
            LaunchedEffect(scrollState.maxValue) {
                println("7453 - scrollState maxValue: ${scrollState.maxValue}")
            }
            LaunchedEffect(scrollState.viewportSize) {
                println("7453 - scrollState viewportSize: ${scrollState.viewportSize}")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WebViewNestedScrollTheme {
        Greeting("Android")
    }
}