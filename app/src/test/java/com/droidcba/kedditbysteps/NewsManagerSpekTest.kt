package com.droidcba.kedditbysteps

import com.droidcba.kedditbysteps.api.*
import com.droidcba.kedditbysteps.commons.RedditNews
import com.droidcba.kedditbysteps.features.news.NewsManager
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.Spek
import java.util.*

/**
 * Unit Tests for NewsManager using Spek
 *
 * @author juancho.
 */
class NewsManagerSpekTest : Spek({

    given("a NewsManager") {
        var testSub = TestObserver<RedditNews>()
        var apiMock = mock<NewsAPI>()

        beforeEach {
            testSub = TestObserver<RedditNews>()
            apiMock = mock<NewsAPI>()
        }

        on("service returns something") {
            beforeEach {
                // prepare
                val redditNewsResponse = RedditNewsResponse(RedditDataResponse(listOf(), null, null))
                val response = Observable.fromArray(redditNewsResponse)

                whenever(apiMock.getNews(any(), any())).thenReturn(response)

                // call
                val newsManager = NewsManager(apiMock)
                newsManager.getNews("").subscribe(testSub)
            }

            it("should receive something and no errors") {
                testSub.assertNoErrors()
                testSub.assertValueCount(1)
                testSub.assertComplete()
            }
        }

        on("service returns just one news") {
            val newsData = RedditNewsDataResponse(
                    "author",
                    "title",
                    10,
                    Date().time,
                    "thumbnail",
                    "url"
            )
            beforeEach {
                // prepare
                val newsResponse = RedditChildrenResponse(newsData)
                val redditNewsResponse = RedditNewsResponse(RedditDataResponse(listOf(newsResponse), null, null))
                val response = Observable.fromArray(redditNewsResponse)

                whenever(apiMock.getNews(any(), any())).thenReturn(response)

                // call
                val newsManager = NewsManager(apiMock)
                newsManager.getNews("").subscribe(testSub)
            }

            it("should process only one news successfully") {
                testSub.assertNoErrors()
                testSub.assertValueCount(1)
                testSub.assertComplete()

                assert(testSub.values()[0].news[0].author == newsData.author)
                assert(testSub.values()[0].news[0].title == newsData.title)
            }
        }

        on("service returns a 500 error") {
            beforeEach {
                // prepare
                val response = Observable.error<RedditNewsResponse>(RuntimeException("error"))

                whenever(apiMock.getNews(any(), any())).thenReturn(response)

                // call
                val newsManager = NewsManager(apiMock)
                newsManager.getNews("").subscribe(testSub)
            }

            it("should receive an onError message") {
                assert(testSub.errorCount() == 1)
            }
        }
    }
})