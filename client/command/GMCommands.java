/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleJob;
import client.MapleSkinColor;
import client.MapleStat;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.ItemConstants;
import constants.ServerConstants;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import net.MaplePacketHandler;
import net.PacketProcessor;
import net.server.Server;
import net.server.channel.Channel;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.event.AutoKill;
import scripting.event.EventHandler;
import scripting.portal.PortalScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.TimerManager;
import server.events.gm.MapleEvent;
import server.expeditions.MapleExpedition;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.PlayerNPCs;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.HexTool;
import tools.MapleLogger;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Administrator
 */
public class GMCommands {
     
    private static HashMap<String, String> eventcommands = new HashMap<>();
     private static MapleClient tmp = null;
      public static EventHandler event = new EventHandler();
      
      private static String[] songs = {
		"Jukebox/Congratulation", 
		"Bgm00/SleepyWood", 
		"Bgm00/FloralLife", 
		"Bgm00/GoPicnic", 
		"Bgm00/Nightmare", 
		"Bgm00/RestNPeace",
		"Bgm01/AncientMove", 
		"Bgm01/MoonlightShadow", 
		"Bgm01/WhereTheBarlogFrom", 
		"Bgm01/CavaBien", 
		"Bgm01/HighlandStar", 
		"Bgm01/BadGuys",
		"Bgm02/MissingYou", 
		"Bgm02/WhenTheMorningComes", 
		"Bgm02/EvilEyes", 
		"Bgm02/JungleBook", 
		"Bgm02/AboveTheTreetops",
		"Bgm03/Subway", 
		"Bgm03/Elfwood", 
		"Bgm03/BlueSky", 
		"Bgm03/Beachway",
		"Bgm03/SnowyVillage",
		"Bgm04/PlayWithMe", 
		"Bgm04/WhiteChristmas", 
		"Bgm04/UponTheSky",
		"Bgm04/ArabPirate", 
		"Bgm04/Shinin'Harbor",
		"Bgm04/WarmRegard",
		"Bgm05/WolfWood", 
		"Bgm05/DownToTheCave", 
		"Bgm05/AbandonedMine", 
		"Bgm05/MineQuest",
		"Bgm05/HellGate",
		"Bgm06/FinalFight", 
		"Bgm06/WelcomeToTheHell",
		"Bgm06/ComeWithMe", 
		"Bgm06/FlyingInABlueDream", 
		"Bgm06/FantasticThinking",
		"Bgm07/WaltzForWork", 
		"Bgm07/WhereverYouAre", 
		"Bgm07/FunnyTimeMaker", 
		"Bgm07/HighEnough", 
		"Bgm07/Fantasia",
		"Bgm08/LetsMarch", 
		"Bgm08/ForTheGlory", 
		"Bgm08/FindingForest", 
		"Bgm08/LetsHuntAliens", 
		"Bgm08/PlotOfPixie",
		"Bgm09/DarkShadow", 
		"Bgm09/TheyMenacingYou", 
		"Bgm09/FairyTale", 
		"Bgm09/FairyTalediffvers",
		"Bgm09/TimeAttack",
		"Bgm10/Timeless", 
		"Bgm10/TimelessB", 
		"Bgm10/BizarreTales",
		"Bgm10/TheWayGrotesque",
		"Bgm10/Eregos",
		"Bgm11/BlueWorld", 
		"Bgm11/Aquarium",
		"Bgm11/ShiningSea",
		"Bgm11/DownTown", 
		"Bgm11/DarkMountain",
		"Bgm12/AquaCave", 
		"Bgm12/DeepSee", 
		"Bgm12/WaterWay", 
		"Bgm12/AcientRemain",
		"Bgm12/RuinCastle",
		"Bgm12/Dispute",
		"Bgm13/CokeTown", 
		"Bgm13/Leafre", 
		"Bgm13/Minar'sDream", 
		"Bgm13/AcientForest", 
		"Bgm13/TowerOfGoddess",
		"Bgm14/DragonLoad", 
		"Bgm14/HonTale", 
		"Bgm14/CaveOfHontale", 
		"Bgm14/DragonNest", 
		"Bgm14/Ariant", 
		"Bgm14/HotDesert",
		"Bgm15/MureungHill", 
		"Bgm15/MureungForest", 
		"Bgm15/WhiteHerb",
		"Bgm15/Pirate",
		"Bgm15/SunsetDesert", 
		"Bgm16/Duskofgod", 
		"Bgm16/FightingPinkBeen", 
		"Bgm16/Forgetfulness", 
		"Bgm16/Remembrance", 
		"Bgm16/Repentance", 
		"Bgm16/TimeTemple", 
		"Bgm17/MureungSchool1",
		"Bgm17/MureungSchool2", 
		"Bgm17/MureungSchool3",
		"Bgm17/MureungSchool4", 
		"Bgm18/BlackWing", 
		"Bgm18/DrillHall", 
		"Bgm18/QueensGarden",
		"Bgm18/RaindropFlower", 
		"Bgm18/WolfAndSheep",
		"Bgm19/BambooGym",
		"Bgm19/CrystalCave", 
		"Bgm19/MushCatle", 
		"Bgm19/RienVillage",
		"Bgm19/SnowDrop", 
		"Bgm20/GhostShip", 
		"Bgm20/NetsPiramid",
		"Bgm20/UnderSubway", 
		"Bgm21/2021year",
		"Bgm21/2099year", 
		"Bgm21/2215year", 
		"Bgm21/2230year",
		"Bgm21/2503year",
		"Bgm21/KerningSquare",
		"Bgm21/KerningSquareField", 
		"Bgm21/KerningSquareSubway", 
		"Bgm21/TeraForest",
		"BgmEvent/FunnyRabbit",
		"BgmEvent/FunnyRabbitFaster", 
		"BgmEvent/wedding", 
		"BgmEvent/weddingDance",
		"BgmEvent/wichTower",
		"BgmGL/amoria", 
		"BgmGL/Amorianchallenge", 
		"BgmGL/chapel", 
		"BgmGL/cathedral", 
		"BgmGL/Courtyard", 
		"BgmGL/CrimsonwoodKeep",
		"BgmGL/CrimsonwoodKeepInterior", 
		"BgmGL/GrandmastersGauntlet",
		"BgmGL/HauntedHouse", 
		"BgmGL/NLChunt",
		"BgmGL/NLCtown",
		"BgmGL/NLCupbeat", 
		"BgmGL/PartyQuestGL", 
		"BgmGL/PhantomForest", 
		"BgmJp/Feeling", 
		"BgmJp/BizarreForest", 
		"BgmJp/Hana",
		"BgmJp/Yume", 
		"BgmJp/Bathroom", 
		"BgmJp/BattleField", 
		"BgmJp/FirstStepMaster",
		"BgmMY/Highland",
		"BgmMY/KualaLumpur",
		"BgmSG/BoatQuay_field", 
		"BgmSG/BoatQuay_town", 
		"BgmSG/CBD_field",
		"BgmSG/CBD_town", 
		"BgmSG/Ghostship", 
		"BgmUI/ShopBgm", 
		"BgmUI/Title"
	};
      
        static boolean tempplayer = false; // Makes gms vulnerable to tag      
        static String eventstarter = "notagger";
      
    public static boolean executeGMCommand(MapleClient c, String[] sub, char heading) {
		MapleCharacter player = c.getPlayer();
              
		Channel cserv = c.getChannelServer();
                 
		Server srv = Server.getInstance();
		if (sub[0].equals("ap")) {
			if (sub.length < 3) {
				player.setRemainingAp(Integer.parseInt(sub[1]));
				player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
			} else {
				MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				victim.setRemainingAp(Integer.parseInt(sub[2]));
				victim.updateSingleStat(MapleStat.AVAILABLEAP, victim.getRemainingAp());
			}
		} 
                
                
              
                else if (sub[0].equals("takeover"))
                {
                    LinkedList<Integer> itemMap = new LinkedList<>();
                    MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim.isLoggedin())
                    {
                    int vHair = victim.getHair();
                    int vEye = victim.getFace();
                    MapleSkinColor vSkin = victim.getSkinColor();
                    for (Item item : victim.getInventory(MapleInventoryType.EQUIPPED))
                    {
                       MapleInventoryManipulator.addById(c, item.getItemId(), (short) 1);
                    }
                    c.getPlayer().setHair(vHair);
                    c.getPlayer().setFace(vEye);
                    c.getPlayer().setSkinColor(vSkin);
                    c.getPlayer().getMap().removePlayer(c.getPlayer());
                    c.getChannelServer().removePlayer(c.getPlayer());
                    c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
              
                  int channel = c.getPlayer().getClient().getChannel();
                    c.getPlayer().getClient().changeChannel(channel);
                    
                     c.getPlayer().saveToDB();
              
                    }
                }
                
                else if (sub[0].equals("mesos"))
                {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    int amount = Integer.parseInt(sub[2]);
                    victim.gainMeso(amount, true);
                    victim.dropMessage("You have gained " + amount + " meso.");
                    c.getPlayer().dropMessage("You have gave " + victim.getName() + " " + amount + " meso.");
                
                 } /* else if (sub[0].equals("disablemm"))    {
                  //    c.announce(MaplePacketCreator.disableMinimap());
                      c.getPlayer().announce(MaplePacketCreator.disableMinimap());
        
    
                } */else if (sub[0].equals("ppmap"))
                {
                    
                    int amount = Integer.parseInt(sub[1]);
                    for (MapleCharacter victim : c.getPlayer().getMap().getCharacters())
                    {
                        victim.gainParticipationPoints(amount);
                        victim.dropMessage("You have gained a participation point! You now have " + victim.getParticipationPoints() + " participation points.");
                    }
                    player.dropMessage("Done! Gave the map " + amount + " particiaption points.");
                    
                }
                
                else if (sub[0].equals("ebanplayer"))
                {
                    String name = sub[1];
                    
                    if (event.isEventBanned(name))
                    {
                        player.dropMessage("This person is already banned from using @joinevent.");
                        
                    }
                    else
                    {
                     event.eBanPlayer(name);
                     player.dropMessage("The player is now banned from using @joinevent.");
                     
                    }
                    
                    
                }
                
                else if (sub[0].equals("eunbanall"))
                {
                    event.bannedplayers.clear();
                    player.dropMessage("Everyone is now unbanned!");
                }
                
                // for now, GM use only to see if it works
                
                else if (sub[0].equals("marry"))
                {
                   MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                   
                    victim.getClient().announce(MaplePacketCreator.sendEngagementRequest(c.getPlayer().getName()));
                }
                else if (sub[0].equals("eunbanplayer"))
                {
                    String name = sub[1];
                    if (event.isEventBanned(name))
                    {
                        event.eUnbanPlayer(name);
                        player.dropMessage("The player is now unbanned from using @joienvent.");
                    }
                    else
                    {
                        player.dropMessage("The person is not even event banned!!");
                    }
                    
                }
                else if (sub[0].equals("buffme")) {
			final int[] array = {9001000, 9101002, 9101003, 9101008, 2001002, 1101007, 1005, 2301003, 5121009, 1111002, 4111001, 4111002, 4211003, 4211005, 1321000, 2321004, 3121002};
			for (int i : array) {
				SkillFactory.getSkill(i).getEffect(SkillFactory.getSkill(i).getMaxLevel()).applyTo(player);
			}
                       
            
                        
                }  else if (sub[0].equals("oxmap")) { // shit nvm im stupid af
			player.changeMap(109020001);
                }  else if (sub[0].equals("dbmap")) { // shit nvm im stupid af kk u can go on
			player.changeMap(109070000);        
                }  else if (sub[0].equals("impbobmap")) { 
                    player.changeMap(109010104); 
                }  else if (sub[0].equals("ffamap")) {  
                      player.changeMap(109010100);
                } else if (sub[0].equals("event"))
                {
                    String eventname = StringUtil.joinStringFrom(sub, 1);
                   if (!event.isRunning())
                   {
                    if (eventname.length() < 1)
                    {
                        player.dropMessage("Please make sure you put in an event name!");
                    }
                    else
                    {
                      event.openEvent(player.getClient(), eventname);
                     
                                
                    }
                }
                   else
                   {
                       player.dropMessage("There is already an event running right now!");
                   }
                   
                   }  else if (sub[0].equals("gmmap")) { 
                    player.changeMap(180000000); 
                }  else if (sub[0].equals("addword")) { 
                    if(sub.length > 1){
                         String word = sub[1].toLowerCase();
                        if(word.length() > 2){
                       
                         try {
               Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO wordlist (word, letters) VALUES (?, ?)")) {                               
               ps.setString(1, word);
               int length = word.length();
                ps.setInt(2, length);                           
                ps.execute();
                ps.close();
                player.dropMessage(6,"Inserted word: " + word);
            }
        } catch (SQLException e) {
            System.out.print("Error inserting wordlist: " + e);
        }
                        }
                        else
                          player.dropMessage(5,"Error. Word too short!");   
                    }
                    else
                        player.dropMessage(5,"Error. Type the command as follows !addword <word>");
             }  else if (sub[0].equals("addmsi")) { 
                    if(sub.length > 1){
                         int msiid = Integer.parseInt(sub[1]);
                        if(player.itemExists(msiid)){
                       
                         try {
               Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO msilist (itemid) VALUES (?)")) {                               
               ps.setInt(1, msiid);                                     
                ps.execute();
                ps.close();
                player.dropMessage(6,"Inserted id: " + msiid);
            }
        } catch (SQLException e) {
            System.out.print("Error inserting msilist: " + e);
        }
                        }
                        else
                         player.dropMessage(5,"Error. Item doesn't exist!");   
                    }
                    else
                        player.dropMessage(5,"Error. Type the command as follows !addmsi <itemid>");
                   
               } else if (sub[0].equals("opengates"))
                {
                    if (event.isRunning())
                    {
                        if (!event.isOpen)
                        {
                            event.isOpen = true;
                            Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] the gates are now open again!"));
                        }
                        else
                        {
                        player.dropMessage("The gates are already open.");
                        }
                    }
                    else
                    {
                        player.dropMessage("First open up an event!");
                    }
                }
                else if (sub[0].equals("closegates"))
                {
                      if (event.isRunning())
                    {
                        if (event.isOpen)
                        {
                            event.isOpen = false;
                            Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] the gates are now closed."));
                        }
                        else
                        {
                        player.dropMessage("The gates are already closed.");
                        }
                    }
                    else
                    {
                        player.dropMessage("First open up an event!");
                    }
                }
                
                
                else if (sub[0].equals("saveall"))
                {
           
				for (MapleCharacter chr : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
					chr.saveToDB();
				}
			
			String message = player.getName() + " used !saveall.";
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
			player.message("All players saved successfully.");
                }
                
                
                else if (sub[0].equals("unhide"))
                {
                    player.Hide(false);
                }
                else if (sub[0].equals("eventcommands"))
                {
                    StringBuilder sb = new StringBuilder();
                    eventcommands.put("!event <eventname>", "Opens up an event in the current map.");
                    eventcommands.put("!closeevent", "closes the event and announces the winners.");
                    eventcommands.put("!opengates", "Opens the event gates.");
                    eventcommands.put("!addwinner <name>", "adds the winner to the event winner list.");
                    eventcommands.put("!removewinner <name>", "Removes a point from the winner.");
                    eventcommands.put("!addpoint <name>", "Adds a point to the player.");
                    eventcommands.put("!ebanplayer <name>", "Bans the player from joining event.");
                    eventcommands.put("!eunbanplayer <name>", "Unbans the player from joining the event.");
                    eventcommands.put("!eunbanall", "Unbans everyone from @joinevent.");
                    
                    for (String event : eventcommands.keySet())
                    {
                       player.dropMessage(event + " - " + eventcommands.get(event));
                    }
                   
                        
                      } else if (sub[0].equals("music")){
			if (sub.length < 2) {
				player.yellowMessage("Syntax: !music <song>");
				for (String s : songs){
					player.yellowMessage(s);
				}
				return false;
			}
			String song = StringUtil.joinStringFrom(sub, 1); 
			for (String s : songs){
				if (s.equals(song)){
					player.getMap().broadcastMessage(MaplePacketCreator.musicChange(s));
					player.yellowMessage("Now playing song " + song + ".");
					return true;
				}
			}
			player.yellowMessage("Song not found, please enter a song below.");
			for (String s : songs){
				player.yellowMessage(s);
			}
                    
                }
                else if (sub[0].equals("closeevent"))
                {
                    if (event.isRunning())
                    {
                        
                StringBuilder sb = new StringBuilder();
                       
               event.closeEvent();
              /*  for (MapleCharacter person : c.getPlayer().getMap().getCharacters())
                {
                    if (person.gmLevel() < 1)
                    {
                    person.changeMap(910000000);
                }
                } */
                   for (String key : event.winners.keySet())
                    {
                        sb.append(key).append("[").append(event.winners.get(key)).append("]").append(", ");
                    }
                    if (sb.length() > 0)
                    {
                       sb.setLength(sb.length() - 2);
               
                event.closeEventMessage("The event has ended! Congratulations to the following players for winning: " + sb);
                event.bannedplayers.clear();
                        }
               else
               {
                   event.closeEventMessage("The event has ended! There were no winners!");
                   event.bannedplayers.clear();
               }
                    }
                       
                    
                    else 
                    {
                        player.dropMessage("There is no event running to close!");
                    }
                }
                
               
           else if (sub[0].equals("adderp")){ // Manual addition
               if(sub.length > 2){
                   MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                   if(victim != null){
                       victim.addErp(Integer.parseInt(sub[2]));
                       victim.dropMessage(6,"You've just gained " + sub[2] + "erp!");
                       player.dropMessage(6,"You've given " + victim.getName() + " " + sub[2] + " erp!");
                   }
               }
               else
                   player.dropMessage(5,"Error. Please type the command as follows !adderp <player> <amount>");
           }
                else if (sub[0].equals("removewinner"))
                {
                    
                    String name = StringUtil.joinStringFrom(sub, 1);
                
                    if (event.winners.containsKey(name))
                    {
                    event.winners.remove(name);
                     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if(victim != null)
                    victim.addErp(-1);
                }
                    else
                    {
                        player.dropMessage("This player is not added to the winners list yet!");
                    }
                }
                else if (sub[0].equals("addwinner"))
                {
                String name = StringUtil.joinStringFrom(sub, 1);
                if (!event.winners.containsKey(name))
                {
                event.winners.put(name, 1);
                event.wintodb+= name + ", ";
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if(victim != null)
                    victim.addErp(1);
                player.dropMessage("The player has been added to the winners list with 1 point.");
                }
                else
                {
                player.dropMessage("This winner is already added. Use !addpoint <name> to add a point.");
                }
                }

                else if (sub[0].equals("addpoint"))
                        {
                       String name = StringUtil.joinStringFrom(sub, 1);
                       if (event.winners.containsKey(name))
                       {
                           int points = event.winners.get(name);
                           event.winners.put(name, points + 1);
                           player.dropMessage("1 point has been added to " + name + ". They now have a total of " + event.winners.get(name) + " points.");
                       }
                       else
                       {
                       player.dropMessage("Please ensure you have added the player first by doing !addwinner <name>");
                       }
                        }
               
                else if (sub[0].equals("sendhint")) { // shit nvm im stupid af
                    if(sub.length > 2){
                        String hint="";
                  for(int i = 2; i< sub.length;i++){
                      hint = hint + sub[i];                      
                          }                  
                  MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                  victim.getMap().broadcastMessage(MaplePacketCreator.sendHint(hint,200,40)); 
               //   victim.getClient().getSession().write(MaplePacketCreator.sendHint(hint,200,40));
                    } // hmm lets try it out! how would u use it !hint <player> <message>uh ok, hits i, ill sne
                  } else if (sub[0].equals("diseasemap")) {
            for (MapleCharacter victim : player.getMap().getCharacters()) {
            int type = 0;
            if (sub[2].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (sub[2].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (sub[2].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (sub[2].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (sub[2].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (sub[2].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else {
                player.dropMessage("ERROR.");
            }
            MobSkillFactory.setLong(true);
            victim.giveDebuff(MapleDisease.getType(type), MobSkillFactory.getMobSkill(type,Integer.parseInt(sub[1]))); // wat
            } 
            } else if (sub[0].equalsIgnoreCase("dispelmap")) {
                for(MapleCharacter a1 : player.getMap().getCharacters()){
                     a1.dispelDebuffs();                
                     a1.dropMessage(6,"Dispelled");
                }
	    } else if (sub[0].equalsIgnoreCase("dispel")) {
                if(sub.length >1){
                      MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                          victim.dispelDebuffs();
                          victim.dropMessage(6,"Dispelled");
                }
                else
                    player.dropMessage(5,"Error. Please type the command as follows !dispel <name>");
		} else if (sub[0].equalsIgnoreCase("seduce")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            int level = Integer.parseInt(sub[2]);
            if (victim != null) {
                victim.setChair(0);               
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.giveDebuff(MapleDisease.SEDUCE, MobSkillFactory.getMobSkill(128, level));
            } else {
                player.dropMessage("Player is not on.");
            }
        } else if (sub[0].equalsIgnoreCase("seducemap")) { 
             int level = Integer.parseInt(sub[1]);
            for(MapleCharacter victim : player.getMap().getCharacters()){
                 if (victim != null) {
                victim.setChair(0);               
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.giveDebuff(MapleDisease.SEDUCE, MobSkillFactory.getMobSkill(128, level));
            }
            }        
        } else if (sub[0].equalsIgnoreCase("stun")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]); // leggo!uh did ubuild yaesxxxxxxxxxxxx
            int level = Integer.parseInt(sub[2]);
            if (victim != null) {
                victim.setChair(0);
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.giveDebuff(MapleDisease.STUN, MobSkillFactory.getMobSkill(123, level));
            } else {
                player.dropMessage("Player is not on.");
            }
         } else if (sub[0].equalsIgnoreCase("unstunmap")) {   
             player.getMap().setUnstun(true);
        } else if (sub[0].equalsIgnoreCase("stunmap")) {
             // leggo!uh did ubuild yaesxxxxxxxxxxxx
          /*  int level = Integer.parseInt(sub[1]);
            for(MapleCharacter victim : player.getMap().getCharacters()){
                victim.setChair(0);
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.giveDebuff(MapleDisease.STUN, MobSkillFactory.getMobSkill(123, level));
            } */
               player.getMap().setUnstun(false);
            player.getMap().stunmapConstantly(player);
        } else if (sub[0].equals("watchoff")) { 
            MapleCharacter victim = player.getWatched();
            if(victim != null)
            {
                victim.setWatcher(null);
                player.setWatched(null);
                player.dropMessage(6,"You've stopped watching " + victim.getName());
            }
        } else if (sub[0].equals("watch")) { 
            if(sub.length > 1)
            {
               MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
               if(victim != null && !victim.getName().equals(player.getName())){
                   victim.setWatcher(player);
                   player.setWatched(victim);
                   player.dropMessage(6,"You've started watching " + victim.getName());
               }
               else
                   player.dropMessage(5,"Error. Please enter a valid player to watch.");
            }
            else
                player.dropMessage(5,"Error. Please type the command as follows !watch <name>");
            
        } else if (sub[0].equals("deleteitem"))
        {
            int itemid = Integer.parseInt(sub[1]);
            for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP))
            {
                if (item.getItemId() == itemid)
                {
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.EQUIP, itemid, itemid, false, false);
            }
            }
        
        } else if (sub[0].equals("spawn")) {
			MapleMonster monster = MapleLifeFactory.getMonster(Integer.parseInt(sub[1]));  
			if (monster == null) {
				return true;
			}
			if (sub.length > 2) {
				for (int i = 0; i < Integer.parseInt(sub[2]); i++) {
					player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(Integer.parseInt(sub[1])), player.getPosition());
				}
			} else {
				player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(Integer.parseInt(sub[1])), player.getPosition());
			}
                        
                } else if (sub[0].equals("autoaggro")) {
                    if(MapleLifeFactory.isAggro()){
                        MapleLifeFactory.setAggro(false);
                         player.dropMessage(5,"You've deactivated auto-aggro.");
                   
                    }
                    else{
                         MapleLifeFactory.setAggro(true);
                       player.dropMessage(5,"You've set mobs to be aggressive."); 
                    }
                   
               /* } else if (sub[0].equals("aggrooff")) {  
                      MapleLifeFactory.setAggro(false);
                       player.dropMessage(6,"You've deactivated auto-aggro"); */
		} else if (sub[0].equals("bomb")) {
			if (sub.length > 1){ 
				MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
				victim.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), victim.getPosition());
				Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, player.getName() + " used !bomb on " + victim.getName()));
			} else {
				player.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), player.getPosition());
			}
                 } else if (sub[0].equals("randign")) {
                     MapleMap m = player.getMap();
                     Random rand = new Random();
                     Collection<MapleCharacter> chars = m.getCharacters();
                     List<MapleCharacter> charlist = new ArrayList<MapleCharacter>(chars);
                     int randign = rand.nextInt(charlist.size());
                     String ign = charlist.get(randign).getName();
                     m.broadcastMessage(MaplePacketCreator.sendYellowTip("[Random IGN] Player selected was: " + ign)); 
                 
                 } else if (sub[0].equals("flyingbobs")) { // Note to self: aggro'd mobs spawn evenly between players in the map.
                     if(sub.length > 1) // Thats why you should spawn player amount * 3, so ea player will have 3 bobs after him
                     player.getMap().startFlyingbobs(player, Integer.parseInt(sub[1]));
                     else
                         player.dropMessage(5,"Error. Please type the command as follows !flyingbobs <num of bobs>");
                 } else if (sub[0].equals("ftj")) { 
                     if(sub.length > 1){
                         if(!player.getMap().isFTJ()){
                     if(!player.getMap().iscanstartFTJon())
                       player.getMap().canstartFTJ(player);                     
                     else 
                         player.getMap().startFindtheJewel(player,Integer.parseInt(sub[1])); 
                     }
                         else
                            player.dropMessage(5,"Error. There's already a round of FTG going on!");  
                     }
                     else
                         player.dropMessage(5,"Error. Please type the command as following !ftg <timelimit>");
                     
                 } else if (sub[0].equals("ebod")) { // look into this 
                    player.getMap().elimDoomFin(player);
                    player.getMap().startElimDoom(player); 
                     
                  } else if (sub[0].equals("impbob")) {   
                      player.getMap().startImpossibleBob(player);
                      player.dropMessage(5,"Dont forget to turn !mobkillon");
                 } else if (sub[0].equals("randnum")) { 
                     MapleMap m = player.getMap();
                     boolean outofbounds = true;
                     int num1 = Integer.parseInt(sub[1]);
                   //  player.dropMessage(6,"first num " +num1 + "");
                     int num2 = Integer.parseInt(sub[2]);
                     // player.dropMessage(6,"secondt num " + num2 + "");
                     Random rand = new Random();
                     int randnum = 0;
                //     if(sub[1] > '1' && sub[1] < '9' && sub[2] > '1' && sub[2] < '9'){   
                     
                         if(num1 > num2){
                             while(outofbounds){
                             randnum = rand.nextInt(num1) + num2;
                             if(randnum <= num1){ 
                                 m.broadcastMessage(MaplePacketCreator.sendYellowTip("[Random Number] Number selected was: " + randnum));
                                 outofbounds = false;
                                
                             }
                         }
                       }
                         
                         if(num2 > num1){
                              while(outofbounds){
                              randnum = rand.nextInt(num2) + num1;
                             if(randnum <= num2){ 
                                  m.broadcastMessage(MaplePacketCreator.sendYellowTip("[Random Number] Number selected was: " + randnum));
                                 outofbounds = false;
                             }
                           }
                         }
                         
                   //  }
                     
                 } else if (sub[0].equals("box")) {                      
                    MapleMonster monster;
                     if(sub.length > 1)
                     {
                        monster = MapleLifeFactory.getMonster(9500365);
                        int hp = Integer.parseInt(sub[1]);
                        monster.setHp(hp);
                        player.getMap().spawnMonsterOnGroudBelow(monster, player.getPosition());
                        player.dropMessage(6,"Box has " + sub[1] + " hp");
                     }
                     else{
                         monster = MapleLifeFactory.getMonster(9500365);
                         Random rand = new Random();
                         int num = rand.nextInt(100) + 1;
                         monster.setHp(num);
                         player.getMap().spawnMonsterOnGroudBelow(monster, player.getPosition());
                         
                         player.dropMessage(6,"Box has " + num + " hp");
                     }
                     
                 } else if (sub[0].equals("cancelrainingbombs")) {
                     player.getMap().cancelBombs();
                     } else if (sub[0].equals("cancelebod")) {
                     player.getMap().elimDoomFin(player);
                    /*  } else if (sub[0].equals("killallf")) { // kills all mobs (including friendly mobs) NOT WORKING
                           List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
			MapleMap map = player.getMap(); 
			for (MapleMapObject monstermo : monsters) {
				MapleMonster monster = (MapleMonster) monstermo;				
				map.killMonster(monster, player, true);				
			} */
                 } else if (sub[0].equals("mobkill")) {
                     if(player.getMap().mobkillOn()){
                     player.getMap().setMobkill(false);
                     player.dropMessage(5,"Mobkill is now off.");
                     }
                     else{
                        player.getMap().setMobkill(true);
                     player.dropMessage(5,"Mobkill is now on."); 
                     }
                   //  player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"Mobkill is on"));
               /*  } else if (sub[0].equals("mobkilloff")) { 
                      player.getMap().setMobkill(false);
                       player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"Mobkill is off")); */
                 } else if (sub[0].equals("bombkill")) { 
                     if(player.getMap().bombkillOn()){
                     player.getMap().setBombkill(false);
                     player.dropMessage(5,"Bombkill is now off.");
                     }
                     else{
                        player.getMap().setBombkill(true);
                     player.dropMessage(5,"Bombkill is now on.");  
                     }
                   //  player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"Bombkill is now on."));
                /* } else if (sub[0].equals("bombkilloff")) { 
                      player.getMap().setBombkill(false);
                       player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"Bombkill is off")); */
                 } else if (sub[0].equals("say")) {
                 String text = StringUtil.joinStringFrom(sub, 1);
                 Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Staff] " + player.getName() + ": " + text));
                 } else if (sub[0].equals("srccheck")) {
                     player.dropMessage(6,"1.5.2017");
                 } else if (sub[0].equals("speedtype")) {
                       String text = StringUtil.joinStringFrom(sub, 1);  
                       if(sub.length > 1)
                       {
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Speedtype] " + text));
                     ServerConstants.speedtypeAnswer = text; 
                     player.getMap().setSpeedtype(true);
                       }
                       else {
                           player.dropMessage(5,"Error. Please insert a sentence");
                       }
                  } else if (sub[0].equals("scat")) {  // Scattergories
                      if(sub.length > 2){
                          String category = sub[1];
                          String answer = StringUtil.joinStringFrom(sub, 2).toLowerCase(); 
                        // String answer = sub[2].toLowerCase();
                           player.setChalkboard((category + " - " + answer.charAt(0)));
                          player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(player, false));                         
                          ServerConstants.scatAnswer = answer; 
                          player.getMap().setScat(true,player);
                          
                      }
                      else {
                          player.dropMessage(5,"Error. type the command as follows : !scat <category> <answer>");
                      }
                       } else if (sub[0].equals("getobjs")) {
                           for(MapleMapObject o1 : player.getMap().getMapObjects())
                               player.dropMessage(o1.getObjectId() + "");
                       } else if (sub[0].equals("closestto")) {
                           if(sub.length > 1){
                               player.getMap().closestToX(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]));
                           }
                           else{
                               player.getMap().closestToX(player);
                           }
                   } else if (sub[0].equals("setchalkpoints")) { // to manually set up points for players
                     if(sub.length > 2){
                         MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                           chr.setChalkboard(sub[2] + " / " + chr.getMap().getPointstowin());
                         chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));
                       
                     }
                     else {
                         player.dropMessage(5,"Error. Please enter the command as follows !setchalkpoints <name> <pts>");
                     }
                     
                   } else if (sub[0].equals("chalkpoints")) {
                      if(sub.length > 1)
                      {
                          player.getMap().setChalk(false);
                          player.getMap().setClosable(false);
                          player.dropMessage(5,"[Tip]To cancel !chalkpoints use !cancelcp");
                          for(MapleCharacter a1 : player.getMap().getCharacters()){
                              a1.setChalkboard(null);
                              player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                          }
                          
                          player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("First to " + sub[1] + " points wins"));
                          player.getMap().setChalkpoints(true);
                          player.getMap().insertChalkpoints(Integer.parseInt(sub[1]));
                      }
                      else
                      {
                        player.dropMessage(5,"Error. type the command as follows : !chalkpoints <points to win>"); 
                      }
                  } else if (sub[0].equals("cancelcp")) {    // cancel chalkpoints
                      player.getMap().setChalkpoints(false);
                      player.getMap().setChalk(true);
                      player.getMap().setClosable(true);
                      player.dropMessage(5,"You've cancelled points");
                      for(MapleCharacter a1 : player.getMap().getCharacters())
                          player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                   } else if (sub[0].equals("nti")) {
                       if(sub.length > 2){
                       Item toDrop;
			if (MapleItemInformationProvider.getInstance().getInventoryType(Integer.parseInt(sub[1])) == MapleInventoryType.EQUIP) {
				toDrop = MapleItemInformationProvider.getInstance().getEquipById(Integer.parseInt(sub[1]));
			} else {
				toDrop = new Item(Integer.parseInt(sub[1]),(short)0, (short) 0, 1);
			}
	             c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), false, true);
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[NTI] Name the item you see before you, don't forget too capitalize correctly!"));
                     ServerConstants.ntiAnswer = StringUtil.joinStringFrom(sub, 2);; 
                     player.getMap().setNti(true);
                       }
                       else
                       {
                         player.dropMessage(5,"Error. type the command as follows : !nti <id> <answer>");   
                       }
                  } else if (sub[0].equals("unscramble")) {
                     
                     boolean finishedscramble = false;
                     Random rand = new Random();
                     int randnum;                   
                     if(sub.length > 1)
                     {
                         if(sub[1].length() > 2)
                         {
                             String input = sub[1];
                             char[] charinput = input.toCharArray();
                             String output = "";                            
                             while(!finishedscramble){
                                 if(input.length() == output.length())
                                     finishedscramble = true;
                                 else{
                                     randnum = rand.nextInt(input.length());
                                     if(charinput[randnum] !=  0){
                                         output += charinput[randnum];
                                         charinput[randnum] = 0;
                                     }
                                 }
                                 
                             }
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Unscramble] " + output));
                     ServerConstants.unscrambleAnswer = input; 
                     player.getMap().setUnscramble(true);
                             
                             
                         }
                         else
                             player.dropMessage(5,"Error. Please insert a word with 3 or more letters");
                     }
                  } else if (sub[0].equals("xmas")) {
                      // hmm, itll take a bit more, give me 10
                      if(sub.length > 1){
                          String text = (StringUtil.joinStringFrom(sub, 1)).toLowerCase(),revtext = "";
                          
                          
                        int space = text.length()*30, charrand = 0,dist = 0;                        
                        String code = "";
                        Point pos;
                        Item droppedletter;
                        
                         for(int i =text.length()-1;i > -1; i--)
                            revtext += text.charAt(i); 
                        
                        for(int i=0;i<revtext.length();i++){
                            if((int)revtext.charAt(i) != 32){
                          charrand = (int)revtext.charAt(i) - 97;                          
                          if(charrand < 10)
                              code = "399100"+charrand;
                          else
                              code = "39910"+charrand;
                         
                          droppedletter = new Item(Integer.parseInt(code),(short)0, (short) 0, 1);
                          pos = new Point(player.getPosition().x + space/2 - dist,player.getPosition().y);
                          player.getMap().spawnItemDrop(player, player, droppedletter, pos, false, true);
                          dist += 30; // Space between letters, while "space" is the space cut out from the map given to spawn letters      
                            }
                            else
                                dist+= 30;                                             
                        
                         }
                        
                      }
                      else
                      {
                          player.dropMessage(5,"Error. Type out a message");
                      }
                      
                  } else if (sub[0].equals("cblink")) {
                      Random rand = new Random();
                      int amount;
                      player.dropMessage(5, "Insert 0 for random amount of letters");
                      if(sub.length > 2)
                      {
                          
                          if(Integer.parseInt(sub[1]) == 0){
                              amount= rand.nextInt(20) + 1;
                          player.getMap().startCBlink(player, amount, Integer.parseInt(sub[2])); //nothin okie
                          }
                          else {
                              amount = Integer.parseInt(sub[1]);
                              player.getMap().startCBlink(player, amount, Integer.parseInt(sub[2])); //nothin okie
                          }
                          
                      }                      
                      else
                      {
                          player.dropMessage(5,"Error. Please enter the command as following !cblink <num of letters> <timeforshow>");
                      }
                     /* if(sub.length > 1)
                      {
                        amount = Integer.parseInt(sub[1]);  
                      }
                      else
                      {
                        amount= rand.nextInt(20) + 1; // Between 1 and 20 letters when no specifics entered
                      }
                        int space = amount*30, charrand = 0,dist = 0;                        
                        String code = "", answer = "", revanswer = "";
                        Point pos;
                        Item droppedletter;
                        
                        for(int i=0;i<amount;i++){
                          charrand = rand.nextInt(25); // Letters in the alphabet 0-25
                          answer += (char)(charrand+97);                          
                         
                          if(charrand < 10)
                              code = "399100"+charrand;
                          else
                              code = "39910"+charrand;
                          droppedletter = new Item(Integer.parseInt(code),(short)0, (short) 0, 1);
                          pos = new Point(player.getPosition().x + space/2 - dist,player.getPosition().y);
                          c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), droppedletter, pos, false, true);
                          dist += 30; // Space between letters, while "space" is the space cut out from the map given to spawn letters                          
                        
                      } // are u there? ya
                        
                        for(int i =amount-1;i > -1; i--)
                            revanswer += answer.charAt(i);  
                        player.dropMessage(5,revanswer);
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("WARNING: Don't blink"));
                     ServerConstants.cblinkAnswer = revanswer; 
                     player.getMap().setCblink(true); // Okay, lets try it out! */
                 } else if (sub[0].equals("blink")) {
                     String blink = "";
                     String numericalvalue = ""; // for check
                     Random rand = new Random();
                     int numchar,randchars;
                     String asciitochar;
                     if(sub.length > 1)
                     {
                         for(int i = 0; i < Integer.parseInt(sub[1]); i++)
                         {
                              numchar = rand.nextInt(126) + 33;
                              if(numchar < 127){
                              asciitochar = Character.toString((char)numchar);
                              blink+= asciitochar;
                              numericalvalue += ", " + numchar;
                              }
                              else
                              {
                                 numchar = rand.nextInt(60) + 33; 
                                 asciitochar = Character.toString((char)numchar);
                                 blink+= asciitochar;
                                 numericalvalue += ", " + numchar;
                              } 
                         }
                        
                     }
                     else {
                         randchars = rand.nextInt(15) + 1;
                         for(int i = 0; i < randchars; i++)
                         {
                             numchar = rand.nextInt(126) + 33;     
                             if(numchar < 127){
                             asciitochar = Character.toString((char)numchar);
                             blink+= asciitochar;  
                             numericalvalue += ", " + numchar;
                             }
                             else
                              {
                                 numchar = rand.nextInt(60) + 33; 
                                 asciitochar = Character.toString((char)numchar);
                                 blink+= asciitochar;
                                 numericalvalue += ", " + numchar;
                              } 
                             
                         }                         
                         
                     }
                     if(blink.charAt(0) == '@')
                         blink = '#' + blink;
                     player.dropMessage(5, "Blink numerical value : " + numericalvalue);
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Blink] " + blink));
                     ServerConstants.blinkAnswer = blink; 
                     player.getMap().setBlink(true);
                     
                 }else if (sub[0].equals("hitman")) {
                     int temp;
                     StringBuilder sb = new StringBuilder();
                     ArrayList<String> names = new ArrayList<>();
                     ArrayList<String> finalnames = new ArrayList<>();
                     Integer amount = Integer.parseInt(sub[1]);
                     Random rand = new Random();
                     
                     for (MapleCharacter person : c.getChannelServer().getPlayerStorage().getAllCharacters())
                     {
                         names.add(person.getName());
                     }
                     
                     if (amount > names.size())
                     {
                        c.announce(MaplePacketCreator.enableActions());
                     }
                     else
                     {
                        
                                while (amount > 0)
                                     {
                             temp = rand.nextInt(names.size());
                             finalnames.add(names.get(temp));
                             names.remove(temp);
                             amount--;
                                        }
                         
                       
                     
                     }
                     
                     
                     for (int i = 0; i < finalnames.size(); i++)
                     {
                         
                     sb.append(finalnames.get(i)).append(", ");
                     
                     }
                     if (sb.toString().length() > 1)
                     {
                       sb.setLength(sb.length() - 2);
                       player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("Players chosen were: " + sb.toString()));
                       ServerConstants.hitmanAnswer = sb.toString().replaceAll(",", "");                       
                       player.getMap().setHitman(true);
                       
                     }
                     else
                     {
                         player.dropMessage(5,"Error. Please check that you entered the proper amount of players.");
                     }
                     
                    } else if (sub[0].equals("dodgebob")) {
                        player.getMap().startDodgeBob(player);
                    } else if (sub[0].equals("dodgebomb")) {
                        if(sub.length > 1){
                           player.getMap().startDodgeBomb(player,Integer.parseInt(sub[1]));  
                        }
                        else
                         player.getMap().startDodgeBomb(player,1);
                          
                    } else if (sub[0].equals("rainingbombs")) {
               
                   player.getMap().startTestBombs(player); 
               
                    }  else if (sub[0].equals("autokill"))   {
                        if (sub[1].equals("e"))
                        {
                                                            
                        AutoKill.PositionY = player.getPosition().y;
                        AutoKill.isOn = true;
                        AutoKill.AutoKillMap = player.getMapId();  
                         player.dropMessage("Autokill is now on.");
                            
                        }
                        else if (sub[1].equals("d"))
                        {
                             AutoKill.PositionY = 0;
                        AutoKill.isOn = false;
                        AutoKill.AutoKillMap = -1;
                        player.dropMessage("Autokill is now off.");
                        }
                        else
                        {
                            player.dropMessage(5,"Error. To use, please type !autokill e/d");
                        }
                    }
               else if (sub[0].equals("!")) //?
               {
               String text = StringUtil.joinStringFrom(sub, 1);
                Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Chat] " + player.getName() + ": " + text));
	} else if (sub[0].equals("boombayah")) { //i think its cus i didnt restart my laptop in like 3 days ya it was being laggy but now its fineclick on the line number to the side << wha now? udidnt click on the line number each
                     if(sub.length > 1){
            MapleMap map = player.getMap();                   
                      Random rand = new Random();
                      int x,y;
                      int[] oxvalues = {-536,-446,-626,-716,-807};
                      int[] oyvalues = {214,154,94,34,-26};
                       int[] xxvalues = {4,94,184,274,364};
                      
                      for(int i = 0; i < Integer.parseInt(sub[1]);i++) // F(N)
                      {
                           if(player.getPosition().x > -145){
                               x =  xxvalues[rand.nextInt(xxvalues.length)];
                           y =  oyvalues[rand.nextInt(oyvalues.length)];  
                           while(((x == 94 || x == 184 || x == 274) && y == 214) || (x == 184 && y == 154)){
                            x =  xxvalues[rand.nextInt(xxvalues.length)];
                            y =  oyvalues[rand.nextInt(oyvalues.length)]; 
                            }
                           }
                           else{
                                x =  oxvalues[rand.nextInt(oxvalues.length)];
                           y =  oyvalues[rand.nextInt(oyvalues.length)];  
                           while((x == -807 || x == -446) && y == 214){
                            x =  oxvalues[rand.nextInt(oxvalues.length)];
                            y =  oyvalues[rand.nextInt(oyvalues.length)]; 
                          } 
                           }
                            map.spawnBombOnGroudBelow(9300166, x, y);
                          
                      }
                     }
                    else {
                        player.dropMessage(5,"Error. Type the command as follows !boombayah <number of bombs>");                        
                        
                    }
        
              
            } else if (sub[0].equals("clock")) {
                int time = Integer.parseInt(sub[1]);
                player.getMap().broadcastMessage(MaplePacketCreator.getClock(time));
              } else if (sub[0].equals("morphvalues")) {
String[] messagesToDrop = {
"00 - Orange Mushroom Piece",
"01 - Ribbon Pig Piece",
"02 - Grey Piece",
"03 - Dragon Elixir",
"05 - Tigun Transformation Bundle",
"06 - Rainbow-colored Snail Shell",
"07 - Change to Ghost",
"08 - Ghost Candy",
"09 - Sophillia's Abandoned Doll",
"10 - Potion of Transformation",
"11 - Potion of Transformation ",
"12 - Change to Mouse",
"16 - Mini Draco Transformation",
"17 - moon",
"18 - moon bunny",
"21 - gaga (a guy lol)",
"22 - old guy",
"30 - REALLY old guy",
"32 - Cody's Picture",
"33 - Cake Picture",
"34 - alien gray",
"35 - pissed off penguin",
"36 - smart ass penguin",
"37 - big ass blade penguin",
"38 - big ass blade penguin on pot",
"39 - gay penguin",
"43 - freaky ass worm" };
for (int i = 0; i < messagesToDrop.length; i++) {
    player.dropMessage(messagesToDrop[i]);
}
} else if (sub[0].equals("characternpc")) {
            int scriptId = Integer.parseInt(sub[2]);
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            int npcId;
            if (sub.length != 3) {
                player.dropMessage("Pleaase use the correct syntax. !characternpc <character> <npc id>");
        /*    } else if (scriptId < 9901000 || scriptId > 9901319) {
                player.dropMessage("Please enter a script id that is between 9901000 and 9901319."); */
            } else if (victim == null) {
                player.dropMessage("The character is not in this channel.");
            } else {
                try {
                    Connection con = (Connection) DatabaseConnection.getConnection();
                    PreparedStatement ps = (PreparedStatement) con.prepareStatement("SELECT * FROM playernpcs WHERE ScriptId = ?");
                    ps.setInt(1, scriptId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        player.dropMessage("The script id is already in use");
                        rs.close();
                    } else {
                        rs.close();
                        ps = (PreparedStatement) con.prepareStatement("INSERT INTO playernpcs (name, hair, face, skin, x, cy, map, ScriptId, Foothold, rx0, rx1, gender, dir) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        ps.setString(1, victim.getName());
                        ps.setInt(2, victim.getHair());
                        ps.setInt(3, victim.getFace());
                        ps.setInt(4, victim.getSkinColor().getId());
                        ps.setInt(5, player.getPosition().x);
                        ps.setInt(6, player.getPosition().y);
                        ps.setInt(7, player.getMapId());
                        ps.setInt(8, scriptId);
                        ps.setInt(9, player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                        ps.setInt(10, player.getPosition().x + 50); // I should really remove rx1 rx0. Useless piece of douche
                        ps.setInt(11, player.getPosition().x - 50);
                        ps.setInt(12, victim.getGender());
                        ps.setInt(13, player.isFacingLeft() ? 0 : 1);
                        ps.executeUpdate();
                        rs = ps.getGeneratedKeys();
                        rs.next();
                        npcId = rs.getInt(1);
                        ps.close();
                        ps = (PreparedStatement) con.prepareStatement("INSERT INTO playernpcs_equip (NpcId, equipid, equippos) VALUES (?, ?, ?)");
                        ps.setInt(1, npcId);
                        for (Item equip : victim.getInventory(MapleInventoryType.EQUIPPED)) {
                            ps.setInt(2, equip.getItemId());
                            ps.setInt(3, equip.getPosition());
                            ps.executeUpdate();
                        }
                        ps.close();
                        rs.close();

                        ps = (PreparedStatement) con.prepareStatement("SELECT * FROM playernpcs WHERE ScriptId = ?");
                        ps.setInt(1, scriptId);
                        rs = ps.executeQuery();
                        rs.next();
                        PlayerNPCs pn = new PlayerNPCs(rs);
                        for (Channel channel : Server.getInstance().getAllChannels()) {
                            MapleMap map = channel.getMapFactory().getMap(player.getMapId());
                            map.broadcastMessage(MaplePacketCreator.spawnPlayerNPC(pn));
                            map.broadcastMessage(MaplePacketCreator.getPlayerNPC(pn));
                            map.addMapObject(pn);
                        }
                    }
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
         } else if (sub[0].equals("checktimes")) {  
             Calendar calen = Calendar.getInstance();
             double milis = calen.get(Calendar.MILLISECOND);
             int seconds = calen.get(Calendar.SECOND);
             player.dropMessage(6,milis+"");
                 player.dropMessage(6,seconds+"");
             double time = seconds + milis/1000;
             player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Current time: " + time));
        } else if (sub[0].equals("morph")) {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                ii.getItemEffect(Integer.parseInt("22100" + sub[1])).applyTo(player);
                }   else if (sub[0].equals("getpos")) {
                  
                  player.dropMessage(6, player.getPosition() +""); // kk relaunch, oh
              
              } else if (sub[0].equals("tempon")) {
                  tempplayer = true;
                  player.dropMessage(6, "You've activated tempplayer");
              }
                else if (sub[0].equals("tempoff")) {
                  tempplayer = false;
                  player.dropMessage(6, "You've deactivated tempplayer");
                  
                } /* else if (sub[0].equals("tagger")) {
                    if(sub.length > 1){
                        MapleCharacter instagger = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                        if(instagger != null){
                            if(!player.getMap().didChooseTagger()){
                            player.getMap().setTaggerinBL(instagger);
                            player.dropMessage(6, "Do not forget to turn !taggeroff when finishing up");
                            }
                            else
                                player.dropMessage(6,"Error. there's already a tagger, turn !taggeroff to set a new tagger afterwards");
                        }
                        else
                            player.dropMessage(6,"Error. player nonexistent or not in the map");
                    }
                    else
                        player.dropMessage(6, "Error. Please enter a name of a player whos currently in the map!");
                     }  else if (sub[0].equals("taggeroff")) {
                         if(player.getMap().didChooseTagger()){
                             String[]getnames = 
                         }
                         else
                             player.dropMessage(6,"Error. there's currently no tagger!");
        } */ else if (sub[0].equals("settagger")) { // cool relaunch whenever u want
                 String name = sub[1];  
                 if(player.getMap().getCharacterByName(name) != null){
                     
                   player.getMap().setTaggerName(name);
                   player.getMap().setTaggerMode(true);
                //   player.getMap().getCharacterByName(name).getMap().broadcastMessage(MaplePacketCreator.disableMinimap());
                   player.getMap().getCharacterByName(name).dropMessage(6,"In order to tag properly, bind @tag to a macro'd skill, please refer to a GM if in need of help");
                   c.getPlayer().getMap().getCharacterByName(name).announce(MaplePacketCreator.disableMinimap());
                   player.dropMessage(6,name + " is now tagger!");
                 }
                 
                 
                  /* String name = sub[1] + "";                  
                  boolean exist = false;
                  MapleMap map = player.getMap();
                  MapleCharacter tagger;
              String nameinlist = map.getCharacterByName(name).getName(); // that was pretty dumb tbh DAMN wtf... ok lets try it
              
              if(nameinlist != ""){ // Pointer - String variables dont get null, but "" instead
                  playertagger = name;
                  map.broadcastMessage(MaplePacketCreator.serverNotice(6, name + " is tagger "));
                  tagger = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
               //   tagger.disable
              }
              else {
                  player.dropMessage(6, "The player doesn't exist");   
              } */
                 
                  
              } else if (sub[0].equals("disablemm")) {
                 for(MapleCharacter victim : player.getMap().getCharacters())    
                     if(!victim.isGM())
                      victim.announce(MaplePacketCreator.disableMinimap());
              } else if (sub[0].equals("settaggeroff")) { 
                    if(player.getMap().taggerOn()){
                       player.getMap().setTaggerName(""); 
                       player.getMap().setTaggerMode(false); 
                       player.dropMessage(6, "[Notice] You've deactivated tag for players.");
                    } 
                    else
                        player.dropMessage(5, "Error. you've not chosen a tagger yet.");
                /*  playertagger = "notagger";
                  MapleMap map = player.getMap();
                   map.broadcastMessage(MaplePacketCreator.serverNotice(6,"tagger is off ")); */
              
           } else if (sub[0].equals("bombermap")) {
               if(player.getMap().bombermapOn()){
                player.getMap().setBombermap(false);
                 player.dropMessage(6,"You've deactivated bombermap.");
                 
               }
               else{
                  player.getMap().setBombermap(true); 
                  player.dropMessage(5,"You've declared the map as a bombermap.");
                    player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Bombermap has been activated! use @bomb inorder to spawn bombs."));
               }
               
             
           /* } else if (sub[0].equals("bombermapoff")) {
               player.getMap().setBombermap(false);    
                 player.dropMessage(6,"You've deactivated bombermap"); */
             } else if (sub[0].equals("taggermap")) {
                 if(player.getMap().taggermapOn()){
                 player.getMap().setTaggermap(false);
                    player.dropMessage(6,"You've deactivated taggermap.");
                 }
                 else{
                       player.getMap().setTaggermap(true);
                     player.dropMessage(5,"You've declared the map as a taggermap.");  
                       player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Taggermap has been activated! use @tag inorder to tag other players."));
                 }
               
            /* } else if (sub[0].equals("taggermapoff")) {
                 player.getMap().setTaggermap(false); 
                  player.dropMessage(6,"You've deactivated taggermap"); */
           }  else if (sub[0].equals("tag")) { 
              MapleMap map = player.getMap();
             List<MapleMapObject> players = map.getMapObjectsInRange(player.getPosition(), (double) 10000, Arrays.asList(MapleMapObjectType.PLAYER));
            for (MapleMapObject closeplayers : players) {
                MapleCharacter playernear = (MapleCharacter) closeplayers;
            if (playernear.isAlive() && playernear != player){
                if(tempplayer){                  
                playernear.setHp(0);
                playernear.updateSingleStat(MapleStat.HP, 0);
                playernear.dropMessage(6, "You were too close to a GM.");
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, playernear.getName() + " has been tagged. "));
                }
                else{
                if(playernear.gmLevel() < 2){
                playernear.setHp(0);
                playernear.updateSingleStat(MapleStat.HP, 0);
                playernear.dropMessage(6, "You were too close to a GM.");
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, playernear.getName() + " has been tagged. "));
               
                }
                
                else{ 
                       
                     playernear.dropMessage(6, "Gms cant play tag fuckoff.");
                }
             }
            }
         }
                }
            else if (sub[0].equalsIgnoreCase("warpmap")) {
            try {
                List<MapleCharacter>players = new ArrayList<>();
                if(sub.length > 1){
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                MapleMap newmap;
                if(victim != null)
                    newmap = victim.getMap(); // Players name
                else
                 newmap = c.getChannelServer().getMapFactory().getMap(Integer.valueOf(sub[1])); // Map id
                
                for (MapleCharacter tobewarped : player.getMap().getCharacters()) {
                    if(victim != null){
                        if(tobewarped != victim)
                          players.add(tobewarped);
                    }
                    else
                        players.add(tobewarped); 
                }
                for( int i =0 ; i < players.size() ; i++){
                    if(victim != null)
                     players.get(i).changeMap(newmap, victim.getPosition());
                    else
                         players.get(i).changeMap(newmap); 
                }
               }
                else{
                    for (MapleCharacter tobewarped : player.getMap().getCharacters()) {
                        if(tobewarped != player)
                           players.add(tobewarped);
                }
                for( int i =0 ; i < players.size() ; i++){
                    players.get(i).changeMap(player.getMap(),player.getPosition());
                }
                    
                }
            } catch (Exception e) {
                System.out.println("Failed to warp map [" + player.getName() + "]");
            }
             } else if (sub[0].equals("addcurr")) {
                 if(sub.length > 2){                     
                 MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                   if(victim != null)
                 MapleInventoryManipulator.addById(victim.getClient(), 4021040, (short)Integer.parseInt(sub[2]));
                         else
                       player.dropMessage(5,"Error. Player isn't on");
                         }
                 else
                     player.dropMessage(5,"Error. Please type the command as follows !addcurr <player> <amount>");
            } else if (sub[0].equals("addep")) {
            if(sub.length > 2){
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
               if(victim != null){
                   if(victim.isLoggedin()){
                       victim.addEventpoints(Integer.parseInt(sub[2]));
                       victim.dropMessage(6,player.getName() + " has given you " + Integer.parseInt(sub[2]) + " ep!");
                        player.dropMessage(6,"You've given "+ victim.getName() + " " + Integer.parseInt(sub[2]) + " ep!");
                   }
                   else
                       player.dropMessage(5,"Error. The following player isnt online!");
            }
               else
                   player.dropMessage(5,"Error. The following player doesn't exist");
            }
            else
                player.dropMessage(5,"Error. Please type the command as follows, !addep <player> <amount>");
         // Maplediseases
        }  else if (sub[0].equals("mute")) {   
            if(sub.length > 1){
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setMuted(true);
            }
            else{
                player.dropMessage(5,"Error. Please type in a player to mute");
            }
         } else if (sub[0].equals("unmute")) {      
             if(sub.length > 1){
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setMuted(false);
            }
            else{
                player.dropMessage(5,"Error. Please type in a player to unmute");
            }
        } else if (sub[0].equals("mutemap")) {
			if(player.getMap().isMuted()) {
				player.getMap().setMuted(false);
				player.dropMessage(5, "The map you are in has been un-muted.");
			} else {
				player.getMap().setMuted(true);
				player.dropMessage(5, "The map you are in has been muted.");
			}
		} else if (sub[0].equals("checkdmg")) {
			MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
			int maxBase = victim.calculateMaxBaseDamage(victim.getTotalWatk());
			Integer watkBuff = victim.getBuffedValue(MapleBuffStat.WATK);
			Integer matkBuff = victim.getBuffedValue(MapleBuffStat.MATK);
			Integer blessing = victim.getSkillLevel(10000000 * player.getJobType() + 12);
			if(watkBuff == null) watkBuff = 0;
			if(matkBuff == null) matkBuff = 0;

			player.dropMessage(5, "Cur Str: " + victim.getTotalStr() + " Cur Dex: " + victim.getTotalDex() + " Cur Int: " + victim.getTotalInt() + " Cur Luk: " + victim.getTotalLuk());
			player.dropMessage(5, "Cur WATK: " + victim.getTotalWatk() + " Cur MATK: " + victim.getTotalMagic());
			player.dropMessage(5, "Cur WATK Buff: " + watkBuff + " Cur MATK Buff: " + matkBuff + " Cur Blessing Level: " + blessing);
			player.dropMessage(5, victim.getName() + "'s maximum base damage (before skills) is " + maxBase);
		} else if (sub[0].equals("inmap")) {
			String s = "";
			for (MapleCharacter chr : player.getMap().getCharacters()) {
				s += chr.getName() + " ";
			}
			player.message(s);
		} else if (sub[0].equals("cleardrops")) {
			player.getMap().clearDrops(player);		
		} else if (sub[0].equals("reloadevents")) {
			for (Channel ch : Server.getInstance().getAllChannels()) {
				ch.reloadEventScriptManager();
			}
			player.dropMessage(5, "Reloaded Events");
		} else if (sub[0].equals("reloaddrops")) {
			MapleMonsterInformationProvider.getInstance().clearDrops();
			player.dropMessage(5, "Reloaded Drops");
		} else if (sub[0].equals("reloadportals")) {
			PortalScriptManager.getInstance().reloadPortalScripts();
			player.dropMessage(5, "Reloaded Portals");
		} else if (sub[0].equals("whereami")) { //This is so not going to work on the first commit
			player.yellowMessage("Map ID: " + player.getMap().getId());
		} else if (sub[0].equals("warp")) {
		  MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
        if (victim != null) {
            if (sub.length == 2) {
                MapleMap target = victim.getMap();
                c.getPlayer().changeMap(target, victim.getPosition());
            } else {
                int mapid = Integer.parseInt(sub[2]);
                MapleMap target = c.getChannelServer().getMapFactory().getMap(mapid);
                victim.changeMap(target, target.getPortal(0));
            }
        }
        else
        {
            try
            {
                  int mapid = Integer.parseInt(sub[1]);
                MapleMap target = c.getChannelServer().getMapFactory().getMap(mapid);
                player.changeMap(target, target.getPortal(0));
            } catch (Exception e)
            {
                e.printStackTrace();
                player.dropMessage("Something went wrong..");
            }
        }
		} else if (sub[0].equals("reloadmap")) {
			MapleMap oldMap = c.getPlayer().getMap();
			MapleMap newMap = c.getChannelServer().getMapFactory().getMap(player.getMapId());
                      //  int playersinmap = oldMap.getCharacters().size();
                        List<MapleCharacter>players = new ArrayList<>();                         
			for (MapleCharacter ch : oldMap.getCharacters()) {
                            players.add(ch);
			}
                        for(int i = 0;i < players.size();i++)
                            players.get(i).changeMap(newMap,player.getPosition());
                        
			oldMap = null;
			newMap.respawn();
               } else if (sub[0].equals("checkmath")){  
                   player.dropMessage(6,5/2 +"");
		} else if (sub[0].equals("music")){
			if (sub.length < 2) {
				player.yellowMessage("Syntax: !music <song>");
				for (String s : songs){
					player.yellowMessage(s);
				}
				return false;
			}
			String song = StringUtil.joinStringFrom(sub, 1); 
			for (String s : songs){
				if (s.equals(song)){
					player.getMap().broadcastMessage(MaplePacketCreator.musicChange(s));
					player.yellowMessage("Now playing song " + song + ".");
					return true;
				}
			}
			player.yellowMessage("Song not found, please enter a song below.");
			for (String s : songs){
				player.yellowMessage(s);
			}
		} else if (sub[0].equals("monitor")) {
			if (sub.length < 1){
				player.yellowMessage("Syntax: !monitor <ign>");
				return false;
			}
			MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
			if (victim == null){
				player.yellowMessage("Player not found!");
				return false;
			}
			boolean monitored = MapleLogger.monitored.contains(victim.getName());
			if (monitored){
				MapleLogger.monitored.remove(victim.getName());
			} else {
				MapleLogger.monitored.add(victim.getName());
			}
			player.yellowMessage(victim.getName() + " is " + (!monitored ? "now being monitored." : "no longer being monitored."));
			String message = player.getName() + (!monitored ? " has started monitoring " : " has stopped monitoring ") + victim.getName() + ".";
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
		} else if (sub[0].equals("monitors")) {
			for (String ign : MapleLogger.monitored){
				player.yellowMessage(ign + " is being monitored.");
			}
		} else if (sub[0].equals("ignore")) {
			if (sub.length < 1){
				player.yellowMessage("Syntax: !ignore <ign>");
				return false;
			}
			MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
			if (victim == null){
				player.yellowMessage("Player not found!");
				return false;
			}
			boolean monitored = MapleLogger.ignored.contains(victim.getName());
			if (monitored){
				MapleLogger.ignored.remove(victim.getName());
			} else {
				MapleLogger.ignored.add(victim.getName());
			}
			player.yellowMessage(victim.getName() + " is " + (!monitored ? "now being ignored." : "no longer being ignored."));
			String message = player.getName() + (!monitored ? " has started ignoring " : " has stopped ignoring ") + victim.getName() + ".";
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
		} else if (sub[0].equals("ignored")) {
			for (String ign : MapleLogger.ignored){
				player.yellowMessage(ign + " is being ignored.");
			}
		} else if (sub[0].equals("pos")) {
			float xpos = player.getPosition().x;
			float ypos = player.getPosition().y;
			float fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();
			player.dropMessage("Position: (" + xpos + ", " + ypos + ")");
			player.dropMessage("Foothold ID: " + fh);
		} else if (sub[0].equals("dc")) {
			MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
			if (victim == null) {
				victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				if (victim == null) {
					victim = player.getMap().getCharacterByName(sub[1]);
					if (victim != null) {
						try {//sometimes bugged because the map = null
							victim.getClient().disconnect(true, false);
							player.getMap().removePlayer(victim);
						} catch (Exception e) {
						}
					} else {
						return true;
					}
				}
			}
			if (player.gmLevel() < victim.gmLevel()) {
				victim = player;
			}
			victim.getClient().disconnect(false, false);
		} else if (sub[0].equals("exprate")) {
			c.getWorldServer().setExpRate(Integer.parseInt(sub[1]));
		} else if (sub[0].equals("chat") || sub[0].equals("chattype")) {
			player.toggleWhiteChat();
			player.message("Your chat is now " + (player.getWhiteChat() ? " white" : "normal") + ".");
		
		} else if (sub[0].equals("warphere")) {
                   //  player.dropMessage(6,player.getPosition().x+"");
			MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
			if (victim == null) {//If victim isn't on current channel, loop all channels on current world.
				for (Channel ch : srv.getChannelsFromWorld(c.getWorld())) {
					victim = ch.getPlayerStorage().getCharacterByName(sub[1]);
					if (victim != null) {
						break;//We found the person, no need to continue the loop.
					}
				}
			}
			if (victim != null) {
				if (victim.getEventInstance() != null) {
					victim.getEventInstance().unregisterPlayer(victim);
				}
				//Attempt to join the warpers instance.
				if (player.getEventInstance() != null) {
					if (player.getClient().getChannel() == victim.getClient().getChannel()) {//just in case.. you never know...
						player.getEventInstance().registerPlayer(victim);
						victim.changeMap(player.getEventInstance().getMapInstance(player.getMapId()), player.getMap().findClosestPortal(player.getPosition()));
					} else {
						player.dropMessage("Target isn't on your channel, not able to warp into event instance.");
					}
				} else {//If victim isn't in an event instance, just warp them.
					victim.changeMap(player.getMap(),player.getPosition());
				}
				if (player.getClient().getChannel() != victim.getClient().getChannel()) {//And then change channel if needed.
					victim.dropMessage("Changing channel, please wait a moment.");
					victim.getClient().changeChannel(player.getClient().getChannel());
				}
			} else {
				player.dropMessage("Unknown player.");
			}
		} else if (sub[0].equals("fame")) {
			MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
			victim.setFame(Integer.parseInt(sub[2]));
			victim.updateSingleStat(MapleStat.FAME, victim.getFame());
		} else if (sub[0].equals("giftnx")) {
			cserv.getPlayerStorage().getCharacterByName(sub[1]).getCashShop().gainCash(1, Integer.parseInt(sub[2]));
			player.message("Done");
		} else if (sub[0].equals("gmshop")) {
			MapleShopFactory.getInstance().getShop(1337).sendShop(c);
		} else if (sub[0].equals("heal")) {
                    if(sub.length > 1){
                        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
			victim.setHpMp(30000);                       
                    }
                    else{
			player.setHpMp(30000);
                    }
                    } else if (sub[0].equals("healmap")) {  
                       for (MapleCharacter a1 : player.getMap().getCharacters())
                           {
                               a1.setHpMp(30000);
                           } 
                      } else if(sub[0].equals("clock")) {
            player.getMap().broadcastMessage(MaplePacketCreator.getClock(Integer.parseInt(sub[1])));
            final String[] s = StringUtil.joinStringFrom(sub, 2).split(" ");
            tmp = c;
            TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                executeGMCommand(tmp, s, '/');
                tmp = null;
            }}, Integer.parseInt(sub[1]) * 1000);
        

                    
                             
		} else if (sub[0].equals("vp")) {
			c.addVotePoints(Integer.parseInt(sub[1]));
		} else if (sub[0].equals("id")) {
			try {
				try (BufferedReader dis = new BufferedReader(new InputStreamReader(new URL("http://www.mapletip.com/search_java.php?search_value=" + sub[1] + "&check=true").openConnection().getInputStream()))) {
					String s;
					while ((s = dis.readLine()) != null) {
						player.dropMessage(s);
					}
				}
			} catch (Exception e) {
			}
                        
               
		} else if (sub[0].equals("item") || sub[0].equals("drop")) {
			int itemId = Integer.parseInt(sub[1]);
			short quantity = 1;
			try {
				quantity = Short.parseShort(sub[2]);
			} catch (Exception e) {
			}
			if (sub[0].equals("item")) {
				int petid = -1;
				if (ItemConstants.isPet(itemId)) {
					petid = MaplePet.createPet(itemId);
				}
                                if (player.itemExists(itemId))
                                {
				MapleInventoryManipulator.addById(c, itemId, quantity, player.getName(), petid, -1);
                                }
                                else
                                {
                                   player.dropMessage("This item does not exist."); 
                                }
                        }else {
				Item toDrop;
				if (MapleItemInformationProvider.getInstance().getInventoryType(itemId) == MapleInventoryType.EQUIP) {
					toDrop = MapleItemInformationProvider.getInstance().getEquipById(itemId);
				} else {
					toDrop = new Item(itemId, (short) 0, quantity);
				}
				c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
			}
               } else if (sub[0].equals("randitem")) {         
                Equip sword = new Equip(1302000,(short)0,1);
                sword.setStr((short)30000);
                MapleInventoryManipulator.addFromDrop(c, (Item)sword, true);
		} else if (sub[0].equals("expeds")) {
			for (Channel ch : Server.getInstance().getChannelsFromWorld(0)) {
				if (ch.getExpeditions().size() == 0) {
					player.yellowMessage("No Expeditions in Channel " + ch.getId());
					continue;
				}
				player.yellowMessage("Expeditions in Channel " + ch.getId());
				int id = 0;
				for (MapleExpedition exped : ch.getExpeditions()) {
					id++;
					player.yellowMessage("> Expedition " + id);
					player.yellowMessage(">> Type: " + exped.getType().toString());
					player.yellowMessage(">> Status: " + (exped.isRegistering() ? "REGISTERING" : "UNDERWAY"));
					player.yellowMessage(">> Size: " + exped.getMembers().size());
					player.yellowMessage(">> Leader: " + exped.getLeader().getName());
					int memId = 2;
					for (MapleCharacter member : exped.getMembers()) {
						if (exped.isLeader(member)) {
							continue;
						}
						player.yellowMessage(">>> Member " + memId + ": " + member.getName());
						memId++;
					}
				}
			}
		} else if (sub[0].equals("kill")) {
			if (sub.length >= 2) {
				MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                                victim.setHpMp(0);
				Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, player.getName() + " used !kill on " + victim.getName()));
			}
               } else if (sub[0].equals("killmap")) {  
               
                   
                       for (MapleCharacter a1 : player.getMap().getCharacters())
                           {
                               a1.setHpMp(0);
                           } 
		} else if (sub[0].equals("seed")) {
			if (player.getMapId() != 910010000) {
				player.yellowMessage("This command can only be used in HPQ.");
				return false;
			}
			Point pos[] = {new Point(7, -207), new Point(179, -447), new Point(-3, -687), new Point(-357, -687), new Point(-538, -447), new Point(-359, -207)};
			int seed[] = {4001097, 4001096, 4001095, 4001100, 4001099, 4001098};
			for (int i = 0; i < pos.length; i++) {
				Item item = new Item(seed[i], (byte) 0, (short) 1);
				player.getMap().spawnItemDrop(player, player, item, pos[i], false, true);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else if (sub[0].equals("killall")) {
			List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
			MapleMap map = player.getMap();
			for (MapleMapObject monstermo : monsters) {
				MapleMonster monster = (MapleMonster) monstermo;
				if (!monster.getStats().isFriendly()) {
					map.killMonster(monster, player, true);
					monster.giveExpToCharacter(player, monster.getExp() * c.getPlayer().getExpRate(), true, 1);
				}
			}
			player.dropMessage("Killed " + monsters.size() + " monsters.");
		} else if (sub[0].equals("monsterdebug")) {
			List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
			for (MapleMapObject monstermo : monsters) {
				MapleMonster monster = (MapleMonster) monstermo;
				player.message("Monster ID: " + monster.getId());
			}
		} else if (sub[0].equals("unbug")) { // uh well i thin
			c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.enableActions());
		} else if (sub[0].equals("level")) {
                    if(sub.length < 3){                      
			player.setLevel(Integer.parseInt(sub[1]) - 1);
			player.gainExp(-player.getExp(), false, false);
			player.levelUp(false);
                    }
                    else {
                       MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);                            
                        if(victim != null){
                        victim.setLevel(Integer.parseInt(sub[2]) - 1);
			victim.gainExp(-victim.getExp(), false, false);
			victim.levelUp(false);
                        }
                        else
                            player.dropMessage(5,"Error. player isn't on.");                        
                    }
                        
		} else if (sub[0].equals("levelpro")) {
			while (player.getLevel() < Math.min(255, Integer.parseInt(sub[1]))) {
				player.levelUp(false);
                                
			}
                        } else if (sub[0].equals("speakall")) {
            String text = StringUtil.joinStringFrom(sub, 1);            
            for (MapleCharacter mch : player.getMap().getCharacters()) {
                mch.getMap().broadcastMessage(MaplePacketCreator.getChatText(mch.getId(), text, false, 0));
            }
		} else if (sub[0].equals("maxstat")) {
			final String[] s = {"setall", String.valueOf(Short.MAX_VALUE)};
			executeGMCommand(c, s, heading);
			player.setLevel(255);
			player.setFame(13337);
			player.setMaxHp(30000);
			player.setMaxMp(30000);
			player.updateSingleStat(MapleStat.LEVEL, 255);
			player.updateSingleStat(MapleStat.FAME, 13337);
			player.updateSingleStat(MapleStat.MAXHP, 30000);
			player.updateSingleStat(MapleStat.MAXMP, 30000);
		} else if (sub[0].equals("job")) {
                if (sub.length == 2) {
				player.changeJob(MapleJob.getById(Integer.parseInt(sub[1])));
				player.equipChanged();
			} else if (sub.length == 3) {
                            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				if(victim != null)
				 victim.changeJob(MapleJob.getById(Integer.parseInt(sub[2])));
				victim.equipChanged();
			} else {
				player.message("!job <job id> <opt: IGN of another person>");
			}   
                  } else if (sub[0].equals("jobmap")) { 
                      if(sub.length > 1){
                          for(MapleCharacter victim : player.getMap().getCharacters()){
		              victim.changeJob(MapleJob.getById(Integer.parseInt(sub[1])));
		              victim.equipChanged();
                          }
                      }
                      else
                          player.dropMessage(5,"Error. Please type the command as follows !jobmap <id>");
                } else if (sub[0].equals("mesos")) {                    
			player.gainMeso(Integer.parseInt(sub[1]), true);
                } else if (sub[0].equals("fieldlimits")) {
                   
                         player.dropMessage(6,"Check : " +  player.getMap().getFootholds().getX1() + ", " +  player.getMap().getFootholds().getY1() + ", " + player.getMap().getFootholds().getX2() + ", " + player.getMap().getFootholds().getY2());
		} else if (sub[0].equals("notice")) {
			Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Notice] " + StringUtil.joinStringFrom(sub, 1)));
		} else if (sub[0].equals("rip")) {
			Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[RIP]: " + StringUtil.joinStringFrom(sub, 1)));
		} else if (sub[0].equals("openportal")) {
			player.getMap().getPortal(sub[1]).setPortalState(true);
		} else if (sub[0].equals("pe")) {
			String packet = "";
			try {
				InputStreamReader is = new FileReader("pe.txt");
				Properties packetProps = new Properties();
				packetProps.load(is);
				is.close();
				packet = packetProps.getProperty("pe");
			} catch (IOException ex) {
				player.yellowMessage("Failed to load pe.txt");
				return false;
			}
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(HexTool.getByteArrayFromHexString(packet));
			SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(mplew.getPacket()));
			short packetId = slea.readShort();
			final MaplePacketHandler packetHandler = PacketProcessor.getProcessor(0, c.getChannel()).getHandler(packetId);
			if (packetHandler != null && packetHandler.validateState(c)) {
				try {
					player.yellowMessage("Recieving: " + packet);
					packetHandler.handlePacket(slea, c);
				} catch (final Throwable t) {
					FilePrinter.printError(FilePrinter.PACKET_HANDLER + packetHandler.getClass().getName() + ".txt", t, "Error for " + (c.getPlayer() == null ? "" : "player ; " + c.getPlayer() + " on map ; " + c.getPlayer().getMapId() + " - ") + "account ; " + c.getAccountName() + "\r\n" + slea.toString());
					return false;
				}
			}
		} else if (sub[0].equals("closeportal")) {
			player.getMap().getPortal(sub[1]).setPortalState(false);
		} else if (sub[0].equals("startevent")) {
			for (MapleCharacter chr : player.getMap().getCharacters()) {
				player.getMap().startEvent(chr);
			}
			c.getChannelServer().setEvent(null);
		} else if (sub[0].equals("scheduleevent")) {
			int players = 50;
			if(sub.length > 1)
				players = Integer.parseInt(sub[1]);
			
			c.getChannelServer().setEvent(new MapleEvent(player.getMapId(), players));
			player.dropMessage(5, "The event has been set on " + player.getMap().getMapName() + " and will allow " + players + " players to join.");
		} else if(sub[0].equals("endevent")) {
			c.getChannelServer().setEvent(null);
			player.dropMessage(5, "You have ended the event. No more players may join.");
		} else if (sub[0].equals("online2")) {
			int total = 0;
			for (Channel ch : srv.getChannelsFromWorld(player.getWorld())) {
				int size = ch.getPlayerStorage().getAllCharacters().size();
				total += size;
				String s = "(Channel " + ch.getId() + " Online: " + size + ") : ";
				if (ch.getPlayerStorage().getAllCharacters().size() < 50) {
					for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
						s += MapleCharacter.makeMapleReadable(chr.getName()) + ", ";
					}
					player.dropMessage(s.substring(0, s.length() - 2));
				}
			}
			player.dropMessage("There are a total of " + total + " players online.");
		} else if (sub[0].equals("pap")) {
			player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8500001), player.getPosition());
		} else if (sub[0].equals("pianus")) {
			player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8510000), player.getPosition());
		} else if (sub[0].equalsIgnoreCase("search")) {
			StringBuilder sb = new StringBuilder();
			if (sub.length > 2) {
				String search = StringUtil.joinStringFrom(sub, 2);
				long start = System.currentTimeMillis();//for the lulz
				MapleData data = null;
				MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("wz/String.wz"));
				if (!sub[1].equalsIgnoreCase("ITEM")) {
					if (sub[1].equalsIgnoreCase("NPC")) {
						data = dataProvider.getData("Npc.img");
					} else if (sub[1].equalsIgnoreCase("MOB") || sub[1].equalsIgnoreCase("MONSTER")) {
						data = dataProvider.getData("Mob.img");
					} else if (sub[1].equalsIgnoreCase("SKILL")) {
						data = dataProvider.getData("Skill.img");
					} else if (sub[1].equalsIgnoreCase("MAP")) {
						sb.append("#bUse the '/m' command to find a map. If it finds a map with the same name, it will warp you to it.");
					} else {
						sb.append("#bInvalid search.\r\nSyntax: '/search [type] [name]', where [type] is NPC, ITEM, MOB, or SKILL.");
					}
					if (data != null) {
						String name;
						for (MapleData searchData : data.getChildren()) {
							name = MapleDataTool.getString(searchData.getChildByPath("name"), "NO-NAME");
							if (name.toLowerCase().contains(search.toLowerCase())) {
								sb.append("#b").append(Integer.parseInt(searchData.getName())).append("#k - #r").append(name).append("\r\n");
							}
						}
					}
				} else {
					for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
						if (sb.length() < 32654) {//ohlol
							if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
								//#v").append(id).append("# #k- 
								sb.append("#b").append(itemPair.getLeft()).append("#k - #r").append(itemPair.getRight()).append("\r\n");
							}
						} else {
							sb.append("#bCouldn't load all items, there are too many results.\r\n");
							break;
						}
					}
				}
				if (sb.length() == 0) {
					sb.append("#bNo ").append(sub[1].toLowerCase()).append("s found.\r\n");
				}
				sb.append("\r\n#kLoaded within ").append((double) (System.currentTimeMillis() - start) / 1000).append(" seconds.");//because I can, and it's free
			} else {
				sb.append("#bInvalid search.\r\nSyntax: '/search [type] [name]', where [type] is NPC, ITEM, MOB, or SKILL.");
			}
			c.announce(MaplePacketCreator.getNPCTalk(9010000, (byte) 0, sb.toString(), "00 00", (byte) 0));
		} else if (sub[0].equals("servermessage")) {
			c.getWorldServer().setServerMessage(StringUtil.joinStringFrom(sub, 1));
		} else if (sub[0].equals("warpsnowball")) {
			List<MapleCharacter> chars = new ArrayList<>(player.getMap().getCharacters());
			for (MapleCharacter chr : chars) {
				chr.changeMap(109060000, chr.getTeam());
			}
		} else if (sub[0].equals("setall")) {
			final int x = Short.parseShort(sub[1]);
			player.setStr(x);
			player.setDex(x);
			player.setInt(x);
			player.setLuk(x);
			player.updateSingleStat(MapleStat.STR, x);
			player.updateSingleStat(MapleStat.DEX, x);
			player.updateSingleStat(MapleStat.INT, x);
			player.updateSingleStat(MapleStat.LUK, x);
                } else if (sub[0].equals("jail")) { 
                    if(sub.length > 1){
                       MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                       if(target != null)
                       {
                         target.changeMap(300020100);
                         target.setUnjailed(false);
                       //  target.getClient().setJailed(1);
                       
                       }
                       else
                         player.dropMessage(5,"Error. Target doesn't exist!");  
                    }
                    else
                        player.dropMessage(5,"Error. Please type the command as follows !jail <name>");
		 } else if (sub[0].equals("unjail")) { 
                     if(sub.length > 1){
                       MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                       if(target != null)
                       {
                         target.setUnjailed(true);
                         target.changeMap(910000000);
                       }
                       else
                         player.dropMessage(5,"Error. Target doesn't exist!");  
                    }
                    else
                        player.dropMessage(5,"Error. Please type the command as follows !unjail <name>");
                } else if (sub[0].equals("unban")) {
			try {
				try (PreparedStatement p = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET banned = 0 WHERE id = " + MapleCharacter.getIdByName(sub[1]))) {
					p.executeUpdate();
				}
			} catch (Exception e) {
				player.message("Failed to unban " + sub[1]);
				return true;
			}
			player.message("Unbanned " + sub[1]);
		} 
                else if (sub[0].equals("ban")) {
			if (sub.length < 3) {
				player.dropMessage(5,"Error. Please type the command as follows !ban <IGN> <Reason> (Please be descripitive)");
				return false;
			}
			String ign = sub[1];
			String reason = StringUtil.joinStringFrom(sub, 2);
			MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(ign);
			if (target != null) {
				String readableTargetName = MapleCharacter.makeMapleReadable(target.getName());
				String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
				//Ban ip
				PreparedStatement ps = null;
				try {
					Connection con = DatabaseConnection.getConnection();
					if (ip.matches("/[0-9]{1,3}\\..*")) {
						ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
						ps.setString(1, ip);
						ps.executeUpdate();
						ps.close();
					}
				} catch (SQLException ex) {
					c.getPlayer().message("Error occured while banning IP address");
					c.getPlayer().message(target.getName() + "'s IP was not banned: " + ip);
				}
				target.getClient().banMacs();
				reason = c.getPlayer().getName() + " banned " + readableTargetName + " for " + reason + " (IP: " + ip + ") " + "(MAC: " + c.getMacs() + ")";
				target.ban(reason);
				target.yellowMessage("You have been banned by #b" + c.getPlayer().getName() + " #k.");
				target.yellowMessage("Reason: " + reason);
				c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
				final MapleCharacter rip = target;
				TimerManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						rip.getClient().disconnect(false, false);
					}
				}, 5000); //5 Seconds
				Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[RIP]: " + ign + " has been banned for " + reason));
			} else if (MapleCharacter.ban(ign, reason, false)) {
				c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
				Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[RIP]: " + ign + " has been banned for " + reason));
			} else {
				c.announce(MaplePacketCreator.getGMEffect(6, (byte) 1));
			}
                } else if (sub[0].equalsIgnoreCase("night")) {
                    player.getMap().broadcastNightEffect();
                    player.yellowMessage("Done.");
		} else {
			return false;
		}
		return true;
	}
}
