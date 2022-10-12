package me.abzylicious.rafflebot.commands

import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import me.abzylicious.rafflebot.dataclasses.Messages
import me.abzylicious.rafflebot.embeds.createRaffleListEmbed
import me.abzylicious.rafflebot.services.RaffleService
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.subcommand
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.discordkt.extensions.jumpLink
import me.jakejmattson.discordkt.extensions.pluralize
import me.jakejmattson.discordkt.prompts.promptModal

fun raffleCommands(raffleService: RaffleService, messages: Messages) = subcommand("raffle") {
    sub("start", "Create a new raffle") {
        execute(ChannelArg<GuildMessageChannel>("Channel", "The channel to start the raffle in").optional { it.channel as GuildMessageChannel },
            UnicodeEmojiArg("Reaction", "The emoji used to enter the raffle").optional(Emojis.tada),
            IntegerArg("Winners", "The total number of winners").optional(1))
        {
            val (channel, emoji, winners) = args
            val reaction = emoji.toReaction()

            val (response, input) = promptModal(interaction!!, "Raffle Information") {
                input("Title") {
                    style = TextInputStyle.Short
                    value = "New Raffle!"
                }

                input("Prize") {
                    style = TextInputStyle.Short
                }

                input("Additional info") {
                    required = false
                }
            }

            val (title, prize, description) = input

            val message = channel.createEmbed {
                this.title = title
                this.description = "This is a raffle for $prize. There will be ${winners.pluralize("winner")}.\nReact with ${reaction.name} for your chance to win!"
                this.color = discord.configuration.theme
                if (description != null) {
                    addField("", description)
                }
            }

            raffleService.addRaffle(guild.id, message.id, channel.id, reaction.name, message.jumpLink()!!)
            message.addReaction(reaction)

            response.respond {
                content = "Created raffle in ${channel.mention}"
            }
        }
    }

    sub("end", "End an existing raffle") {
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

            val response = messages.CONGRATULATION + "\n" + winners.joinToString("\n") { winner ->
                "${winner.mention} (${winner.name} :: ${winner.id})"
            }

            respondPublic(response)
            raffleService.removeRaffle(guildId, messageId)
        }
    }

    sub("cancel", "Cancel an existing raffle") {
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

    sub("list", "Lists all active raffles") {
        execute {
            val guildId = guild.id
            val raffles = raffleService.getRaffles(guildId)
            respond { createRaffleListEmbed(discord, raffles) }
        }
    }
}
