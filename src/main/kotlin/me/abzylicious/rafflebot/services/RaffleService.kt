package me.abzylicious.rafflebot.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import kotlinx.coroutines.flow.toList
import me.abzylicious.rafflebot.dataclasses.Raffle
import me.abzylicious.rafflebot.dataclasses.RaffleEntries
import me.abzylicious.rafflebot.extensions.kord.getReaction
import me.abzylicious.rafflebot.utilities.Randomizer
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.dsl.edit
import me.jakejmattson.discordkt.extensions.toSnowflake

data class Winner(val id: String, val name: String, val mention: String)

@Service
class RaffleService(private val raffleEntries: RaffleEntries) {
    private val randomizer: Randomizer<User> = Randomizer()

    fun rafflesExist(guildId: Snowflake) = raffleEntries.raffles.any { it.guildId == guildId }
    fun raffleExists(guildId: Snowflake, messageId: Snowflake) = raffleEntries.raffles.any { it.guildId == guildId && it.messageId == messageId }
    fun getRaffles(guildId: Snowflake) = raffleEntries.raffles.filter { it.guildId == guildId }
    private fun getRaffle(guildId: Snowflake, messageId: Snowflake) = raffleEntries.raffles.find { it.guildId == guildId && it.messageId == messageId }

    fun addRaffle(guildId: Snowflake, messageId: Snowflake, channelId: Snowflake, reaction: String, messageUrl: String) {
        if (!raffleExists(guildId, messageId))
            raffleEntries.edit { raffles.add(Raffle(guildId, messageId, channelId, reaction, messageUrl)) }
    }

    fun removeRaffle(guildId: Snowflake, messageId: Snowflake) = raffleEntries.edit {
        raffles.removeIf { it.guildId == guildId && it.messageId == messageId }
    }

    fun clearRaffles(guildId: Snowflake) = raffleEntries.edit { raffles.removeIf { it.guildId == guildId } }

    suspend fun resolveRaffle(guildId: Snowflake, messageId: Snowflake, winnerCount: Int = 1): List<Winner> {
        if (!raffleExists(guildId, messageId))
            return listOf()

        val raffle = getRaffle(guildId, messageId)!!
        val participants = getRaffleParticipants(raffle).filter { !it.isBot }
        return randomizer.selectRandom(participants, winnerCount).map { Winner(it.id.toString(), it.tag, it.mention) }
    }

    private suspend fun getRaffleParticipants(raffle: Raffle): List<User> {
        val channel = raffle.channelId.toTextChannel() ?: return emptyList()
        val message = channel.getMessage(raffle.messageId.toSnowflake())
        val reaction = message.getReaction(raffle.reaction)
        return if (reaction != null) message.getReactors(reaction).toList() else listOf()
    }
}