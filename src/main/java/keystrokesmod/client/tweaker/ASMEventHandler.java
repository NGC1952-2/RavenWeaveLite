package keystrokesmod.client.tweaker;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.combat.LeftClicker;
import keystrokesmod.client.module.modules.combat.Reach;
import keystrokesmod.client.module.modules.movement.KeepSprint;
import keystrokesmod.client.module.modules.movement.NoSlow;
import keystrokesmod.client.module.modules.player.SafeWalk;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

public class ASMEventHandler {
   public static String eventHandlerClassName = ASMEventHandler.class.getName().replace(".", "/"); //added replace or it won't launch
   private static final Minecraft mc = Minecraft.getMinecraft();

   public static String getUnformattedTextForChat(String s) {
      return s;
   }


   /**
    * called when an entity moves
    * ASM Modules : SafeWalk
    */
   public static boolean onEntityMove(Entity entity) {
      if (entity == mc.thePlayer && mc.thePlayer.onGround) {
         Module safeWalk = Raven.moduleManager.getModuleByClazz(SafeWalk.class);

         if (safeWalk != null && safeWalk.isEnabled() && !SafeWalk.doShift.isToggled()) {
            if (SafeWalk.blocksOnly.isToggled()) {
               ItemStack i = mc.thePlayer.getHeldItem();
               if (i == null || !(i.getItem() instanceof ItemBlock)) {
                  return mc.thePlayer.isSneaking();
               }
            }

            return true;
         } else {
            return mc.thePlayer.isSneaking();
         }
      } else {
         return false;
      }
   }

   /*public String getModName()
   {
      return "lunarclient:db2533c";
   }*/

   /**
    * called when a player is using an item (aka right-click)
    * ASM Modules : NoSlow
    */
   public static void onLivingUpdate() {
      Module noSlow = Raven.moduleManager.getModuleByClazz(NoSlow.class);
      if (noSlow != null && noSlow.isEnabled()) {
         NoSlow.sl();
      } else {
         mc.thePlayer.movementInput.moveStrafe *= 0.2F;
         mc.thePlayer.movementInput.moveForward *= 0.2F;
      }
   }

   /**
    * called when a player is moving and hits another one
    * ASM Modules : KeepSprint
    */
   public static void onAttackTargetEntityWithCurrentItem(Entity en) {
      Module keepSprint = Raven.moduleManager.getModuleByClazz(KeepSprint.class);
      if (keepSprint != null && keepSprint.isEnabled()) {
         KeepSprint.onHit(en);
      } else {
         mc.thePlayer.motionX *= 0.6D;
         mc.thePlayer.motionZ *= 0.6D;
      }
   }

   /**
    * called every tick
    * ASM Modules : AutoClicker, Reach
    */
   public static void onTick() {
      Module autoClicker = Raven.moduleManager.getModuleByClazz(LeftClicker.class);
      if (mc == null || mc.entityRenderer == null) return;
      if (autoClicker == null || !autoClicker.isEnabled() || !Mouse.isButtonDown(0) || !Reach.call()) {
         mc.entityRenderer.getMouseOver(1.0F);
      }
   }
}
