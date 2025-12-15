package org.wdsl.witness.shared

/**
 * Defines the message path constants used for communication
 * between the wearable device and the paired phone
 */
object WearableMessageConstants {

    // Messages sent from the wearable to the phone
    const val HELP_MESSAGE_PATH = "/WitnessHelpMessage"
    const val WHISTLE_MESSAGE_PATH = "/WitnessWhistleMessage"

    // Messages sent from the phone to the wearable
    const val HELP_CONFIRMATION_PATH = "/WitnessHelpConfirmationMessage"
    const val HELP_STOP_PATH = "/WitnessHelpStopMessage"
    const val WHISTLE_CONFIRMATION_PATH = "/WitnessWhistleConfirmationMessage"
}