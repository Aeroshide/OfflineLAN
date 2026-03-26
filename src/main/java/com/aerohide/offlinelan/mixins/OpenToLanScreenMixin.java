package com.aerohide.offlinelan.mixins;

import com.aerohide.offlinelan.Offlinelan;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShareToLanScreen.class)
public abstract class OpenToLanScreenMixin extends Screen {
    protected OpenToLanScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addOfflineModeToggle(CallbackInfo ci) {
        Offlinelan.onlineMode = true;
        this.addRenderableWidget(
                CycleButton.onOffBuilder(Offlinelan.onlineMode)
                        .create(
                                this.width / 2 - 75,
                                190,
                                150,
                                20,
                                Component.translatable("offlinelan.toggleText"),
                                (button, value) -> Offlinelan.onlineMode = value
                        )
        );
    }
}