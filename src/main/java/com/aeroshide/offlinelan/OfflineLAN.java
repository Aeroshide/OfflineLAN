package com.aeroshide.offlinelan;

import net.fabricmc.api.ModInitializer;
//? if >=1.18 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?}
//? if <1.18 {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///?}

public class OfflineLAN implements ModInitializer {
    public static OnlineModeBoolean onlineModeOption = new OnlineModeBoolean(true);
//? if >=1.18 {
    public static final Logger LOG = LoggerFactory.getLogger("OfflineLAN");
//?}
//? if <1.18 {
    /*public static final Logger LOG = LogManager.getLogger("OfflineLAN");
*///?}
    public static final String VERSION = /*$ version*/ "2.0.0";

    @Override
    public void onInitialize() {
    }
}
