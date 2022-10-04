package me.abzylicious.rafflebot.preconditions

import me.abzylicious.rafflebot.dataclasses.Configuration
import me.abzylicious.rafflebot.services.LoggingService
import me.jakejmattson.discordkt.dsl.precondition

fun commandLogger(loggingService: LoggingService, configuration: Configuration) = precondition {
    command ?: return@precondition fail()

    val guildId = guild?.id ?: return@precondition
    val loggingChannelId = configuration.guildConfigurations[guildId]?.loggingChannel ?: return@precondition
    val commandName = command!!.names.first().toLowerCase()
    loggingService.log(loggingChannelId, "${author.tag} :: ${author.id} invoked **$commandName** in ${channel.mention}")
    return@precondition
}
