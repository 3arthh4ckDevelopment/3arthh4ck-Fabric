package me.earth.earthhack.impl.event.events.client;

import net.minecraft.client.MinecraftClient;

/**
 * Fired when {@link MinecraftClient#onInitFinished(MinecraftClient.LoadingContext)} is called.
 * Lets the client know when {@link net.minecraft.client.option.GameOptions} is accessible, for example.
 * Could probably be done in a better way (where this event is redundant)
 * but this is relatively simple and clean.
 */
public class ClientInitEvent { }
