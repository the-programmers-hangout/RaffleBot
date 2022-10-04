package me.abzylicious.rafflebot.commands

import me.jakejmattson.discordkt.commands.commands

fun utilityCommands() = commands("Utility") {
    command("Ping") {
        description = "Check the status of the bot"
        execute {
            respond("Pong! (${discord.kord.gateway.averagePing})")
        }
    }
}
