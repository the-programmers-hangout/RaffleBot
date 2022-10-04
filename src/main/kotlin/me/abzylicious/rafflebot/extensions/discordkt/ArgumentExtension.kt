package me.abzylicious.rafflebot.extensions.discordkt

import dev.kord.core.entity.GuildEmoji
import dev.kord.x.emoji.DiscordEmoji
import me.jakejmattson.discordkt.api.arguments.Either

suspend fun Either<GuildEmoji, DiscordEmoji>.getEmoteIdOrValue() = map({ emote -> emote.id.asString }, { unicodeEmote -> unicodeEmote.unicode })
