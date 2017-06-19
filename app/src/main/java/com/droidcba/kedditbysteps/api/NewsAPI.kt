package com.droidcba.kedditbysteps.api

import io.reactivex.Observable

/**
 * News API
 *
 * @author juancho.
 */
interface NewsAPI {
    fun getNews(after: String, limit: String): Observable<RedditNewsResponse>
}