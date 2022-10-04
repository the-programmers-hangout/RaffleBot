package me.abzylicious.rafflebot.conversations

import me.abzylicious.rafflebot.configuration.Configuration
import me.abzylicious.rafflebot.configuration.Messages
import me.abzylicious.rafflebot.embeds.createConfigurationMessageEmbed
import me.abzylicious.rafflebot.extensions.discordkt.getEmoteIdOrValue
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.conversations.conversation

class ConfigurationConversation(private val configuration: Configuration, private val messages: Messages) {
    suspend fun createConfigurationConversation(guildId: Long) = conversation {
        val setPrefix = prompt(BooleanArg) {
            createConfigurationMessageEmbed(discord, "Setup - Prefix", messages.SETUP_PREFIX_DECISION)
        }

        val prefix = if (setPrefix) {
            prompt(EveryArg) { createConfigurationMessageEmbed(discord, "Setup - Prefix", messages.SETUP_PREFIX) }
        } else {
            configuration.prefix
        }

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
            prompt(EitherArg(GuildEmojiArg, UnicodeEmojiArg)) {
                createConfigurationMessageEmbed(discord, "Setup - Default Raffle Reaction", messages.SETUP_DEFAULT_RAFFLE_REACTION)
            }.getEmoteIdOrValue()
        } else {
            configuration.defaultRaffleReaction
        }

        configuration.setup(guildId, prefix, adminRole.id.value, staffRole.id.value, loggingChannel.id.value, defaultRaffleReaction)
    }
}
