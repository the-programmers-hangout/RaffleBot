package me.abzylicious.rafflebot.dataclasses

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class Configuration(
    val defaultRaffleReaction: String = "\uD83C\uDF89",
    val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()
) : Data() {
    operator fun get(id: Snowflake) = guildConfigurations[id]
    operator fun set(guildId: Snowflake, configuration: GuildConfiguration) {
        guildConfigurations[guildId] = configuration
    }

    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)
}

@Serializable
data class GuildConfiguration(
    var loggingChannel: Snowflake,
    var defaultRaffleReaction: String = "\uD83C\uDF89"
)