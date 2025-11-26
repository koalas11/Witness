package org.wdsl.witness.service

import io.ktor.client.HttpClient

interface GoogleDriveService {
    fun uploadRecordingsToDrive()
}

class GoogleDriveServiceImpl(
    private val httpClient: HttpClient,
): GoogleDriveService {
    override fun uploadRecordingsToDrive() {

    }
}
