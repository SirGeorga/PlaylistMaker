package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.api.ExternalNavigatorRepository
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorRepositoryImpl(private val context: Context) : ExternalNavigatorRepository {
    override fun shareLink(link: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        val chooserIntent = Intent.createChooser(shareIntent, null)
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooserIntent)
    }

    override fun sharePlaylist(message: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        val chooserIntent = Intent.createChooser(shareIntent, null)
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooserIntent)
    }

    override fun openLink(agreement: String?) {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(agreement)
        agreementIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(agreementIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, emailData.email)
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.theme)
        supportIntent.putExtra(Intent.EXTRA_TEXT, emailData.message)
        supportIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(supportIntent)
    }
}