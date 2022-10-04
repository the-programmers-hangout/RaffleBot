package me.abzylicious.rafflebot.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.GuildMessageChannel
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service

@Service
class LoggingService(val discord: Discord) {
    suspend fun log(logChannelId: Snowflake, message: String) {
        val logChannel = discord.kord.getChannelOf<GuildMessageChannel>(logChannelId) ?: return
        logChannel.createMessage(message)
    }
}
