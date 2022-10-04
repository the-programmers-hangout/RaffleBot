package me.abzylicious.rafflebot.embeds

import dev.kord.common.kColor
import dev.kord.rest.builder.message.EmbedBuilder
import me.abzylicious.rafflebot.services.BotStatsService
import me.jakejmattson.discordkt.commands.DiscordContext
import me.jakejmattson.discordkt.extensions.addInlineField
import me.jakejmattson.discordkt.extensions.pfpUrl

data class Project(val author: String, val version: String, val discordkt: String, val kotlin: String, val repository: String)

suspend fun EmbedBuilder.createBotInformationEmbed(discordContext: DiscordContext, project: Project) {
    val botStatsService = discordContext.discord.getInjectionObjects(BotStatsService::class)
    val self = discordContext.discord.kord.getSelf()

    color = discordContext.discord.configuration.theme
    thumbnail { url = self.pfpUrl }

    title = self.tag
    description = "A multi-guild discord bot to host all the giveaways you could ever want"

    addInlineField("Author", project.author)
    addInlineField("Source", "[GitHub](${project.repository})")
    addInlineField("Prefix", discordContext.prefix())

    field {
        name = "Build Info"
        value = "```" +
                "Version: ${project.version}\n" +
                "DiscordKt: ${project.discordkt}\n" +
                "Kotlin: ${project.kotlin}\n" +
                "```"
    }

    addInlineField("Uptime", botStatsService.uptime)
    addInlineField("Ping", botStatsService.ping)
}
