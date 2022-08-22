package net.borisshoes.shulkerdupefix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Shulkerdupefix implements ModInitializer {
   
   private static final Logger logger = LogManager.getLogger("Shulker Dupe Fix");
   
   @Override
   public void onInitialize(){
      ServerTickEvents.END_SERVER_TICK.register(this::onTick);
      PlayerBlockBreakEvents.BEFORE.register(this::breakBlocks);
      
      logger.info("Initializing Shulker Dupe Fix");
   }
   
   private boolean breakBlocks(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity){
      ScreenHandler screen = playerEntity.currentScreenHandler;
      if(screen instanceof ShulkerBoxScreenHandler){
         //System.out.println("Detected Breakage While in Shulker Box");
         
         if(blockEntity instanceof ShulkerBoxBlockEntity){
            //System.out.println("Detected Shulker Box Breakage While in Shulker Box");
            
            // Alert online OPs
            String alert = "A Player Attempted Shulker Box Dupe: "+playerEntity.getEntityName()+" ["+playerEntity.getUuidAsString()+"]";
            logger.info(alert);
            
            try{
               PlayerManager manager = world.getServer().getPlayerManager();
               if(manager != null){
                  for(String op : manager.getOpNames()){
                     ServerPlayerEntity admin = manager.getPlayer(op);
                     if(admin != null){
                        admin.sendMessage(MutableText.of(new LiteralTextContent(alert)).formatted(Formatting.RED));
                     }
                  }
               }
            }catch(Exception e){
               e.printStackTrace();
               return false;
            }
            return false;
         }else{
            return true;
         }
      }else{
         return true;
      }
      
   }
   
   private void onTick(MinecraftServer server){
      List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
      for(ServerPlayerEntity player : players){
         ScreenHandler screen = player.currentScreenHandler;
         if(!(screen == null || screen.equals(player.playerScreenHandler))){
            //System.out.println(screen.toString());
         }
      }
   }
}
