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

fun configurationCommands(configuration: Configuration, messages: Messages) = commands("Configuration") {
    command("configuration") {
        description = "Show the current guild configuration"
        requiredPermissionLevel = PermissionLevel.Staff
        execute {
            val guildId = guild.id.value
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
            val guildId = guild.id.value
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

    command("setprefix") {
        description = "Set the bot prefix"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(EveryArg) {
            val guildId = guild.id.value
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val prefix = args.first
            configuration[guildId]?.prefix = prefix
            configuration.save()
            respond("Prefix set to **$prefix**")
        }
    }

    command("setadminrole") {
        description = "Set the bot admin role"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(RoleArg) {
            val guildId = guild.id.value
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val role = args.first
            configuration[guildId]?.adminRole = role.id.value
            configuration.save()
            respond("Role set to: **${role.name}**")
        }
    }

    command("setstaffrole") {
        description = "Set the bot staff role"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(RoleArg) {
            val guildId = guild.id.value
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val role = args.first
            configuration[guild.id.value]?.staffRole = role.id.value
            configuration.save()
            respond("Role set to: **${role.name}**")
        }
    }

    command("setloggingchannel") {
        description = "Set the bot logging channel"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(ChannelArg) {
            val guildId = guild.id.value
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val channel = args.first
            configuration[guild.id.value]?.loggingChannel = channel.id.value
            configuration.save()
            respond("Channel set to: **${channel.name}**")
        }
    }

    command("setdefaultreaction") {
        description = "Set the default reaction for raffles"
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(EitherArg(GuildEmojiArg, UnicodeEmojiArg)) {
            val guildId = guild.id.value
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val reaction = args.first.getEmoteIdOrValue()
            configuration[guild.id.value]?.defaultRaffleReaction = reaction
            configuration.save()
            respond("Reaction set to: ${reaction.toDisplayableEmote(guildId)}")
        }
    }
}
