package me.abzylicious.rafflebot.services

import me.jakejmattson.discordkt.commands.Command

enum class PermissionLevel {
    Everyone,
    Staff,
    Administrator,
}

val DEFAULT_REQUIRED_PERMISSION = PermissionLevel.Everyone
val commandPermissions: MutableMap<Command, PermissionLevel> = mutableMapOf()

var Command.requiredPermissionLevel: PermissionLevel
    get() = commandPermissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        commandPermissions[this] = value
    }
