package me.abzylicious.rafflebot.commands

import dev.kord.x.emoji.toReaction
import me.abzylicious.rafflebot.dataclasses.Messages
import me.abzylicious.rafflebot.embeds.createRaffleListEmbed
import me.abzylicious.rafflebot.services.RaffleService
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.arguments.MessageArg
import me.jakejmattson.discordkt.arguments.UnicodeEmojiArg
import me.jakejmattson.discordkt.commands.subcommand
import me.jakejmattson.discordkt.extensions.jumpLink

fun raffleCommands(raffleService: RaffleService, messages: Messages) = subcommand("raffle") {
    sub("Convert", "Converts a message to a raffle") {
        execute(MessageArg("Message", "The message to convert to a raffle"),
            UnicodeEmojiArg("Emoji", "The emoji used to enter the raffle")) {
            val guildId = guild.id
            val message = args.first
            val messageId = message.id

            if (raffleService.raffleExists(guildId, messageId)) {
                respond(messages.RAFFLE_EXISTS)
                return@execute
            }

            val messageUrl = args.first.jumpLink()
            val channelId = args.first.channelId
            val reaction = args.second.toReaction()

            raffleService.addRaffle(guildId, messageId, channelId, reaction.name, messageUrl!!)
            message.addReaction(reaction)
            respond(messages.MESSAGE_CONVERT_SUCCESS)
        }
    }

    sub("End", "End a given raffle") {
        execute(MessageArg("Message", "The raffle message ID"),
            IntegerArg("Winners", "The number of winners").optional(1)) {
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

    sub("Cancel", "Cancel a given raffle") {
        execute(MessageArg("Message", "The raffle message ID")) {
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

    sub("List", "Lists all active raffles") {
        execute {
            val guildId = guild.id
            val raffles = raffleService.getRaffles(guildId)
            respond { createRaffleListEmbed(discord, raffles) }
        }
    }
}
