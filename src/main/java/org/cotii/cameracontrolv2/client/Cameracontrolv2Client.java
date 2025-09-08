package org.cotii.cameracontrolv2.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import static com.mojang.text2speech.Narrator.LOGGER;

public class Cameracontrolv2Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LOGGER.info("Inicializando Camera Control Mod");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            CameraCommand.register(dispatcher);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> CameraSystem.getInstance().tick());
    }
}
