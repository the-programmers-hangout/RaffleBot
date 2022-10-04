package me.abzylicious.rafflebot

import com.google.gson.Gson
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import me.abzylicious.rafflebot.configuration.Configuration
import me.abzylicious.rafflebot.configuration.Messages
import me.abzylicious.rafflebot.embeds.Project
import me.abzylicious.rafflebot.embeds.createBotInformationEmbed
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

    val botOwnerId = System.getenv("BOT_OWNER") ?: "none"
    Configuration(ownerId = botOwnerId).save()

    bot(token) {
        val configuration = data("config/config.json") { Configuration() }

        prefix {
            guild?.let { configuration[it.id.value.toLong()]?.prefix } ?: configuration.prefix
        }

        configure {
            theme = Color.CYAN
            intents = Intents(Intent.GuildMessages)
            defaultPermissions = Permissions(Permission.ManageMessages)
        }

        mentionEmbed {
            val propertyFile = Project::class.java.getResource("/properties.json").readText()
            val project = Gson().fromJson(propertyFile, Project::class.java)
            createBotInformationEmbed(it, project)
        }

        onStart {
            val logger = this.getInjectionObjects(LoggingService::class)
            configuration.guildConfigurations.forEach { logger.log(it.value.loggingChannel, messages.STARTUP_LOG) }
        }
    }
}
