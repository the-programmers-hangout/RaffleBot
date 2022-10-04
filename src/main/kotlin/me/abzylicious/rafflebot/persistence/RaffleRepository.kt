package me.abzylicious.rafflebot.persistence

import dev.kord.common.entity.Snowflake
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.dsl.edit

class RaffleRepository(private val discord: Discord) {

    private val raffleEntries = loadRaffles()

    private fun loadRaffles() = discord.getInjectionObjects(RaffleEntries::class)

    fun getAll(guildId: Snowflake) = raffleEntries.raffles.filter { it.GuildId == guildId }
    fun get(guildId: Snowflake, messageId: Snowflake) = raffleEntries.raffles.find { it.GuildId == guildId && it.MessageId == messageId }

    fun add(raffle: Raffle) {
        raffleEntries.edit { raffles.add(raffle) }
    }

    fun remove(guildId: Snowflake, messageId: Snowflake) {
        raffleEntries.edit {
            raffles.removeIf { it.GuildId == guildId && it.MessageId == messageId }
        }
    }

    fun clear(guildId: Snowflake) {
        raffleEntries.edit { raffles.removeIf { it.GuildId == guildId } }
    }

    fun exists(guildId: Snowflake) = raffleEntries.raffles.any { it.GuildId == guildId }
    fun exists(guildId: Snowflake, messageId: Snowflake) = raffleEntries.raffles.any { it.GuildId == guildId && it.MessageId == messageId }
}
