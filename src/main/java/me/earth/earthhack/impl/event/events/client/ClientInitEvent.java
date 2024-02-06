package me.earth.earthhack.impl.event.events.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.resource.ResourceReload;

/**
 * Fired when {@link MinecraftClient#onInitFinished(RealmsClient, ResourceReload, RunArgs.QuickPlay)} is called.
 * Lets the client know when {@link net.minecraft.client.option.GameOptions} is accessible, for example.
 * Could probably be done in a better way (where this event is redundant)
 * but this is relatively simple and clean.
 */
public class ClientInitEvent { }
