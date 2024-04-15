package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val shareAppLink: String,
    private val devEmail: String,
    private val mailText: String,
    private val mailSubject: String,
    private val agreementLink: String
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return shareAppLink
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = devEmail,
            message = mailText,
            theme = mailSubject
        )
    }

    private fun getTermsLink(): String {
        return agreementLink
    }

}