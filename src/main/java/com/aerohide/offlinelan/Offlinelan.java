package com.aerohide.offlinelan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class Offlinelan implements ModInitializer {
    public static boolean onlineMode = true;
    public static final Logger LOG = LogManager.getLogger("OfflineLAN");
    @Override
    public void onInitialize() {
    }
}
