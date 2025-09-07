package org.cotii.cameracontrolv2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.cotii.cameracontrolv2.client.CameraCommand;
import org.cotii.cameracontrolv2.client.CameraSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cameracontrolv2 implements ModInitializer {
    public static final String MOD_ID = "assets/cameracontrol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Inicializando Camera Control Mod");
        CommandRegistrationCallback.EVENT.register(((   commandDispatcher, commandRegistryAccess, registrationEnvironment) -> CameraCommand.register()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> CameraSystem.getInstance().tick());
    }
}