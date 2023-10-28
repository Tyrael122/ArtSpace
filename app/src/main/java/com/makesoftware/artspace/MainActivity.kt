package com.makesoftware.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makesoftware.artspace.adapters.PaintingDataParser
import com.makesoftware.artspace.ui.theme.ArtSpaceTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceTheme {
                MaterialTheme(colorScheme = lightColorScheme(), content = {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ArtSpace(
                            paintingDataParser = PaintingDataParser(LocalContext.current),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                })
            }
        }
    }
}

@Composable
fun ArtSpace(modifier: Modifier = Modifier, paintingDataParser: PaintingDataParser) {
    var paintingIndex by remember { mutableStateOf(0) }

    val paintingData = paintingDataParser.getPaintingData(paintingIndex)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Painting(paintingData)

        Spacer(Modifier.weight(1f))

        Column {
            PaintingLabel(
                title = paintingData.getString("title"),
                author = paintingData.getString("author"),
                yearCreation = paintingData.getString("year_created")
            )

            Spacer(Modifier.height(20.dp))

            val numberOfPaintings = paintingDataParser.getNumberOfPaintings()
            NavigationButtonRow(paintingIndex,
                maxPaintingIndex = numberOfPaintings - 1,
                onPaintingIndexChanged = { paintingIndex = it })
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun Painting(paintingData: JSONObject, modifier: Modifier = Modifier) {
    val imageIdentifier = LocalContext.current.resources.getIdentifier(
        paintingData.getString("image_identifier"), "drawable", LocalContext.current.packageName
    )

    PaintingFrame(
        imagePainter = if (imageIdentifier != 0) painterResource(imageIdentifier) else null,
        contentDescription = paintingData.getString("content_description")
    )
}

@Composable
fun PaintingFrame(
    imagePainter: Painter?, contentDescription: String, modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = 8.dp, modifier = modifier
            .fillMaxWidth()
            .padding(30.dp)
            .background(color = Color.Gray)
    ) {
        if (imagePainter == null) {
            NoImage()
        } else {
            Image(
                painter = imagePainter,
                contentDescription = contentDescription,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.padding(30.dp)
            )
        }
    }
}

@Composable
fun PaintingLabel(
    title: String, author: String, yearCreation: String, modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .background(color = Color(0xFFECEBF4))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Light)

            val secondRowText = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(author)
                }

                withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                    append(" ($yearCreation)")
                }
            }

            Text(secondRowText)
        }
    }
}

@Composable
fun NavigationButtonRow(
    currentPaintingIndex: Int,
    maxPaintingIndex: Int,
    modifier: Modifier = Modifier,
    onPaintingIndexChanged: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            modifier = Modifier.width(120.dp),
            onClick = { onPaintingIndexChanged(currentPaintingIndex - 1) },
            enabled = currentPaintingIndex > 0
        ) {
            Text(stringResource(R.string.button_previous))
        }

        Button(
            onClick = { onPaintingIndexChanged(currentPaintingIndex + 1) },
            modifier = Modifier.width(120.dp),
            enabled = currentPaintingIndex < maxPaintingIndex
        ) {
            Text(stringResource(R.string.button_next))
        }
    }
}

@Composable
fun NoImage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6F)
            .padding(30.dp)
    ) {
        Text("No image has been found")
    }
}