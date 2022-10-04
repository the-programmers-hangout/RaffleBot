package me.abzylicious.rafflebot.persistence

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class Raffle (
    val GuildId: Snowflake,
    val MessageId: Snowflake,
    val ChannelId: Snowflake,
    val Reaction: String,
    val MessageUrl: String,
)

@Serializable
data class RaffleEntries(val raffles: MutableList<Raffle> = mutableListOf()) : Data()