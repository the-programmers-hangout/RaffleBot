package me.abzylicious.rafflebot.persistence

import dev.kord.common.entity.Snowflake
import me.jakejmattson.discordkt.Discord

class RaffleRepository(private val discord: Discord) {

    private val raffleEntries = loadRaffles()

    private fun loadRaffles() = discord.getInjectionObjects(RaffleEntries::class)
    private fun saveRaffles() = raffleEntries.save()

    fun getAll(guildId: Snowflake) = raffleEntries.raffles.filter { it.GuildId == guildId }
    fun get(guildId: Snowflake, messageId: Snowflake) = raffleEntries.raffles.find { it.GuildId == guildId && it.MessageId == messageId }

    fun add(raffle: Raffle) {
        raffleEntries.raffles.add(raffle)
        saveRaffles()
    }

    fun remove(guildId: Snowflake, messageId: Snowflake) {
        if (raffleEntries.raffles.removeIf { it.GuildId == guildId && it.MessageId == messageId })
            saveRaffles()
    }

    fun clear(guildId: Snowflake) {
        if (raffleEntries.raffles.removeIf {it.GuildId == guildId })
            saveRaffles()
    }

    fun exists(guildId: Snowflake) = raffleEntries.raffles.any { it.GuildId == guildId }
    fun exists(guildId: Snowflake, messageId: Snowflake) = raffleEntries.raffles.any { it.GuildId == guildId && it.MessageId == messageId }
}
