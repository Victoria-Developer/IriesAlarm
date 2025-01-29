package com.iries.youtubealarm.data.network

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

object YoutubeAuth {
    private const val SCOPE: String = "https://www.googleapis.com/auth/youtube"

    fun getSignInClient(context: Context?): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SCOPE))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context!!, gso)
    }

    fun authorize(context: Context): GoogleAccountCredential {
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