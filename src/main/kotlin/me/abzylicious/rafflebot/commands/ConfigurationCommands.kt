package me.abzylicious.rafflebot.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import me.abzylicious.rafflebot.dataclasses.Configuration
import me.abzylicious.rafflebot.dataclasses.Messages
import me.abzylicious.rafflebot.conversations.ConfigurationConversation
import me.abzylicious.rafflebot.embeds.createConfigurationEmbed
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit

fun configurationCommands(configuration: Configuration, messages: Messages) = commands("Configuration", Permissions(Permission.ManageGuild)) {
    command("configuration") {
        description = "Show the current guild configuration"
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
