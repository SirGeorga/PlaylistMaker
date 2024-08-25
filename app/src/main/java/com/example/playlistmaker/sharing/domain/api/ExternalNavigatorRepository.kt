package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigatorRepository {
    fun shareLink(link: String?)
    fun sharePlaylist(message: String?)
    fun openLink(agreement: String?)
    fun openEmail(emailData: EmailData)
}