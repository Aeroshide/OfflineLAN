package com.aerohide.offlinelan.mixins;

import com.aerohide.offlinelan.Offlinelan;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.MultiplayerOptionsScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiplayerOptionsScreen.class)
public abstract class OpenToLanScreenMixin extends Screen {

    @Shadow protected abstract void updateApplyChangesActiveState();

    @Shadow private MinecraftServer.MultiplayerScope wantedMultiplayerScope;

    @Shadow private void sendPublishMessage(Component message) {}

    @Unique
    private boolean initialOnlineMode;

    @Unique
    private LinearLayout capturedContentLayout;

    protected OpenToLanScreenMixin(Component title) {
        super(title);
    }


    @ModifyVariable(method = "init", at = @At("STORE"), ordinal = 0)
    private LinearLayout captureContentLayout(LinearLayout content) {
        this.capturedContentLayout = content;
        return content;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;visitWidgets(Ljava/util/function/Consumer;)V"))
    private void addOfflineModeToggleToLayout(CallbackInfo ci) {
        this.initialOnlineMode = Offlinelan.onlineModeOption.getOnlineMode();

        if (this.capturedContentLayout != null) {
            this.capturedContentLayout.addChild(
                    CycleButton.onOffBuilder(Offlinelan.onlineModeOption.getOnlineMode())
                            .create(
                                    Component.translatable("offlinelan.toggleText"),
                                    (button, value) -> {
                                        Offlinelan.onlineModeOption.setOnlineMode(value);
                                        this.updateApplyChangesActiveState();
                                    }
                            )
            );
        }
    }

    @Inject(method = "hasSettingsChanges", at = @At("RETURN"), cancellable = true)
    private void checkOfflineModeChanges(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (Offlinelan.onlineModeOption.getOnlineMode() != this.initialOnlineMode) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "lanPortChanged", at = @At("RETURN"), cancellable = true)
    private void forceRepublishOnOfflineModeChange(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN) {
            if (Offlinelan.onlineModeOption.getOnlineMode() != this.initialOnlineMode) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "publish",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/MultiplayerOptionsScreen;sendPublishMessage(Lnet/minecraft/network/chat/Component;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void appendOfflineLanMessage(IntegratedServer singleplayerServer, MinecraftServer.MultiplayerScope scope, CallbackInfo ci) {
        if (!Offlinelan.onlineModeOption.getOnlineMode()) {
            this.sendPublishMessage(Component.translatable("offlinelan.chatMessage"));
        }
    }
}