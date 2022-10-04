package me.abzylicious.rafflebot.extensions.kord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.MessageChannel
import me.abzylicious.rafflebot.extensions.stdlib.isEmoji
import me.abzylicious.rafflebot.extensions.stdlib.isGuildEmote
import me.abzylicious.rafflebot.extensions.stdlib.toGuildEmote
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.toSnowflake

suspend fun MessageChannel.addReaction(guildId: Snowflake, messageId: Snowflake, reaction: String) {
    if (reaction.isGuildEmote(guildId))
        this.getMessage(messageId).addReaction(reaction.toGuildEmote(guildId)!!)

    if (reaction.isEmoji())
        this.getMessage(messageId).addReaction(ReactionEmoji.Unicode(reaction))
}
