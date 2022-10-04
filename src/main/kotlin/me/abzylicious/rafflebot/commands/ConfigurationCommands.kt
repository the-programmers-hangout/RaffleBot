package me.abzylicious.rafflebot.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import me.abzylicious.rafflebot.dataclasses.Configuration
import me.abzylicious.rafflebot.dataclasses.GuildConfiguration
import me.abzylicious.rafflebot.dataclasses.Messages
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.UnicodeEmojiArg
import me.jakejmattson.discordkt.commands.subcommand
import me.jakejmattson.discordkt.dsl.edit

fun configurationCommands(configuration: Configuration, messages: Messages) = subcommand("configure", Permissions(Permission.ManageGuild)) {
    sub("all", "Configure a guild to use this bot") {
        execute(ChannelArg("Channel", "The channel to send logs to"),
            UnicodeEmojiArg("Emoji", "The default raffle reaction")) {
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

    sub("loggingchannel", "Set the bot logging channel") {
        execute(ChannelArg("Channel", "The channel to send logs to")) {
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

    sub("defaultreaction", "Set the default reaction for raffles") {
        execute(UnicodeEmojiArg("Emoji", "The default raffle reaction")) {
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
