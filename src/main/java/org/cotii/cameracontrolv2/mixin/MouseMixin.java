package org.cotii.cameracontrolv2.mixin;

import net.minecraft.client.Mouse;
import org.cotii.cameracontrolv2.client.CameraSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void blockMouseInput(CallbackInfo ci) {
        if (CameraSystem.getInstance().isInputBlocked()) {
            ci.cancel();
        }
    }
}