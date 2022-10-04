package me.abzylicious.rafflebot.configuration

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.edit

@Serializable
data class Configuration(
    val defaultRaffleReaction: String = "\uD83C\uDF89",
    val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()
) : Data() {
    operator fun get(id: Snowflake) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)

    fun setup(guildId: Snowflake, adminRoleId: Snowflake, staffRoleId: Snowflake, loggingChannel: Snowflake, defaultRaffleReaction: String) {
        if (guildConfigurations[guildId] != null) return

        val newGuildConfiguration = GuildConfiguration(adminRoleId, staffRoleId, loggingChannel, defaultRaffleReaction)

        edit { guildConfigurations[guildId] = newGuildConfiguration }
    }
}

@Serializable
data class GuildConfiguration(
    var adminRole: Snowflake,
    var staffRole: Snowflake,
    var loggingChannel: Snowflake,
    var defaultRaffleReaction: String = "\uD83C\uDF89"
)