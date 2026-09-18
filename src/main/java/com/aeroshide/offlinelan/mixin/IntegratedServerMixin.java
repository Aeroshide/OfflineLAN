package com.aeroshide.offlinelan.mixin;

import com.aeroshide.offlinelan.OfflineLAN;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
//? if >=26.2 {
    @Inject(method = "publishServer(Lnet/minecraft/server/MinecraftServer$MultiplayerScope;I)Z", at = @At("HEAD"))
    private void onOpenToLan(MinecraftServer.MultiplayerScope scope, int port, CallbackInfoReturnable<Boolean> cir) {
        IntegratedServer integratedServer = (IntegratedServer) (Object) this;
        integratedServer.setUsesAuthentication(OfflineLAN.onlineModeOption.getOnlineMode());
        OfflineLAN.LOG.info("{}", integratedServer.usesAuthentication());
        if (!integratedServer.usesAuthentication())
            OfflineLAN.LOG.info("The server will make no attempt to authenticate usernames. Beware.");
    }
//?} elif >=26.1 {
    /*@Inject(method = "publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", at = @At("HEAD"))
    private void onOpenToLan(net.minecraft.world.level.GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir) {
        IntegratedServer integratedServer = (IntegratedServer) (Object) this;
        integratedServer.setUsesAuthentication(OfflineLAN.onlineModeOption.getOnlineMode());
        OfflineLAN.LOG.info("{}", integratedServer.usesAuthentication());
        if (!integratedServer.usesAuthentication())
            OfflineLAN.LOG.info("The server will make no attempt to authenticate usernames. Beware.");
    }

    @Inject(method = "publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", at = @At("RETURN"))
    private void appendOfflineLanMessage(net.minecraft.world.level.GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue()) && !OfflineLAN.onlineModeOption.getOnlineMode()) {
            net.minecraft.client.Minecraft.getInstance().gui.getChat().addClientSystemMessage(net.minecraft.network.chat.Component.translatable("offlinelan.chatMessage"));
        }
    }
*///?} elif >=1.19 {
    /*@Inject(method = "publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", at = @At("HEAD"))
    private void onOpenToLan(net.minecraft.world.level.GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir) {
        IntegratedServer integratedServer = (IntegratedServer) (Object) this;
        integratedServer.setUsesAuthentication(OfflineLAN.onlineModeOption.getOnlineMode());
        OfflineLAN.LOG.info("{}", integratedServer.usesAuthentication());
        if (!integratedServer.usesAuthentication())
            OfflineLAN.LOG.info("The server will make no attempt to authenticate usernames. Beware.");
    }

    @Inject(method = "publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", at = @At("RETURN"))
    private void appendOfflineLanMessage(net.minecraft.world.level.GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue()) && !OfflineLAN.onlineModeOption.getOnlineMode()) {
            net.minecraft.client.Minecraft.getInstance().gui.getChat().addMessage(net.minecraft.network.chat.Component.translatable("offlinelan.chatMessage"));
        }
    }
*///?} else {
    /*@Inject(method = "publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", at = @At("HEAD"))
    private void onOpenToLan(net.minecraft.world.level.GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir) {
        IntegratedServer integratedServer = (IntegratedServer) (Object) this;
        integratedServer.setUsesAuthentication(OfflineLAN.onlineModeOption.getOnlineMode());
        OfflineLAN.LOG.info("{}", integratedServer.usesAuthentication());
        if (!integratedServer.usesAuthentication())
            OfflineLAN.LOG.info("The server will make no attempt to authenticate usernames. Beware.");
    }

    @Inject(method = "publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", at = @At("RETURN"))
    private void appendOfflineLanMessage(net.minecraft.world.level.GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue()) && !OfflineLAN.onlineModeOption.getOnlineMode()) {
            net.minecraft.client.Minecraft.getInstance().gui.getChat().addMessage(new net.minecraft.network.chat.TranslatableComponent("offlinelan.chatMessage"));
        }
    }
*///?}

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void resetSettings(CallbackInfo ci) {
        OfflineLAN.onlineModeOption.setOnlineMode(true);
    }
}
