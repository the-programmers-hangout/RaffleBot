package me.abzylicious.rafflebot.commands

import me.abzylicious.rafflebot.configuration.Configuration
import me.abzylicious.rafflebot.configuration.Messages
import me.abzylicious.rafflebot.embeds.createRaffleListEmbed
import me.abzylicious.rafflebot.extensions.discordkt.getEmoteIdOrValue
import me.abzylicious.rafflebot.extensions.kord.addReaction
import me.abzylicious.rafflebot.extensions.kord.jumpLink
import me.abzylicious.rafflebot.services.PermissionLevel
import me.abzylicious.rafflebot.services.RaffleService
import me.abzylicious.rafflebot.services.requiredPermissionLevel
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands

fun raffleCommands(configuration: Configuration, raffleService: RaffleService, messages: Messages) = commands("Raffle") {
    command("List") {
        description = "Lists all active raffles"
        requiredPermissionLevel = PermissionLevel.Staff
        execute {
            val guildId = guild.id
            val raffles = raffleService.getRaffles(guildId)
            respond { createRaffleListEmbed(discord, raffles, guildId) }
        }
    }

    command("Convert") {
        description = "Converts a message to a raffle"
        requiredPermissionLevel = PermissionLevel.Staff
        execute(MessageArg, EitherArg(GuildEmojiArg, UnicodeEmojiArg).optionalNullable()) {
            val guildId = guild.id
            val messageId = args.first.id

            if (raffleService.raffleExists(guildId, messageId)) {
                respond(messages.RAFFLE_EXISTS)
                return@execute
            }

            val messageUrl = args.first.jumpLink(guildId.value.toLong())
            val channelId = args.first.channelId
            val reaction = args.second?.getEmoteIdOrValue() ?: configuration.defaultRaffleReaction

            raffleService.addRaffle(guildId, messageId, channelId, reaction, messageUrl)
            channel.addReaction(guildId, messageId, reaction)
            respond(messages.MESSAGE_CONVERT_SUCCESS)
        }
    }

    command("End") {
        description = "End a given raffle"
        requiredPermissionLevel = PermissionLevel.Staff
        execute(MessageArg, IntegerArg.optional(1)) {
            val guildId = guild.id
            val messageId = args.first.id
            val winnerCount = args.second

            if (!raffleService.raffleExists(guildId, messageId)) {
                respond(messages.RAFFLE_NOT_FOUND)
                return@execute
            }

            val winners = raffleService.resolveRaffle(guildId, messageId, winnerCount)
            if (winners.isEmpty()) {
                respond(messages.NO_WINNER_AVAILABLE)
                return@execute
            }

            respond(messages.CONGRATULATION)
            for (winner in winners) {
                respond("${winner.mention} (${winner.name} :: ${winner.id})")
            }

            raffleService.removeRaffle(guildId, messageId)
        }
    }

    command("Remove") {
        requiredPermissionLevel = PermissionLevel.Staff
        description = "Remove a given raffle"
        execute(MessageArg) {
            val guildId = guild.id
            val messageId = args.first.id

            if (!raffleService.raffleExists(guildId, messageId)) {
                respond(messages.RAFFLE_NOT_FOUND)
                return@execute
            }

            raffleService.removeRaffle(guildId, messageId)
            respond(messages.RAFFLE_REMOVED)
        }
    }

    command("Clear") {
        requiredPermissionLevel = PermissionLevel.Staff
        description = "Remove all raffles"
        execute {
            val guildId = guild.id

            if (!raffleService.rafflesExist(guildId)) {
                respond(messages.NO_RAFFLES_AVAILABLE)
                return@execute
            }

            raffleService.clearRaffles(guildId)
            respond(messages.RAFFLES_CLEARED)
        }
    }
}
