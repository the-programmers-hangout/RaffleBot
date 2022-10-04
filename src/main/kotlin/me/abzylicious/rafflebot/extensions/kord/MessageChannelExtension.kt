package me.abzylicious.rafflebot.extensions.kord

import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.MessageChannel
import me.abzylicious.rafflebot.extensions.stdlib.isEmoji
import me.abzylicious.rafflebot.extensions.stdlib.isGuildEmote
import me.abzylicious.rafflebot.extensions.stdlib.toGuildEmote
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.toSnowflake

private lateinit var api: Kord

@Service
class ApiInitializer(discord: Discord) { init { api = discord.kord } }

suspend fun MessageChannel.addReaction(guildId: Long, messageId: Long, reaction: String) {
    if (reaction.isGuildEmote(guildId))
        this.getMessage(messageId.toSnowflake()).addReaction(reaction.toGuildEmote(guildId)!!)

    if (reaction.isEmoji())
        this.getMessage(messageId.toSnowflake()).addReaction(ReactionEmoji.Unicode(reaction))
}
