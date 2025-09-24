package com.example

import com.iries.alarm.data.remote.SearchApiRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ApiTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: SearchApiRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `findRandomTrackByArtist returns randomTrackResponse`() = runTest {
        val artistId: Long = 203412507
        val randomTrackResponseResult = repository.findRandomTrackByArtist(artistId)

        assertTrue(randomTrackResponseResult.isSuccess)
        val randomTrackResponse = randomTrackResponseResult.getOrNull()
        assertEquals(artistId, randomTrackResponse?.artistId)
    }

    @Test
    fun `findArtistByName returns artist`() = runTest {
        val query = "Mozart"
        val randomTrackResponseResult = repository.findArtistsByName(query)

        assertTrue(randomTrackResponseResult.isSuccess)
        val randomTrackResponse = randomTrackResponseResult.getOrNull()
        assertEquals(query, randomTrackResponse?.get(0)?.username?.contains(query) ?: "")
    }
}