package me.abzylicious.rafflebot.commands

import me.abzylicious.rafflebot.configuration.Configuration
import me.abzylicious.rafflebot.configuration.Messages
import me.abzylicious.rafflebot.conversations.ConfigurationConversation
import me.abzylicious.rafflebot.embeds.createConfigurationEmbed
import me.abzylicious.rafflebot.extensions.discordkt.getEmoteIdOrValue
import me.abzylicious.rafflebot.extensions.stdlib.toDisplayableEmote
import me.abzylicious.rafflebot.services.PermissionLevel
import me.abzylicious.rafflebot.services.requiredPermissionLevel
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit

fun configurationCommands(configuration: Configuration, messages: Messages) = commands("Configuration") {
    command("configuration") {
        description = "Show the current guild configuration"
        requiredPermissionLevel = PermissionLevel.Staff
        execute {
            val guildId = guild.id
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val guildConfiguration = configuration[guildId]!!
            respond { createConfigurationEmbed(discord, guild, guildConfiguration) }
        }
    }

    command("configure") {
        description = "Configure a guild to use this bot"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute {
            val guildId = guild.id
            if (configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_EXISTS)
                return@execute
            }

            ConfigurationConversation(configuration, messages)
                .createConfigurationConversation(guildId)
                .startPublicly(discord, author, channel)

            respond("**${guild.name}** ${messages.SETUP_COMPLETE}")
        }
    }

    command("setadminrole") {
        description = "Set the bot admin role"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(RoleArg) {
            val guildId = guild.id
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val role = args.first
            configuration.edit { this[guildId]?.adminRole = role.id }
            respond("Role set to: **${role.name}**")
        }
    }

    command("setstaffrole") {
        description = "Set the bot staff role"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(RoleArg) {
            val guildId = guild.id
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val role = args.first
            configuration.edit { this[guild.id]?.staffRole = role.id }
            respond("Role set to: **${role.name}**")
        }
    }

    command("setloggingchannel") {
        description = "Set the bot logging channel"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(ChannelArg) {
            val guildId = guild.id
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val channel = args.first
            configuration.edit { this[guild.id]?.loggingChannel = channel.id }
            respond("Channel set to: **${channel.name}**")
        }
    }

    command("setdefaultreaction") {
        description = "Set the default reaction for raffles"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(EitherArg(GuildEmojiArg, UnicodeEmojiArg)) {
            val guildId = guild.id
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val reaction = args.first.getEmoteIdOrValue()
            configuration.edit { this[guild.id]?.defaultRaffleReaction = reaction }
            respond("Reaction set to: ${reaction.toDisplayableEmote(guildId)}")
        }
    }
}
