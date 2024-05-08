package com.medium.d.kostadinov.repo1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medium.d.kostadinov.repo1.ui.theme.MediumMinAmountofLinesTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediumMinAmountofLinesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostList()
                }
            }
        }
    }

    @Composable
    fun PostList(postsViewModel: PostsViewModel = viewModel()) {
        val posts by postsViewModel.posts.collectAsState(initial = emptyList())

        LazyColumn {
            items(posts) { post ->
                Text("Title: ${post.title}")
            }
        }
    }

    @Serializable
    data class Post(val userId: Int, val id: Int, val title: String, val body: String)

    class PostsViewModel : ViewModel() {
        private val client = HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
        }

        private val _posts = MutableStateFlow<List<Post>>(emptyList())
        val posts: StateFlow<List<Post>> = _posts

        init {
            viewModelScope.launch {
                _posts.value = fetchPosts()
            }
        }

        private suspend fun fetchPosts(): List<Post> {
            return client.get("https://jsonplaceholder.typicode.com/posts").body()
        }
    }
}