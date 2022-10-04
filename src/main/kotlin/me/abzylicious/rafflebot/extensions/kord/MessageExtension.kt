package me.abzylicious.rafflebot.extensions.kord

import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji

fun Message.getReaction(reaction: String): ReactionEmoji? {
    val optionalReaction = reactions.parallelStream()
        .filter { it.emoji.name == reaction || it.id?.toString() == reaction }
        .findFirst()

    return if (optionalReaction.isPresent) optionalReaction.get().emoji else null
}

fun Message.jumpLink(guildId: Long) = "https://discord.com/channels/${guildId}/${channel.id}/${id}"