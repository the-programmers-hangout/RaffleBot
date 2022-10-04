package me.abzylicious.rafflebot.conversations

import dev.kord.common.entity.Snowflake
import me.abzylicious.rafflebot.dataclasses.Configuration
import me.abzylicious.rafflebot.dataclasses.Messages
import me.abzylicious.rafflebot.embeds.createConfigurationMessageEmbed
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.conversations.conversation

class ConfigurationConversation(private val configuration: Configuration, private val messages: Messages) {
    suspend fun createConfigurationConversation(guildId: Snowflake) = conversation {
        val adminRole = prompt(RoleArg) {
            createConfigurationMessageEmbed(discord, "Setup - Admin Role", messages.SETUP_ADMIN_ROLE)
        }

        val staffRole = prompt(RoleArg) {
            createConfigurationMessageEmbed(discord, "Setup - Staff Role", messages.SETUP_STAFF_ROLE)
        }

        val loggingChannel = prompt(ChannelArg) {
            createConfigurationMessageEmbed(discord, "Setup - Logging Channel", messages.SETUP_LOGGING_CHANNEL)
        }

        val setDefaultRaffleReaction = prompt(BooleanArg) {
            createConfigurationMessageEmbed(discord, "Setup - Default Raffle Reaction", messages.SETUP_DEFAULT_RAFFLE_REACTION_DECISION)
        }

        val defaultRaffleReaction = if (setDefaultRaffleReaction) {
            prompt(UnicodeEmojiArg) {
                createConfigurationMessageEmbed(discord, "Setup - Default Raffle Reaction", messages.SETUP_DEFAULT_RAFFLE_REACTION)
            }.unicode
        } else {
            configuration.defaultRaffleReaction
        }

        configuration.setup(guildId, adminRole.id, staffRole.id, loggingChannel.id, defaultRaffleReaction)
    }
}
