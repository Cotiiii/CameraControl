package org.cotii.cameracontrolv2.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.nio.file.FileStore;

public class CameraSystem {
    private static CameraSystem instance;
    private final MinecraftClient client = MinecraftClient.getInstance();

    // Estado del sistema de cámara
    private boolean cameraControlActive = false;
    private boolean inputBlocked = false;
    private boolean followingPlayer = false;

    // Configuración actual
    private String targetPlayerName = "";
    private Vec3d targetPosition = Vec3d.ZERO;
    private Vec3d currentCameraPos = Vec3d.ZERO;
    private Vec3d followOffset = Vec3d.ZERO;
    private float targetYaw = 0.0f;
    private float targetPitch = 0.0f;
    private float currentCameraYaw = 0.0f;
    private float currentCameraPitch = 0.0f;
    private float followYawOffset = 0.0f;
    private float followPitchOffset = 0.0f;
    private float speed = 1.0f;
    private boolean smoothMovement = true;

    // Posición original del jugador
    private Vec3d originalPlayerPos = Vec3d.ZERO;
    private float originalPlayerYaw = 0.0f;
    private float originalPlayerPitch = 0.0f;
    private boolean originalPositionSaved = false;

    private CameraSystem() {}

    public static CameraSystem getInstance() {
        if (instance == null) {
            instance = new CameraSystem();
        }
        return instance;
    }

    public void setCameraPosition(double x, double y, double z, float yaw, float pitch, float speed, boolean smooth) {
        if (client.player == null) return;

        saveOriginalPositionIfNeeded();

        this.targetPosition = new Vec3d(x, y, z);
        this.targetYaw = yaw;
        this.targetPitch = pitch;
        this.speed = Math.max(0.1f, speed);
        this.smoothMovement = smooth;
        this.followingPlayer = false;
        this.cameraControlActive = true;
        this.inputBlocked = true;

        if (!smooth) {
            // Movimiento instantáneo
            this.currentCameraPos = targetPosition;
            this.currentCameraYaw = targetYaw;
            this.currentCameraPitch = targetPitch;
        } else {
            // Inicializar posición actual si es la primera vez
            if (this.currentCameraPos.equals(Vec3d.ZERO)) {
                this.currentCameraPos = client.player.getPos();
                this.currentCameraYaw = client.player.getYaw();
                this.currentCameraPitch = client.player.getPitch();
            }
        }
    }

    public void followPlayer(String playerName, double offsetX, double offsetY, double offsetZ,
                             float yawOffset, float pitchOffset, float speed, boolean smooth) {
        if (client.player == null || playerName.trim().isEmpty()) return;

        saveOriginalPositionIfNeeded();

        this.targetPlayerName = playerName;
        this.followOffset = new Vec3d(offsetX, offsetY, offsetZ);
        this.followYawOffset = yawOffset;
        this.followPitchOffset = pitchOffset;
        this.speed = Math.max(0.1f, speed);
        this.smoothMovement = smooth;
        this.followingPlayer = true;
        this.cameraControlActive = true;
        this.inputBlocked = true;

        // Inicializar posición actual si es la primera vez
        if (this.currentCameraPos.equals(Vec3d.ZERO)) {
            this.currentCameraPos = client.player.getPos();
            this.currentCameraYaw = client.player.getYaw();
            this.currentCameraPitch = client.player.getPitch();
        }
    }

    public void resetCamera() {
        if (client.player == null) return;

        this.followingPlayer = false;
        this.cameraControlActive = false;
        this.inputBlocked = false;

        // Restaurar posición original del jugador si fue guardada
        if (originalPositionSaved) {
            client.player.setPosition(originalPlayerPos.x, originalPlayerPos.y, originalPlayerPos.z);
            client.player.setYaw(originalPlayerYaw);
            client.player.setPitch(originalPlayerPitch);
        }

        // Resetear variables de cámara
        this.currentCameraPos = Vec3d.ZERO;
        this.currentCameraYaw = 0.0f;
        this.currentCameraPitch = 0.0f;
        this.originalPositionSaved = false;
    }

    public void tick() {
        if (!cameraControlActive || client.player == null) return;

        if (followingPlayer) {
            tickFollowingCamera();
        } else if (smoothMovement) {
            tickSmoothCamera();
        }

        // Mantener al jugador invisible/inmóvil mientras la cámara está activa
        if (cameraControlActive) {
            // El jugador se mantiene en su posición original pero la cámara se mueve independientemente
            if (originalPositionSaved) {
                client.player.setPosition(originalPlayerPos.x, originalPlayerPos.y, originalPlayerPos.z);
                client.player.setYaw(originalPlayerYaw);
                client.player.setPitch(originalPlayerPitch);
            }
        }
    }

    private void tickFollowingCamera() {
        PlayerEntity targetPlayer = findPlayerByName(targetPlayerName);
        if (targetPlayer == null) {
            // Si el jugador objetivo no existe, resetear la cámara
            resetCamera();
            return;
        }

        Vec3d playerPos = targetPlayer.getPos();
        Vec3d targetPos = playerPos.add(followOffset);
        float targetYaw = targetPlayer.getYaw() + followYawOffset;
        float targetPitch = targetPlayer.getPitch() + followPitchOffset;

        if (smoothMovement) {
            interpolateToTarget(targetPos, targetYaw, targetPitch);
        } else {
            this.currentCameraPos = targetPos;
            this.currentCameraYaw = targetYaw;
            this.currentCameraPitch = targetPitch;
        }
    }

    private void tickSmoothCamera() {
        interpolateToTarget(targetPosition, targetYaw, targetPitch);
    }

    private void interpolateToTarget(Vec3d targetPos, float targetYaw, float targetPitch) {
        // Calcular factor de interpolación
        float lerpFactor = speed * 0.05f; // Ajustado para mejor control
        lerpFactor = MathHelper.clamp(lerpFactor, 0.01f, 1.0f);

        // Interpolar posición
        this.currentCameraPos = currentCameraPos.lerp(targetPos, lerpFactor);

        // Interpolar rotación (manejando el wrap de 360 grados)
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentCameraYaw);
        float pitchDiff = targetPitch - currentCameraPitch;

        this.currentCameraYaw = currentCameraYaw + (yawDiff * lerpFactor);
        this.currentCameraPitch = currentCameraPitch + (pitchDiff * lerpFactor);
        this.currentCameraPitch = MathHelper.clamp(currentCameraPitch, -90.0f, 90.0f);
    }

    private void saveOriginalPositionIfNeeded() {
        if (client.player == null || originalPositionSaved) return;

        this.originalPlayerPos = client.player.getPos();
        this.originalPlayerYaw = client.player.getYaw();
        this.originalPlayerPitch = client.player.getPitch();
        this.originalPositionSaved = true;
    }

    private PlayerEntity findPlayerByName(String name) {
        if (client.world == null || name == null || name.trim().isEmpty()) return null;

        return client.world.getPlayers()
                .stream()
                .filter(player -> player.getName().getString().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    // Getters para el estado del sistema
    public boolean isCameraControlActive() {
        return cameraControlActive;
    }

    public boolean isInputBlocked() {
        return inputBlocked;
    }

    public boolean isFollowingPlayer() {
        return followingPlayer;
    }

    public Vec3d getCurrentCameraPos() {
        return cameraControlActive ? currentCameraPos : null;
    }

    public float getCurrentYaw() {
        return cameraControlActive ? currentCameraYaw : 0;
    }

    public float getCurrentPitch() {
        return cameraControlActive ? currentCameraPitch : 0;
    }

    public boolean isCameraActive() {
        return true;
    }

    public FileStore getCurrentMode() {
        return null;
    }
}