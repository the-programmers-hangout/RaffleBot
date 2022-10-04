package me.abzylicious.rafflebot.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import me.abzylicious.rafflebot.dataclasses.Configuration
import me.abzylicious.rafflebot.dataclasses.GuildConfiguration
import me.abzylicious.rafflebot.dataclasses.Messages
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.UnicodeEmojiArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit

fun configurationCommands(configuration: Configuration, messages: Messages) = commands("Configuration", Permissions(Permission.ManageGuild)) {
    command("configure") {
        description = "Configure a guild to use this bot"
        execute(ChannelArg, UnicodeEmojiArg) {
            val guildId = guild.id
            val (channel, reaction) = args

            if (configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_EXISTS)
                return@execute
            }

            configuration.edit {
                this[guildId] = GuildConfiguration(channel.id, reaction.unicode)
            }

            respond("**${guild.name}** ${messages.SETUP_COMPLETE}")
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
        execute(UnicodeEmojiArg) {
            val guildId = guild.id
            if (!configuration.hasGuildConfig(guildId)) {
                respond(messages.GUILD_CONFIGURATION_NOT_FOUND)
                return@execute
            }

            val reaction = args.first.unicode
            configuration.edit { this[guild.id]?.defaultRaffleReaction = reaction }
            respond("Reaction set to: $reaction")
        }
    }
}
