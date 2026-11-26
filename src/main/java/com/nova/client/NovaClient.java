package com.nova.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.nova.client.gui.NovaScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class NovaClient implements ClientModInitializer {
    private static KeyMapping openGui;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("nova", "main"));
        openGui = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nova.open_gui", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.consumeClick()) {
                if (client.screen == null) client.setScreen(new NovaScreen());
            }
        });
    }
}

