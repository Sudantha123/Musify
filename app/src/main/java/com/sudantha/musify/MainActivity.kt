package com.sudantha.musify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val bg = Color(0xFF10131B)
private val panel = Color(0xFF1B202C)
private val purple = Color(0xFF9B87F5)
private val muted = Color(0xFF9AA3B5)

private data class Track(val id: Int, val title: String, val artist: String, val color: Color)
private val demo = listOf(
    Track(1, "Midnight City", "M83", Color(0xFF635BCE)),
    Track(2, "The Nights", "Avicii", Color(0xFFCF785D)),
    Track(3, "Blinding Lights", "The Weeknd", Color(0xFFB54B72)),
    Track(4, "Night Changes", "One Direction", Color(0xFF487E9B)),
    Track(5, "Perfect", "Ed Sheeran", Color(0xFF7D9462)),
    Track(6, "Heat Waves", "Glass Animals", Color(0xFFBA8A49))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MusifyApp() }
    }
}

@Composable
private fun MusifyApp() {
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(0) }
    var selected by remember { mutableStateOf(demo.first()) }
    var playing by remember { mutableStateOf(false) }
    val tracks = remember(query) {
        if (query.isBlank()) demo else demo.filter {
            it.title.contains(query, true) || it.artist.contains(query, true)
        }
    }
    MaterialTheme(colorScheme = darkColorScheme(primary = purple, background = bg, surface = panel)) {
        Scaffold(
            containerColor = bg,
            bottomBar = {
                Column {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp)).background(panel).clickable { playing = !playing }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Art(selected, Modifier.size(42.dp))
                        Column(Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(selected.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text(selected.artist, color = muted, fontSize = 12.sp)
                        }
                        IconButton(onClick = { playing = !playing }) {
                            Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, "Play/Pause")
                        }
                        IconButton(onClick = { selected = demo[(selected.id) % demo.size]; playing = true }) {
                            Icon(Icons.Default.SkipNext, "Next")
                        }
                    }
                    NavigationBar(containerColor = Color(0xFF151923)) {
                        listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.LibraryMusic).forEachIndexed { i, icon ->
                            NavigationBarItem(
                                selected = tab == i, onClick = { tab = i },
                                icon = { Icon(icon, contentDescription = null) },
                                label = { Text(listOf("Home", "Search", "Library")[i]) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) {
                Text("MUSIFY", color = purple, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp, modifier = Modifier.padding(top = 22.dp))
                Text(
                    when (tab) { 1 -> "Find your music"; 2 -> "Your library"; else -> "Your sound. Your space." },
                    color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 5.dp, bottom = 16.dp)
                )
                if (tab == 1) {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Songs, artists…") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        singleLine = true
                    )
                } else if (tab == 0) {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF39306D), Color(0xFF20283B))))
                            .clickable { tab = 1 }.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("FIND YOUR NEXT\nFAVOURITE", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("Search songs and artists", color = Color.LightGray, modifier = Modifier.padding(top = 8.dp))
                        }
                        Icon(Icons.Default.ArrowForward, null, tint = purple)
                    }
                } else {
                    Text("Local demo library · account sync not connected", color = muted, fontSize = 12.sp)
                }
                Text(if (tab == 1 && query.isNotBlank()) "SEARCH RESULTS" else "QUICK PICKS",
                    color = muted, fontSize = 11.sp, letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(if (tab == 1) tracks else demo, key = { it.id }) { track ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable {
                                selected = track; playing = true
                            }.padding(vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Art(track, Modifier.size(54.dp))
                            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                Text(track.title, color = Color.White, fontWeight = FontWeight.SemiBold,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(track.artist, color = muted, fontSize = 12.sp)
                            }
                            IconButton(onClick = { selected = track; playing = true }) {
                                Icon(Icons.Default.PlayArrow, "Play", tint = purple)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Art(track: Track, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(
        Brush.linearGradient(listOf(track.color, track.color.copy(alpha = .35f), panel))
    ), contentAlignment = Alignment.Center) {
        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
    }
}
