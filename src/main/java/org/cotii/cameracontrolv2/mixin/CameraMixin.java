package org.cotii.cameracontrolv2.mixin;

import org.cotii.cameracontrolv2.client.CameraSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "update", at = @At("HEAD"))
    private void onCameraUpdate(CallbackInfo ci) {

    }
    @Shadow private Vec3d pos;
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Inject(method = "update", at = @At("TAIL"))
    private void updateCamera(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        CameraSystem cameraSystem = CameraSystem.getInstance();

        if (cameraSystem.isCameraControlActive()) {
            cameraSystem.tick();

            Vec3d newPos = cameraSystem.getCurrentCameraPos();
            if (newPos != null) {
                this.pos = newPos;
                this.yaw = cameraSystem.getCurrentYaw();
                this.pitch = cameraSystem.getCurrentPitch();
            }
        }
    }
}