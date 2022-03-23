package com.raywenderlich.android.redditclone.repositories

import androidx.paging.PagingSource
import com.raywenderlich.android.redditclone.models.RedditPost
import com.raywenderlich.android.redditclone.networking.RedditService
import retrofit2.HttpException
import java.io.IOException

class RedditPagingSource(private val redditService: RedditService) :

// String Param: API provides before and after keys, tell API how to fetch next/prev data page
// RedditPost Param: Info received from Reddit API (posts of type RedditPost)
    PagingSource<String, RedditPost>() {

    //enable key reuse!! or app will crash after scrolling down a bit
    override val keyReuseSupported: Boolean = true


    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditPost> {
        return try {
            // 1 Fetch List of Posts from REDDIT API, pass loadSize as params
            val response = redditService.fetchPosts(loadSize = params.loadSize)
            // 2 Get list of posts from response body
            val listing = response.body()?.data
            val redditPosts = listing?.children?.map { it.data }
            // 3 Create an instance of LoadResult.page
            LoadResult.Page(
                // 4 Pass in List of Reddit Posts
                redditPosts ?: listOf(),
                // 5 Pass in before and after keys from API response body
                listing?.before,
                listing?.after
            )
        } catch (exception: IOException) { // 6 exception handling
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}