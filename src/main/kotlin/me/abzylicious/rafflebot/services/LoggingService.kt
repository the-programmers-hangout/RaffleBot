package me.abzylicious.rafflebot.services

import dev.kord.common.entity.Snowflake
import me.abzylicious.rafflebot.extensions.stdlib.isValidChannelId
import me.abzylicious.rafflebot.extensions.stdlib.toTextChannel
import me.jakejmattson.discordkt.annotations.Service

@Service
class LoggingService {
    suspend fun log(logChannelId: Snowflake, message: String) {
        if (!logChannelId.isValidChannelId()) return
        val loggingChannel = logChannelId.toTextChannel()
        loggingChannel!!.createMessage(message)
    }
}
