package me.abzylicious.rafflebot.persistence

import dev.kord.common.entity.Snowflake
import me.jakejmattson.discordkt.dsl.Data

data class Raffle (
    val GuildId: Snowflake,
    val MessageId: Snowflake,
    val ChannelId: Snowflake,
    val Reaction: String,
    val MessageUrl: String,
)

data class RaffleEntries(val raffles: MutableList<Raffle> = mutableListOf()) : Data("data/raffles.json", false)