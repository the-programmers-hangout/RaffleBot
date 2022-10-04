package me.abzylicious.rafflebot.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import kotlinx.coroutines.flow.toList
import me.abzylicious.rafflebot.extensions.kord.getReaction
import me.abzylicious.rafflebot.persistence.Raffle
import me.abzylicious.rafflebot.persistence.RaffleRepository
import me.abzylicious.rafflebot.utilities.Randomizer
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.toSnowflake

data class Winner(val id: String, val name: String, val mention: String)

@Service
class RaffleService(discord: Discord) {

    private val repository: RaffleRepository = RaffleRepository(discord)
    private val randomizer: Randomizer<User> = Randomizer()

    fun rafflesExist(guildId: Snowflake) = repository.exists(guildId)
    fun raffleExists(guildId: Snowflake, messageId: Snowflake) = repository.exists(guildId, messageId)
    fun getRaffles(guildId: Snowflake) = repository.getAll(guildId)

    fun addRaffle(guildId: Snowflake, messageId: Snowflake, channelId: Snowflake, reaction: String, messageUrl: String) {
        if (!raffleExists(guildId, messageId))
            repository.add(Raffle(guildId, messageId, channelId, reaction, messageUrl))
    }

    fun removeRaffle(guildId: Snowflake, messageId: Snowflake) = repository.remove(guildId, messageId)
    fun clearRaffles(guildId: Snowflake) = repository.clear(guildId)

    suspend fun resolveRaffle(guildId: Snowflake, messageId: Snowflake, winnerCount: Int = 1): List<Winner> {
        if (!raffleExists(guildId, messageId))
            return listOf()

        val raffle = repository.get(guildId, messageId)!!
        val participants = getRaffleParticipants(raffle).filter { it.isBot == null || it.isBot == false }
        return randomizer.selectRandom(participants, winnerCount).map { Winner(it.id.toString(), it.tag, it.mention) }
    }

    private suspend fun getRaffleParticipants(raffle: Raffle): List<User> {
        val channel = raffle.ChannelId.toTextChannel() ?: return emptyList()
        val message = channel.getMessage(raffle.MessageId.toSnowflake())
        val reaction = message.getReaction(raffle.Reaction)
        return if (reaction != null) message.getReactors(reaction).toList() else listOf()
    }
}
