package me.abzylicious.rafflebot.dataclasses

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class Raffle (
    val guildId: Snowflake,
    val messageId: Snowflake,
    val channelId: Snowflake,
    val reaction: String,
    val messageUrl: String,
)

@Serializable
data class RaffleEntries(val raffles: MutableList<Raffle> = mutableListOf()) : Data()