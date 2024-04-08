package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.data.settings.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {
    val context = App.context
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String? {
        return context?.resources?.getString(R.string.st_practicum_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context?.resources?.getString(R.string.st_dev_email),
            message = context?.resources?.getString(R.string.st_mail_text),
            theme = context?.resources?.getString(R.string.st_mail_subject)
        )
    }

    private fun getTermsLink(): String? {
        return context?.resources?.getString(R.string.st_agreement_link)
    }

}