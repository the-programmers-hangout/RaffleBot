package me.abzylicious.rafflebot.commands

import me.abzylicious.rafflebot.configuration.Configuration
import me.abzylicious.rafflebot.configuration.Messages
import me.abzylicious.rafflebot.embeds.createRaffleListEmbed
import me.abzylicious.rafflebot.extensions.discordkt.getEmoteIdOrValue
import me.abzylicious.rafflebot.services.RaffleService
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.jumpLink

fun raffleCommands(configuration: Configuration, raffleService: RaffleService, messages: Messages) = commands("Raffle") {
    command("List") {
        description = "Lists all active raffles"
        execute {
            val guildId = guild.id
            val raffles = raffleService.getRaffles(guildId)
            respond { createRaffleListEmbed(discord, raffles, guildId) }
        }
    }

    command("Convert") {
        description = "Converts a message to a raffle"
        execute(MessageArg, EitherArg(GuildEmojiArg, UnicodeEmojiArg).optionalNullable()) {
            val guildId = guild.id
            val message = args.first
            val messageId = message.id

            if (raffleService.raffleExists(guildId, messageId)) {
                respond(messages.RAFFLE_EXISTS)
                return@execute
            }

            val messageUrl = args.first.jumpLink()
            val channelId = args.first.channelId
            val reaction = args.second?.getEmoteIdOrValue() ?: configuration.defaultRaffleReaction

            raffleService.addRaffle(guildId, messageId, channelId, reaction, messageUrl)
            message.addReaction(reaction)
            respond(messages.MESSAGE_CONVERT_SUCCESS)
        }
    }

    command("End") {
        description = "End a given raffle"
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
