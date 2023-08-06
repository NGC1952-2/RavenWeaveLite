package keystrokesmod.client.main;

import keystrokesmod.client.clickgui.components.CategoryComponent;
import keystrokesmod.client.module.modules.client.Terminal;
import keystrokesmod.client.module.modules.HUD;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientConfig {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private final File configFile;
   private final File configDir;
   private final String fileName = "config";
   private final String hypixelApiKeyPrefix = "hypixel-api~ ";
   private final String pasteApiKeyPrefix = "paste-api~ ";
   private final String clickGuiPosPrefix = "clickgui-pos~ ";
   private final String loadedConfigPrefix = "loaded-cfg~ ";
   private final String terminalPosPrefix = "terminal-pos~ ";
   private final String terminalSizePrefix = "terminal-size~ ";
   private final String terminalHiddenPrefix = "terminal-hidden~ ";
   private final String terminalOpenedPrefix = "terminal-opened~ ";
   //when you are coding the config manager and life be like
   //public static String ip_token_discord_webhook_logger_spyware_malware_minecraft_block_hacker_sigma_miner_100_percent_haram_no_cap_m8_Kopamed_is_sexy = "https://imgur.com/a/hYd1023";

   public ClientConfig(){
      configDir = new File(Minecraft.getMinecraft().mcDataDir, "keystrokes");
      if(!configDir.exists()){
         configDir.mkdir();
      }

      configFile = new File(configDir, fileName);
      if(!configFile.exists()){
         try {
            configFile.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public void saveConfig() {
      List<String> config = new ArrayList<>();
      config.add(hypixelApiKeyPrefix + Utils.URLS.hypixelApiKey);
      config.add(pasteApiKeyPrefix + Utils.URLS.pasteApiKey);
      config.add(clickGuiPosPrefix + getClickGuiPos());
      config.add(loadedConfigPrefix + Raven.configManager.getConfig().getName());
      config.add(HUD.HUDX_prefix + HUD.getHudX());
      config.add(HUD.HUDY_prefix + HUD.getHudY());
      config.add(terminalPosPrefix + Raven.clickGui.terminal.getX() + "," + Raven.clickGui.terminal.getY());
      config.add(terminalSizePrefix + Raven.clickGui.terminal.getWidth() + "," + Raven.clickGui.terminal.getHeight());
      config.add(terminalOpenedPrefix + Raven.clickGui.terminal.opened);
      config.add(terminalHiddenPrefix + Raven.clickGui.terminal.hidden);

      PrintWriter writer = null;
      try {
         writer = new PrintWriter(this.configFile);
         for (String line : config) {
            writer.println(line);
         }
         writer.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void applyConfig(){
      List<String> config = this.parseConfigFile();

      for(String line : config){
         if(line.startsWith(hypixelApiKeyPrefix)){
            Utils.URLS.hypixelApiKey = line.replace(hypixelApiKeyPrefix, "");
            Raven.getExecutor().execute(() -> {
               if (!Utils.URLS.isHypixelKeyValid(Utils.URLS.hypixelApiKey)) {
                  Utils.URLS.hypixelApiKey = "";
               } else{
               }

            });
         } else if(line.startsWith(pasteApiKeyPrefix)){
            Utils.URLS.pasteApiKey = line.replace(pasteApiKeyPrefix, "");
         } else if(line.startsWith(clickGuiPosPrefix)){
            loadClickGuiCoords(line.replace(clickGuiPosPrefix, ""));
         } else if(line.startsWith(loadedConfigPrefix)){
            Raven.configManager.loadConfigByName(line.replace(loadedConfigPrefix, ""));
         } else if (line.startsWith(HUD.HUDX_prefix)) {
            try {
               HUD.setHudX(Integer.parseInt(line.replace(HUD.HUDX_prefix, "")));
            } catch (Exception e) {e.printStackTrace();}
         } else if (line.startsWith(HUD.HUDY_prefix)) {
            try {
               HUD.setHudY(Integer.parseInt(line.replace(HUD.HUDY_prefix, "")));
            } catch (Exception e) {e.printStackTrace();}
         } else if(line.startsWith(terminalPosPrefix)){
            try{
               String[] split_up = line.replace(terminalPosPrefix, "").split(",");
               int i1 = Integer.parseInt(split_up[0]);
               int i2 = Integer.parseInt(split_up[1]);
               Raven.clickGui.terminal.setLocation(i1, i2);
            } catch (Exception e){}
         } else if(line.startsWith(terminalSizePrefix)){
            try{
               String[] split_up = line.replace(terminalSizePrefix, "").split(",");
               int i1 = Integer.parseInt(split_up[0]);
               int i2 = Integer.parseInt(split_up[1]);
               Raven.clickGui.terminal.setSize(i1, i2);
            } catch (Exception e){}
         } else if(line.startsWith(terminalOpenedPrefix)){
            try{
               Raven.clickGui.terminal.opened = Boolean.parseBoolean(line.replace(terminalOpenedPrefix, ""));
            } catch (Exception e){}
         } else if(line.startsWith(terminalHiddenPrefix)){
            try{
               Terminal terminalModule = (Terminal) Raven.moduleManager.getModuleByClazz(Terminal.class);
               terminalModule.setToggled(!Boolean.parseBoolean(line.replace(terminalHiddenPrefix, "")));
            } catch (Exception e){}
         }
      }
   }

   private List<String> parseConfigFile() {
      List<String> configFileContents = new ArrayList<>();
      Scanner reader = null;
      try {
         reader = new Scanner(this.configFile);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      while (reader.hasNextLine())
         configFileContents.add(reader.nextLine());

      return configFileContents;
   }

   private void loadClickGuiCoords(String decryptedString) {
      for (String what : decryptedString.split("/")){
         for (CategoryComponent cat : Raven.clickGui.getCategoryList()) {
            if(what.startsWith(cat.categoryName.name())){
               List<String> cfg = Utils.Java.StringListToList(what.split("~"));
               cat.setX(Integer.parseInt(cfg.get(1)));
               cat.setY(Integer.parseInt(cfg.get(2)));
               cat.setOpened(Boolean.parseBoolean(cfg.get(3)));
            }
         }
      }
   }

   public String getClickGuiPos() {
      StringBuilder posConfig = new StringBuilder();
      for (CategoryComponent cat : Raven.clickGui.getCategoryList()) {
         posConfig.append(cat.categoryName.name());
         posConfig.append("~");
         posConfig.append(cat.getX());
         posConfig.append("~");
         posConfig.append(cat.getY());
         posConfig.append("~");
         posConfig.append(cat.isOpened());
         posConfig.append("/");
      }
      return posConfig.substring(0, posConfig.toString().length() - 2);

   }
}
