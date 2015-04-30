//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import java.util.Map;
import java.util.Random;
import mantle.pulsar.config.ForgeCFG;
import mantle.pulsar.control.PulseManager;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tconstruct.achievements.AchievementEvents;
import tconstruct.achievements.TAchievements;
import tconstruct.api.TConstructAPI;
import tconstruct.armor.TinkerArmor;
import tconstruct.armor.player.TPlayerHandler;
import tconstruct.common.TProxyCommon;
import tconstruct.library.TConstructCreativeTab;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.mechworks.TinkerMechworks;
import tconstruct.mechworks.landmine.behavior.Behavior;
import tconstruct.mechworks.landmine.behavior.stackCombo.SpecialStackHandler;
import tconstruct.plugins.TinkerThaumcraft;
import tconstruct.plugins.fmp.TinkerFMP;
import tconstruct.plugins.gears.TinkerGears;
import tconstruct.plugins.ic2.TinkerIC2;
import tconstruct.plugins.imc.TinkerAE2;
import tconstruct.plugins.imc.TinkerBuildCraft;
import tconstruct.plugins.imc.TinkerMystcraft;
import tconstruct.plugins.mfr.TinkerMFR;
import tconstruct.plugins.te4.TinkerTE4;
import tconstruct.plugins.te4.TinkersThermalFoundation;
import tconstruct.plugins.ubc.TinkerUBC;
import tconstruct.plugins.waila.TinkerWaila;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerModification;
import tconstruct.tools.TinkerTools;
import tconstruct.util.EnvironmentChecks;
import tconstruct.util.IMCHandler;
import tconstruct.util.config.DimensionBlacklist;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.network.PacketPipeline;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.world.TinkerWorld;
import tconstruct.world.gen.SlimeIslandGen;
import tconstruct.world.village.ComponentSmeltery;
import tconstruct.world.village.ComponentToolWorkshop;
import tconstruct.world.village.TVillageTrades;
import tconstruct.world.village.VillageSmelteryHandler;
import tconstruct.world.village.VillageToolStationHandler;

@Mod(
        modid = "TConstruct",
        name = "TConstruct",
        version = "1.7.10-1.8.3.build919",
        dependencies = "required-after:Forge@[10.13.2.1291,11.14);required-after:Mantle@[1.7.10-0.3.2,);after:MineFactoryReloaded@[1.7.10R2.8.0RC7,);after:NotEnoughItems;after:Waila;after:ThermalExpansion@[1.7.10R4.0.0RC2,);after:ThermalFoundation@[1.7.10R1.0.0RC3,);before:UndergroundBiomes",
        acceptedMinecraftVersions = "[1.7.10]"
)
public class TConstruct {
    public static final String modVersion = "1.7.10-1.8.3.build919";
    public static final int ingotLiquidValue = 144;
    public static final int oreLiquidValue = 288;
    public static final int blockLiquidValue = 1296;
    public static final int chunkLiquidValue = 72;
    public static final int nuggetLiquidValue = 16;
    public static final int stoneLiquidValue = 18;
    public static final int liquidUpdateAmount = 6;
    public static final String modID = "TConstruct";
    public static final Logger logger = LogManager.getLogger("TConstruct");
    public static final PacketPipeline packetPipeline = new PacketPipeline();
    public static Random random = new Random();
    @Instance("TConstruct")
    public static TConstruct instance;
    @SidedProxy(
            clientSide = "tconstruct.client.TProxyClient",
            serverSide = "tconstruct.common.TProxyCommon"
    )
    public static TProxyCommon proxy;
    public static PulseManager pulsar = new PulseManager("TConstruct", new ForgeCFG("TinkersModules", "Modules: Disabling these will disable a chunk of the mod"));
    public static TPlayerHandler playerTracker;
    public static LiquidCasting tableCasting;
    public static LiquidCasting basinCasting;
    public static Detailing chiselDetailing;

    public TConstruct() {
        if(Loader.isModLoaded("Natura")) {
            logger.info("Natura, what are we going to do tomorrow night?");
            LogManager.getLogger("Natura").info("TConstruct, we\'re going to take over the world!");
        } else {
            logger.info("Preparing to take over the world");
        }

        EnvironmentChecks.verifyEnvironmentSanity();
    }

    @NetworkCheckHandler
    public boolean matchModVersions(Map<String, String> remoteVersions, Side side) {
        return remoteVersions.containsKey("TConstruct") && "1.7.10-1.8.3.build919".equals(remoteVersions.get("TConstruct"));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PHConstruct.initProps(event.getModConfigurationDirectory());
        pulsar.registerPulse(new TinkerWorld());
        pulsar.registerPulse(new TinkerTools());
        pulsar.registerPulse(new TinkerSmeltery());
        pulsar.registerPulse(new TinkerMechworks());
        pulsar.registerPulse(new TinkerArmor());
        pulsar.registerPulse(new TinkerWeaponry());
        pulsar.registerPulse(new TinkerThaumcraft());
        pulsar.registerPulse(new TinkerWaila());
        pulsar.registerPulse(new TinkerBuildCraft());
        pulsar.registerPulse(new TinkerAE2());
        pulsar.registerPulse(new TinkerIC2());
        pulsar.registerPulse(new TinkerMystcraft());
        pulsar.registerPulse(new TinkerMFR());
        pulsar.registerPulse(new TinkerTE4());
        pulsar.registerPulse(new TinkersThermalFoundation());
        pulsar.registerPulse(new TinkerFMP());
        pulsar.registerPulse(new TinkerUBC());
        pulsar.registerPulse(new TinkerGears());
        pulsar.registerPulse(new TinkerModification());
        TConstructRegistry.materialTab = new TConstructCreativeTab("TConstructMaterials");
        TConstructRegistry.toolTab = new TConstructCreativeTab("TConstructTools");
        TConstructRegistry.partTab = new TConstructCreativeTab("TConstructParts");
        TConstructRegistry.blockTab = new TConstructCreativeTab("TConstructBlocks");
        TConstructRegistry.equipableTab = new TConstructCreativeTab("TConstructEquipables");
        TConstructRegistry.weaponryTab = new TConstructCreativeTab("TConstructWeaponry");
        tableCasting = new LiquidCasting();
        basinCasting = new LiquidCasting();
        chiselDetailing = new Detailing();
        playerTracker = new TPlayerHandler();
        FMLCommonHandler.instance().bus().register(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        pulsar.preInit(event);
        if(PHConstruct.achievementsEnabled) {
            TAchievements.addDefaultAchievements();
        }

        if(PHConstruct.addToVillages) {
            VillagerRegistry.instance().registerVillagerId(78943);
            if(PHConstruct.allowVillagerTrading) {
                VillagerRegistry.instance().registerVillageTradeHandler(78943, new TVillageTrades());
            }

            VillagerRegistry.instance().registerVillageCreationHandler(new VillageToolStationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageSmelteryHandler());
            MapGenStructureIO.registerStructureComponent(ComponentToolWorkshop.class, "TConstruct:ToolWorkshopStructure");
            MapGenStructureIO.registerStructureComponent(ComponentSmeltery.class, "TConstruct:SmelteryStructure");
        }

        TConstructAPI.PROP_NAME = "TConstruct";
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        packetPipeline.initalise();
        if(event.getSide() == Side.CLIENT) {
            ;
        }

        DimensionBlacklist.getBadBimensions();
        GameRegistry.registerWorldGenerator(new SlimeIslandGen(TinkerWorld.slimePool, 2), 2);
        pulsar.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInitialise();
        Behavior.registerBuiltInBehaviors();
        SpecialStackHandler.registerBuiltInStackHandlers();
        proxy.initialize();
        pulsar.postInit(event);
        if(PHConstruct.achievementsEnabled) {
            TAchievements.registerAchievementPane();
            MinecraftForge.EVENT_BUS.register(new AchievementEvents());
        }

    }

    @EventHandler
    public void handleIMC(IMCEvent e) {
        IMCHandler.processIMC(e.getMessages());
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent evt) {
        IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this));
    }

    public static LiquidCasting getTableCasting() {
        return tableCasting;
    }

    public static LiquidCasting getBasinCasting() {
        return basinCasting;
    }

    public static Detailing getChiselDetailing() {
        return chiselDetailing;
    }
}
