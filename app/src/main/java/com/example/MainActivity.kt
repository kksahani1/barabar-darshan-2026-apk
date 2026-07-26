package com.example

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.BarabarTheme

const val TARGET_URL = "https://script.google.com/macros/s/AKfycbzM58JK6bqCKSYpm0BOnanVYs2EkvOr-RSlChIhvsG0lQbLomcGQtQNDHAVCETP2reSrw/exec"

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BarabarTheme {
        MainScreen()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MainScreen() {
  val context = LocalContext.current
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }
  
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(true) }
  var loadProgress by remember { mutableFloatStateOf(0f) }
  var hasError by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf("") }

  // Handle system back button to go back in WebView history
  BackHandler(enabled = canGoBack) {
    webViewInstance?.goBack()
  }

  Scaffold(
    topBar = {
      Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
      ) {
        Column {
          TopAppBar(
            title = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
              ) {
                // District Logo Icon Badge
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF8E1))
                    .border(1.5.dp, Color(0xFFD87800), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "जिला प्रशासन जहानाबाद लोगो",
                    modifier = Modifier.size(36.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                  Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = stringResource(id = R.string.subtitle_text),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            },
            actions = {
              // Back Button
              IconButton(
                onClick = { webViewInstance?.goBack() },
                enabled = canGoBack,
                modifier = Modifier.testTag("nav_back_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "पीछे जाएं",
                  tint = if (canGoBack) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f)
                )
              }

              // Forward Button
              IconButton(
                onClick = { webViewInstance?.goForward() },
                enabled = canGoForward,
                modifier = Modifier.testTag("nav_forward_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = "आगे जाएं",
                  tint = if (canGoForward) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f)
                )
              }

              // Home Button
              IconButton(
                onClick = {
                  hasError = false
                  webViewInstance?.loadUrl(TARGET_URL)
                },
                modifier = Modifier.testTag("nav_home_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Home,
                  contentDescription = "गृह पृष्ठ",
                  tint = MaterialTheme.colorScheme.primary
                )
              }

              // Refresh Button
              IconButton(
                onClick = {
                  hasError = false
                  webViewInstance?.reload()
                },
                modifier = Modifier.testTag("nav_refresh_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "पुनः लोड करें",
                  tint = MaterialTheme.colorScheme.primary
                )
              }

              // Share Button
              IconButton(
                onClick = {
                  val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "बराबर दर्शन 2026")
                    putExtra(Intent.EXTRA_TEXT, "बराबर दर्शन 2026 - जिला प्रशासन जहानाबाद:\n$TARGET_URL")
                  }
                  context.startActivity(Intent.createChooser(shareIntent, "शेयर करें"))
                },
                modifier = Modifier.testTag("nav_share_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = "शेयर करें",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            },
            colors = TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.surface
            )
          )

          // Smooth Progress Bar below TopAppBar
          AnimatedVisibility(
            visible = isLoading && !hasError,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            LinearProgressIndicator(
              progress = { loadProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (hasError) {
        ErrorScreen(
          message = if (errorMessage.isNotBlank()) errorMessage else stringResource(R.string.error_message),
          onRetry = {
            hasError = false
            webViewInstance?.loadUrl(TARGET_URL)
          },
          onOpenBrowser = {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TARGET_URL))
              context.startActivity(intent)
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
        )
      } else {
        AndroidView(
          modifier = Modifier
            .fillMaxSize()
            .testTag("webview_container"),
          factory = { ctx ->
            WebView(ctx).apply {
              layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
              )

              isFocusable = true
              isFocusableInTouchMode = true
              requestFocus()

              settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                userAgentString = userAgentString + " BarabarDarshanApp/2026"
              }

              webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                  loadProgress = newProgress / 100f
                  isLoading = newProgress < 100
                }
              }

              webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                  super.onPageStarted(view, url, favicon)
                  isLoading = true
                  hasError = false
                  canGoBack = view?.canGoBack() ?: false
                  canGoForward = view?.canGoForward() ?: false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                  super.onPageFinished(view, url)
                  isLoading = false
                  canGoBack = view?.canGoBack() ?: false
                  canGoForward = view?.canGoForward() ?: false
                }

                override fun onReceivedError(
                  view: WebView?,
                  request: WebResourceRequest?,
                  error: WebResourceError?
                ) {
                  super.onReceivedError(view, request, error)
                  if (request?.isForMainFrame == true) {
                    hasError = true
                    errorMessage = error?.description?.toString() ?: ""
                  }
                }

                override fun shouldOverrideUrlLoading(
                  view: WebView?,
                  request: WebResourceRequest?
                ): Boolean {
                  val url = request?.url?.toString() ?: return false

                  // Handle special schemes (tel, mailto, whatsapp, intent, etc.)
                  if (url.startsWith("tel:") ||
                    url.startsWith("mailto:") ||
                    url.startsWith("whatsapp:") ||
                    url.startsWith("intent:") ||
                    url.startsWith("geo:")
                  ) {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                      ctx.startActivity(intent)
                      return true
                    } catch (e: Exception) {
                      e.printStackTrace()
                      return true
                    }
                  }

                  // Default behavior: load within WebView
                  return false
                }
              }

              loadUrl(TARGET_URL)
              webViewInstance = this
            }
          },
          update = { webView ->
            webViewInstance = webView
            canGoBack = webView.canGoBack()
            canGoForward = webView.canGoForward()
          }
        )
      }
    }
  }
}

@Composable
fun ErrorScreen(
  message: String,
  onRetry: () -> Unit,
  onOpenBrowser: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
      .testTag("error_screen"),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // District Seal Badge Container
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFF8E1))
            .border(2.dp, Color(0xFFD87800), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "District Seal Logo",
            modifier = Modifier.size(60.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(id = R.string.error_title),
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = message,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = onOpenBrowser,
            modifier = Modifier
              .weight(1f)
              .testTag("browser_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.OpenInNew,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(id = R.string.open_in_browser),
              fontSize = 12.sp,
              maxLines = 1
            )
          }

          Button(
            onClick = onRetry,
            modifier = Modifier
              .weight(1f)
              .testTag("retry_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(id = R.string.retry),
              fontSize = 12.sp,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}

