package me.abzylicious.rafflebot

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import me.abzylicious.rafflebot.dataclasses.Configuration
import me.abzylicious.rafflebot.dataclasses.Messages
import me.abzylicious.rafflebot.dataclasses.RaffleEntries
import me.abzylicious.rafflebot.services.LoggingService
import me.jakejmattson.discordkt.dsl.bot
import java.awt.Color

@KordPreview
suspend fun main(args: Array<String>) {
    val messages = Messages()
    val token = args.firstOrNull()
        ?: System.getenv("BOT_TOKEN")
        ?: null

    require(token != null) { messages.NO_TOKEN_PROVIDED }

    bot(token) {
        val configuration = data("config/config.json") { Configuration() }
        data("data/raffles.json") { RaffleEntries() }
        data("config/messages.json") { Messages() }

        prefix { "/" }

        configure {
            recommendCommands = false
            dualRegistry = false
            theme = Color.CYAN
            intents = Intents(Intent.GuildMessages)
            defaultPermissions = Permissions(Permission.ManageMessages)
        }

        onStart {
            val logger = this.getInjectionObjects(LoggingService::class)
            configuration.guildConfigurations.forEach { logger.log(it.value.loggingChannel, messages.STARTUP_LOG) }
        }
    }
}
