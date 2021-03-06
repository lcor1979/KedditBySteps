package com.droidcba.kedditbysteps.features.news

import com.droidcba.kedditbysteps.api.NewsAPI
import com.droidcba.kedditbysteps.commons.RedditNews
import com.droidcba.kedditbysteps.commons.RedditNewsItem
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * News Manager allows you to request news from Reddit API.
 *
 * @author juancho
 */
@Singleton
class NewsManager @Inject constructor(private val api: NewsAPI) {

    /**
     *
     * Returns Reddit News paginated by the given limit.
     *
     * @param after indicates the next page to navigate.
     * @param limit the number of news to request.
     */
    fun getNews(after: String, limit: String = "10"): Observable<RedditNews> {
        return api.getNews(after, limit).map {
            response ->
            val news = response.data.children.map {
                val item = it.data
                RedditNewsItem(item.author, item.title, item.num_comments,
                        item.created, item.thumbnail, item.url)
            }

            RedditNews(
                    response.data.after ?: "",
                    response.data.before ?: "",
                    news)
        }
    }
}