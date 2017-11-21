package com.mcmoddev.mmdbuckets;

import com.mcmoddev.mmdbuckets.proxy.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Mod entry point
 * <p>
 * Many thanks to TehNut for his help and examples that got this to where it is
 *
 * @author Daniel Hazelton &lt;dshadowwolf@gmail.com&gt;
 */

@Mod(
        modid = MMDBuckets.MODID,
        name = MMDBuckets.NAME,
        version = MMDBuckets.VERSION,
        dependencies = "required-after:forge@[14.21.0.2327,);required-after:basemetals;after:modernmetals;",
        acceptedMinecraftVersions = "[1.12,)")
public class MMDBuckets {
    @Instance
    public static MMDBuckets instance;

    /**
     * ID of this Mod
     */
    public static final String MODID = "mmdbuckets";
    /**
     * Display name of this Mod
     */
    public static final String NAME = "MMD Buckets";
    /**
     * Version Number, in SemVer format
     */
    public static final String VERSION = "0.0.1-alpha2";

    /**
     * base format for naming the proxies
     */
    public static final String PROXY_BASE = "com.mcmoddev." + MODID + ".proxy.";

    @SidedProxy(clientSide = PROXY_BASE + "ClientProxy", serverSide = PROXY_BASE + "ServerProxy")
    public static CommonProxy proxy;

    public static Logger logger = LogManager.getFormatterLogger(MODID);


    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    public static boolean hasMMDLib() {
        return Loader.isModLoaded("mmdlib");
    }

    public static boolean hasModernMetals() {
        return Loader.isModLoaded("modernmetals");
    }

    public static boolean hasBaseMetals() {
        return Loader.isModLoaded("basemetals");
    }
}
