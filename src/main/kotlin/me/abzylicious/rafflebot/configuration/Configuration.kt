package me.abzylicious.rafflebot.configuration

import me.jakejmattson.discordkt.dsl.Data

data class Configuration(
    val ownerId: String = "insert-owner-id",
    val defaultRaffleReaction: String = "\uD83C\uDF89",
    val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()
) : Data() {
    operator fun get(id: Long) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Long) = guildConfigurations.containsKey(guildId)

    fun setup(guildId: Long, adminRoleId: Long, staffRoleId: Long, loggingChannel: Long, defaultRaffleReaction: String) {
        if (guildConfigurations[guildId] != null) return

        val newGuildConfiguration = GuildConfiguration(
            guildId,
            adminRoleId,
            staffRoleId,
            loggingChannel,
            defaultRaffleReaction
        )
        guildConfigurations[guildId] = newGuildConfiguration
        save()
    }
}

data class GuildConfiguration(
    val id: Long,
    var adminRole: Long,
    var staffRole: Long,
    var loggingChannel: Long,
    var defaultRaffleReaction: String = "\uD83C\uDF89"
)