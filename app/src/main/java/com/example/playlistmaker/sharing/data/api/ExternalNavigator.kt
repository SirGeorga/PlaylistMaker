package com.example.playlistmaker.sharing.data.api

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String?)
    fun openLink(agreement: String?)
    fun openEmail(emailData: EmailData)
}