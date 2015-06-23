//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.tools;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import tconstruct.TConstruct;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.library.event.ToolBuildEvent;
import tconstruct.library.event.PartBuilderEvent.NormalPart;
import tconstruct.library.event.ToolCraftEvent.NormalTool;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;
import tconstruct.util.ItemHelper;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.network.MovementUpdatePacket;

public class TinkerToolEvents {
    public TinkerToolEvents() {
    }

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event) {
        Item item = event.crafting.getItem();
        if (!event.player.worldObj.isRemote) {
            if (item == Item.getItemFromBlock(TinkerTools.toolStationWood)) {
                if (!PHConstruct.beginnerBook) {
                    return;
                }

                TPlayerStats i = TPlayerStats.get(event.player);
                if (!i.materialManual) {
                    i.materialManual = true;
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TinkerTools.manualBook, 1, 1));
                }
            }

            if (item == Item.getItemFromBlock(TinkerTools.craftingSlabWood) && event.crafting.getMetadata() == 4) {
                for (int var5 = 0; var5 < event.craftMatrix.getSizeInventory(); ++var5) {
                    ItemStack stack = event.craftMatrix.getStackInSlot(var5);
                    if (stack != null && stack.getItem() == Item.getItemFromBlock(TinkerTools.toolStationWood) && stack.getMetadata() == 5) {
                        event.crafting.setTagCompound(stack.getTagCompound());
                        break;
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public void buildTool(ToolBuildEvent event) {
        if (event.handleStack.getItem() == Items.bone) {
            event.handleStack = new ItemStack(TinkerTools.toolRod, 1, 5);
        } else {
            ArrayList sticks = OreDictionary.getOres("stickWood");
            Iterator i$ = sticks.iterator();

            ItemStack stick;
            do {
                if (!i$.hasNext()) {
                    return;
                }

                stick = (ItemStack) i$.next();
            } while (!OreDictionary.itemMatches(stick, event.handleStack, false));

            event.handleStack = new ItemStack(TinkerTools.toolRod, 1, 0);
        }
    }

    @SubscribeEvent
    public void craftTool(NormalTool event) {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");
        int modifiers;
        if (PHConstruct.denyMattock && event.tool == TinkerTools.mattock) {
            modifiers = toolTag.getInteger("Head");
            int handle = toolTag.getInteger("Handle");
            int accessory = toolTag.getInteger("Accessory");
            if (!this.allowCrafting(modifiers, handle, accessory)) {
                event.setResult(Result.DENY);
                return;
            }
        }

        this.handlePaper(toolTag, event.tool);
        this.handleThaumium(toolTag, event.tool);
        if (event.tool == TinkerTools.battlesign) {
            modifiers = toolTag.getInteger("Modifiers");
            ++modifiers;
            toolTag.setInteger("Modifiers", modifiers);
        }

    }

    private void handlePaper(NBTTagCompound toolTag, ToolCore tool) {
        int modifiers = toolTag.getInteger("Modifiers");
        if (toolTag.getInteger("Head") == 9) {
            ++modifiers;
        }

        if (toolTag.getInteger("Handle") == 9) {
            ++modifiers;
        }

        if (toolTag.getInteger("Accessory") == 9) {
            ++modifiers;
        }

        if (toolTag.getInteger("Extra") == 9) {
            ++modifiers;
        }

        if (tool.getPartAmount() == 2 && toolTag.getInteger("Head") == 9) {
            ++modifiers;
        }

        toolTag.setInteger("Modifiers", modifiers);
    }

    private void handleThaumium(NBTTagCompound toolTag, ToolCore tool) {
        int thaum = 0;
        if (toolTag.getInteger("Head") == 31) {
            ++thaum;
        }

        if (toolTag.getInteger("Handle") == 31) {
            ++thaum;
        }

        if (toolTag.getInteger("Accessory") == 31) {
            ++thaum;
        }

        if (toolTag.getInteger("Extra") == 31) {
            ++thaum;
        }

        int bonusModifiers = (int) Math.ceil((double) thaum / 2.0D);
        if (tool.getPartAmount() == 2) {
            bonusModifiers = thaum;
        }

        int modifiers = toolTag.getInteger("Modifiers");
        modifiers += bonusModifiers;
        toolTag.setInteger("Modifiers", modifiers);
    }

    private boolean allowCrafting(int head, int handle, int accessory) {
        int[] nonMetals = new int[]{0, 1, 3, 4, 5, 6, 7, 8, 9, 17};

        for (int i = 0; i < nonMetals.length; ++i) {
            if (head == nonMetals[i] || handle == nonMetals[i] || accessory == nonMetals[i]) {
                return false;
            }
        }

        return true;
    }

    @SubscribeEvent
    public void craftPart(NormalPart event) {
        ItemStack result;
        if (event.pattern.getItem() == TinkerTools.woodPattern && event.pattern.getMetadata() == 23) {
            result = craftBowString(event.material);
            if (result != null) {
                event.overrideResult(new ItemStack[]{result, null});
            }
        }

        if (event.pattern.getItem() == TinkerTools.woodPattern && event.pattern.getMetadata() == 24) {
            result = craftFletching(event.material);
            if (result != null) {
                event.overrideResult(new ItemStack[]{result, null});
            }
        }

    }

    public static ItemStack craftBowString(ItemStack stack) {
        if (stack.stackSize < 3) {
            return null;
        } else {
            BowstringMaterial mat = (BowstringMaterial) TConstructRegistry.getCustomMaterial(stack, BowstringMaterial.class);
            return mat != null ? mat.craftingItem.copy() : null;
        }
    }

    public static ItemStack craftFletching(ItemStack stack) {
        FletchingMaterial mat = (FletchingMaterial) TConstructRegistry.getCustomMaterial(stack, FletchingMaterial.class);
        if (mat == null) {
            mat = (FletchingMaterial) TConstructRegistry.getCustomMaterial(stack, FletchlingLeafMaterial.class);
        }

        return mat != null ? mat.craftingItem.copy() : null;
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() == TinkerTools.battlesign) {
                if (!stack.hasTagCompound() || stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken")) {
                    return;
                }

                DamageSource source = event.source;
                if (!source.isUnblockable() && !source.isMagicDamage() && !source.isExplosion()) {
                    Entity attacker;
                    if (source.isProjectile()) {
                        attacker = source.getSourceOfDamage();
                        Vec3 motion = Vec3.createVectorHelper(attacker.motionX, attacker.motionY, attacker.motionZ);
                        Vec3 look = player.getLookVec();
                        double strength = -look.dotProduct(motion.normalize());
                        if (strength < 0.1D) {
                            return;
                        }

                        event.setCanceled(true);
                        double speed = attacker.motionX * attacker.motionX + attacker.motionY * attacker.motionY + attacker.motionZ * attacker.motionZ;
                        speed = Math.sqrt(speed);
                        speed = (speed + 2.0D) * strength;
                        attacker.motionX = look.xCoord * speed;
                        attacker.motionY = look.yCoord * speed;
                        attacker.motionZ = look.zCoord * speed;
                        attacker.rotationYaw = (float) (Math.atan2(attacker.motionX, attacker.motionZ) * 180.0D / 3.141592653589793D);
                        attacker.rotationPitch = (float) (Math.atan2(attacker.motionY, speed) * 180.0D / 3.141592653589793D);
                        TConstruct.packetPipeline.sendToAll(new MovementUpdatePacket(attacker));
                        if (attacker instanceof EntityArrow) {
                            ((EntityArrow) attacker).shootingEntity = player;
                            attacker.motionX /= -0.10000000149011612D;
                            attacker.motionY /= -0.10000000149011612D;
                            attacker.motionZ /= -0.10000000149011612D;
                            if (attacker instanceof ProjectileBase) {
                                ((ProjectileBase) attacker).defused = false;
                            }
                        }
                    } else {
                        attacker = source.getEntity();
                        if (attacker != null) {
                            attacker.attackEntityFrom(DamageSource.causeThornsDamage(player), event.ammount);
                        }
                    }

                    AbilityHelper.damageTool(stack, (int) Math.ceil((double) (event.ammount / 2.0F)), player, false);
                }
            }
        }

    }

    @SubscribeEvent
    public void onLivingDrop(LivingDropsEvent event) {
        if (event.entityLiving != null) {
            EntityPlayer source;
            ItemStack stack;
            int beheading;
            if (event.recentlyHit) {
                if (event.entityLiving.getClass() == EntitySkeleton.class) {
                    EntitySkeleton player = (EntitySkeleton) event.entityLiving;
                    if (event.source.damageType.equals("player")) {
                        source = (EntityPlayer) event.source.getEntity();
                        stack = source.getCurrentEquippedItem();
                        if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                            beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                            if (stack.getItem() == TinkerTools.cleaver) {
                                beheading += 2;
                            }

                            if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 10) {
                                ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, player.getSkeletonType()));
                            }
                        }
                    }

                    if (player.getSkeletonType() == 1 && TConstruct.random.nextInt(Math.max(1, 5 - event.lootingLevel)) == 0) {
                        ItemHelper.addDrops(event, new ItemStack(TinkerTools.materials, 1, 8));
                    }
                }

                if (event.entityLiving.getClass() == EntityZombie.class) {
                    EntityZombie player1 = (EntityZombie) event.entityLiving;
                    if (event.source.damageType.equals("player")) {
                        source = (EntityPlayer) event.source.getEntity();
                        stack = source.getCurrentEquippedItem();
                        if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                            beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                            if (stack != null && stack.hasTagCompound() && stack.getItem() == TinkerTools.cleaver) {
                                beheading += 2;
                            }

                            if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 10) {
                                ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 2));
                            }
                        }

                        if (stack != null && stack.hasTagCompound() && stack.getItem() == TinkerTools.cleaver && TConstruct.random.nextInt(100) < 10) {
                            ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 2));
                        }
                    }
                }

                if (event.entityLiving.getClass() == EntityCreeper.class) {
                    EntityCreeper player2 = (EntityCreeper) event.entityLiving;
                    if (event.source.damageType.equals("player")) {
                        source = (EntityPlayer) event.source.getEntity();
                        stack = source.getCurrentEquippedItem();
                        if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                            beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                            if (stack.getItem() == TinkerTools.cleaver) {
                                beheading += 2;
                            }

                            if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 5) {
                                ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 4));
                            }
                        }
                    }
                }
            }

            if (event.entityLiving instanceof EntityPlayer) {
                EntityPlayer player3 = (EntityPlayer) event.entityLiving;
                if (PHConstruct.dropPlayerHeads) {
                    ItemStack source1 = new ItemStack(Items.skull, 1, 3);
                    NBTTagCompound stack1 = new NBTTagCompound();
                    stack1.setString("SkullOwner", player3.getDisplayName());
                    source1.setTagCompound(stack1);
                    ItemHelper.addDrops(event, source1);
                } else if (event.source.damageType.equals("player")) {
                    source = (EntityPlayer) event.source.getEntity();
                    stack = source.getCurrentEquippedItem();
                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore) {
                        beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.getItem() == TinkerTools.cleaver) {
                            beheading += 2;
                        }

                        if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 50) {
                            ItemStack dropStack = new ItemStack(Items.skull, 1, 3);
                            NBTTagCompound nametag = new NBTTagCompound();
                            nametag.setString("SkullOwner", player3.getDisplayName());
                            dropStack.setTagCompound(nametag);
                            ItemHelper.addDrops(event, dropStack);
                        }
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public void registerOre(OreRegisterEvent evt) {
        if (evt.Name.equals("crystalQuartz")) {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 2);
        } else if (evt.Name.equals("crystalCertusQuartz")) {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 24);
        }

    }

    @SubscribeEvent
    public void damageToolsOnDeath(PlayerDropsEvent event) {
        if (PHConstruct.deathPenality) {
            EnumDifficulty difficulty = event.entityPlayer.worldObj.difficultySetting;
            if (difficulty != EnumDifficulty.PEACEFUL && difficulty != EnumDifficulty.EASY) {
                int punishment = 20;
                if (difficulty == EnumDifficulty.HARD) {
                    punishment = 10;
                }

                int derp = 1;
                if (event.entityPlayer.ticksExisted < 6000) {
                    derp = TPlayerStats.get(event.entityPlayer).derpLevel;
                    if (derp <= 0) {
                        derp = 1;
                    }

                    punishment *= derp;
                }

                boolean damaged = false;
                Iterator i$ = event.drops.iterator();

                while (i$.hasNext()) {
                    EntityItem drop = (EntityItem) i$.next();
                    if (drop.getEntityItem().getItem() instanceof ToolCore && drop.getEntityItem().hasTagCompound()) {
                        NBTTagCompound tags = drop.getEntityItem().getTagCompound().getCompoundTag("InfiTool");
                        int dur = tags.getInteger("TotalDurability");
                        dur /= punishment;
                        AbilityHelper.damageTool(drop.getEntityItem(), dur, event.entityPlayer, true);
                        damaged = true;
                    }
                }

                if (damaged) {
                    ++derp;
                }

                TPlayerStats.get(event.entityPlayer).derpLevel = derp + 1;
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(PlayerEvent.BreakSpeed e) {
        if (e.entityPlayer == null) return;

        ItemStack stack = e.entityPlayer.getCurrentEquippedItem();
        if (stack == null) return;

        if (stack.getItem() instanceof HarvestTool) {
            ((HarvestTool) stack.getItem()).onBlockBreak(e);
        }
    }
}
