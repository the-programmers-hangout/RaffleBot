package me.abzylicious.rafflebot.embeds

import dev.kord.rest.builder.message.EmbedBuilder
import me.abzylicious.rafflebot.dataclasses.Raffle
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.extensions.addInlineField
import me.jakejmattson.discordkt.extensions.pfpUrl
import me.jakejmattson.discordkt.extensions.thumbnail

suspend fun EmbedBuilder.createRaffleListEmbed(discord: Discord, raffles: List<Raffle>) {
    color = discord.configuration.theme
    thumbnail(discord.kord.getSelf().pfpUrl)
    title = "Raffles"
    description = if (raffles.isNotEmpty()) { "Currently active raffles" } else { "There are no active raffles currently" }

    if (raffles.isEmpty())
        return

    for (raffle in raffles) {
        addInlineField("Raffle Id (MessageId)", raffle.messageId.toString())
        addInlineField("Message", "[Jump to](${raffle.messageUrl})")
        addInlineField("Reaction", raffle.reaction)
    }
}
