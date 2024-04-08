package com.example.playlistmaker.data.settings

import com.example.playlistmaker.domain.sharing.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String?)
    fun openLink(agreement: String?)
    fun openEmail(emailData: EmailData)
}