package me.abzylicious.rafflebot.embeds

import dev.kord.core.entity.Guild
import dev.kord.rest.builder.message.EmbedBuilder
import me.abzylicious.rafflebot.configuration.GuildConfiguration
import me.abzylicious.rafflebot.extensions.stdlib.toDisplayableEmote
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.extensions.*

suspend fun EmbedBuilder.createConfigurationMessageEmbed(discord: Discord, title: String, description: String) {
    color = discord.configuration.theme
    thumbnail(discord.kord.getSelf().pfpUrl)
    this.title = title
    this.description = description
}

suspend fun EmbedBuilder.createConfigurationEmbed(discord: Discord, guild: Guild, guildConfiguration: GuildConfiguration) {
    color = discord.configuration.theme
    title = "${discord.kord.getGuild(guildConfiguration.id.toSnowflake())?.name} - Configuration"
    thumbnail(discord.kord.getSelf().pfpUrl)
    addField("Admin Role", guild.getRole(guildConfiguration.adminRole.toSnowflake()).mention)
    addField("Staff Role", guild.getRole(guildConfiguration.staffRole.toSnowflake()).mention)
    addField("Logging Channel", guild.getChannel(guildConfiguration.loggingChannel.toSnowflake()).mention)
    addField("Default Raffle Reaction", guildConfiguration.defaultRaffleReaction.toDisplayableEmote(guildConfiguration.id))
}
