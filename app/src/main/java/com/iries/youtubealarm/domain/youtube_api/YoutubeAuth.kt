package com.iries.youtubealarm.domain.youtube_api

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube

object YoutubeAuth {
    private const val SCOPE: String = "https://www.googleapis.com/auth/youtube"
    private val httpTransport: HttpTransport = NetHttpTransport()
    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

    fun getSignInClient(context: Context?): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SCOPE))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context!!, gso)
    }

    fun getYoutube(context: Context): YouTube {
        val credential = authorize(context)
        return YouTube.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("Youtube")
            .build()
    }

    private fun authorize(context: Context): GoogleAccountCredential {
        val signedInAccount = GoogleSignIn.getLastSignedInAccount(context)

        val account = signedInAccount!!.account
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            setOf(SCOPE)
        )
        credential.setSelectedAccount(account)
        return credential
    }
}