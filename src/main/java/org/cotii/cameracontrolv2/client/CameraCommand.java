package org.cotii.cameracontrolv2.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;


import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class CameraCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register( //no tengo idea de como arreglar esto
                literal("camera")
                        // Comando básico: /camera set x y z yaw pitch speed smooth
                        .then(literal("set")
                                .then(argument("x", DoubleArgumentType.doubleArg())
                                        .then(argument("y", DoubleArgumentType.doubleArg())
                                                .then(argument("z", DoubleArgumentType.doubleArg())
                                                        .then(argument("yaw", FloatArgumentType.floatArg())
                                                                .then(argument("pitch", FloatArgumentType.floatArg())
                                                                        .then(argument("speed", FloatArgumentType.floatArg(0.1f))
                                                                                .then(argument("smooth", BoolArgumentType.bool())
                                                                                        .executes(CameraCommand::setCameraWithAllArgs)
                                                                                )
                                                                                .executes(CameraCommand::setCameraWithoutSmooth)
                                                                        )
                                                                        .executes(CameraCommand::setCameraWithoutSpeed)
                                                                )
                                                                .executes(CameraCommand::setCameraWithoutPitch)
                                                        )
                                                        .executes(CameraCommand::setCameraWithoutYaw)
                                                )
                                        )
                                )
                        )
                        // Comando reset: /camera reset
                        .then(literal("reset")
                                .executes(CameraCommand::resetCamera)
                        )
                        // Comando follow: /camera followplayer <player> x y z yaw pitch speed smooth
                        .then(literal("followplayer")
                                .then(argument("player", StringArgumentType.string())
                                        .then(argument("offsetX", DoubleArgumentType.doubleArg())
                                                .then(argument("offsetY", DoubleArgumentType.doubleArg())
                                                        .then(argument("offsetZ", DoubleArgumentType.doubleArg())
                                                                .then(argument("yaw", FloatArgumentType.floatArg())
                                                                        .then(argument("pitch", FloatArgumentType.floatArg())
                                                                                .then(argument("speed", FloatArgumentType.floatArg(0.1f))
                                                                                        .then(argument("smooth", BoolArgumentType.bool())
                                                                                                .executes(CameraCommand::followPlayerWithAllArgs)
                                                                                        )
                                                                                        .executes(CameraCommand::followPlayerWithoutSmooth)
                                                                                )
                                                                                .executes(CameraCommand::followPlayerWithoutSpeed)
                                                                        )
                                                                        .executes(CameraCommand::followPlayerWithoutPitch)
                                                                )
                                                                .executes(CameraCommand::followPlayerWithoutYaw)
                                                        )
                                                )
                                        )
                                )
                        )
                        // Comando de estado: /camera status
                        .then(literal("status")
                                .executes(CameraCommand::showCameraStatus)
                        )
        );
    }

    // Implementaciones de los comandos SET

    private static int setCameraWithAllArgs(CommandContext<FabricClientCommandSource> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        float yaw = FloatArgumentType.getFloat(context, "yaw");
        float pitch = FloatArgumentType.getFloat(context, "pitch");
        float speed = FloatArgumentType.getFloat(context, "speed");
        boolean smooth = BoolArgumentType.getBool(context, "smooth");

        CameraSystem.getInstance().setCameraPosition(x, y, z, yaw, pitch, speed, smooth);

        context.getSource().sendFeedback(Text.literal(
                String.format("§aCámara movida a: §f%.2f %.2f %.2f §a| Rotación: §f%.1f %.1f §a| Velocidad: §f%.1f §a| Suave: §f%s",
                        x, y, z, yaw, pitch, speed, smooth ? "Sí" : "No")
        ));

        return 1;
    }

    private static int setCameraWithoutSmooth(CommandContext<FabricClientCommandSource> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        float yaw = FloatArgumentType.getFloat(context, "yaw");
        float pitch = FloatArgumentType.getFloat(context, "pitch");
        float speed = FloatArgumentType.getFloat(context, "speed");

        CameraSystem.getInstance().setCameraPosition(x, y, z, yaw, pitch, speed, true);

        context.getSource().sendFeedback(Text.literal(
                String.format("§aCámara movida a: §f%.2f %.2f %.2f §a| Rotación: §f%.1f %.1f §a| Velocidad: §f%.1f §a| Suave: §fSí",
                        x, y, z, yaw, pitch, speed)
        ));

        return 1;
    }

    private static int setCameraWithoutSpeed(CommandContext<FabricClientCommandSource> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        float yaw = FloatArgumentType.getFloat(context, "yaw");
        float pitch = FloatArgumentType.getFloat(context, "pitch");

        CameraSystem.getInstance().setCameraPosition(x, y, z, yaw, pitch, 1.0f, true);

        context.getSource().sendFeedback(Text.literal(
                String.format("§aCámara movida a: §f%.2f %.2f %.2f §a| Rotación: §f%.1f %.1f",
                        x, y, z, yaw, pitch)
        ));

        return 1;
    }

    private static int setCameraWithoutPitch(CommandContext<FabricClientCommandSource> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        float yaw = FloatArgumentType.getFloat(context, "yaw");

        CameraSystem.getInstance().setCameraPosition(x, y, z, yaw, 0.0f, 1.0f, true);

        context.getSource().sendFeedback(Text.literal(
                String.format("§aCámara movida a: §f%.2f %.2f %.2f §a| Yaw: §f%.1f",
                        x, y, z, yaw)
        ));

        return 1;
    }

    private static int setCameraWithoutYaw(CommandContext<FabricClientCommandSource> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");

        CameraSystem.getInstance().setCameraPosition(x, y, z, 0.0f, 0.0f, 1.0f, true);

        context.getSource().sendFeedback(Text.literal(
                String.format("§aCámara movida a: §f%.2f %.2f %.2f", x, y, z)
        ));

        return 1;
    }

    // Comando RESET

    private static int resetCamera(CommandContext<FabricClientCommandSource> context) {
        CameraSystem cameraSystem = CameraSystem.getInstance();

        if (cameraSystem.isCameraActive()) {
            context.getSource().sendError(Text.literal("§cLa cámara ya está en modo normal"));
            return 0;
        }

        cameraSystem.resetCamera();
        context.getSource().sendFeedback(Text.literal("§aCámara reseteada al jugador"));

        return 1;
    }

    // Implementaciones de los comandos FOLLOWPLAYER

    private static int followPlayerWithAllArgs(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        float yaw = FloatArgumentType.getFloat(context, "yaw");
        float pitch = FloatArgumentType.getFloat(context, "pitch");
        float speed = FloatArgumentType.getFloat(context, "speed");
        boolean smooth = BoolArgumentType.getBool(context, "smooth");

        return executeFollowPlayer(context, playerName, offsetX, offsetY, offsetZ, yaw, pitch, speed, smooth);
    }

    private static int followPlayerWithoutSmooth(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        float yaw = FloatArgumentType.getFloat(context, "yaw");
        float pitch = FloatArgumentType.getFloat(context, "pitch");
        float speed = FloatArgumentType.getFloat(context, "speed");

        return executeFollowPlayer(context, playerName, offsetX, offsetY, offsetZ, yaw, pitch, speed, true);
    }

    private static int followPlayerWithoutSpeed(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        float yaw = FloatArgumentType.getFloat(context, "yaw");
        float pitch = FloatArgumentType.getFloat(context, "pitch");

        return executeFollowPlayer(context, playerName, offsetX, offsetY, offsetZ, yaw, pitch, 1.0f, true);
    }

    private static int followPlayerWithoutPitch(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        float yaw = FloatArgumentType.getFloat(context, "yaw");

        return executeFollowPlayer(context, playerName, offsetX, offsetY, offsetZ, yaw, 0.0f, 1.0f, true);
    }

    private static int followPlayerWithoutYaw(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");

        return executeFollowPlayer(context, playerName, offsetX, offsetY, offsetZ, 0.0f, 0.0f, 1.0f, true);
    }

    // Metodo auxiliar para ejecutar follow player
    private static int executeFollowPlayer(CommandContext<FabricClientCommandSource> context, String playerName,
                                           double offsetX, double offsetY, double offsetZ,
                                           float yaw, float pitch, float speed, boolean smooth) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null) {
            context.getSource().sendError(Text.literal("§cNo estás en un mundo"));
            return 0;
        }

        // Buscar el jugador por nombre
        PlayerEntity targetPlayer = null;
        for (PlayerEntity player : client.world.getPlayers()) {
            if (player.getName().getString().equalsIgnoreCase(playerName)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            context.getSource().sendError(Text.literal("§cJugador '" + playerName + "' no encontrado"));
            return 0;
        }

        CameraSystem.getInstance().followPlayer(String.valueOf(targetPlayer), offsetX, offsetY, offsetZ, yaw, pitch, speed, smooth);

        context.getSource().sendFeedback(Text.literal(
                String.format("§aCámara siguiendo a §f%s §acon offset: §f%.2f %.2f %.2f §a| Rotación: §f%.1f %.1f",
                        playerName, offsetX, offsetY, offsetZ, yaw, pitch)
        ));

        return 1;
    }

    // Comando de estado
    private static int showCameraStatus(CommandContext<FabricClientCommandSource> context) {
        CameraSystem cameraSystem = CameraSystem.getInstance();

        if (cameraSystem.isCameraActive()) {
            context.getSource().sendFeedback(Text.literal("§aCámara: §fNormal (controlada por el jugador)"));
        } else {
            String mode = cameraSystem.getCurrentMode().name();
            String following = cameraSystem.isFollowingPlayer() ? "Sí" : "No";

            context.getSource().sendFeedback(Text.literal(
                    String.format("§aCámara: §fActiva | Modo: §f%s §a| Siguiendo jugador: §f%s",
                            mode, following)
            ));
        }

        return 1;
    }

    public static void register() {
    }
}