package me.abzylicious.rafflebot.extensions.stdlib

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.flow.first
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.toSnowflake

private lateinit var api: Kord
private val emojiRegex = "[^\\x00-\\x7F]+ *(?:[^\\x00-\\x7F]| )*".toRegex()

@Service
class ApiInitializer(discord: Discord) { init { api = discord.kord } }

suspend fun Long.toTextChannel() = try { api.getChannelOf<TextChannel>(toSnowflake()) } catch (e: Exception) { null }
suspend fun Long.isValidChannelId() = this.toTextChannel() != null

suspend fun String.toGuildEmote(guildId: Snowflake) = try { api.guilds.first { it.id == guildId }.getEmoji(toSnowflake()) } catch (e: Exception) { null }
suspend fun String.isGuildEmote(guildId: Snowflake) = this.toGuildEmote(guildId) != null

fun String.isEmoji() = this.matches(emojiRegex)
suspend fun String.toDisplayableEmote(guildId: Snowflake) = if (isGuildEmote(guildId)) toGuildEmote(guildId)!!.mention else this