/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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