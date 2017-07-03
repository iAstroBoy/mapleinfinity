/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.BuddylistEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import java.sql.PreparedStatement;
import constants.GameConstants;
import constants.ServerConstants;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import net.server.Server;
import net.server.channel.Channel;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import scripting.event.EventHandler;
import scripting.event.Fishing;
import scripting.event.Ranking;
import scripting.npc.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.gachapon.MapleGachapon.Gachapon;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;

/**
 *
 * @author Administrator
 */
public class PlayerCommands {
    
    

	private static HashMap<String, Integer> gotomaps = new HashMap<>();
        
         public static Fishing fish = new Fishing();        
        public static EventHandler event = GMCommands.event; // might not work, revisit if it doesnt
	private static String[] tips = {
		"Please only use @gm in emergencies or to report somebody.",
		"To report a bug or make a suggestion, use the forum.",
		"Please do not use @gm to ask if a GM is online.",
		"Do not ask if you can receive help, just state your issue.",
		"Do not say 'I have a bug to report', just state it.",
	};
        
        

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

	static {
		gotomaps.put("gmmap", 180000000);
		gotomaps.put("southperry", 60000);
		gotomaps.put("amherst", 1010000);
		gotomaps.put("henesys", 100000000);
		gotomaps.put("ellinia", 101000000);
		gotomaps.put("perion", 102000000);
		gotomaps.put("kerning", 103000000);
		gotomaps.put("lith", 104000000);
		gotomaps.put("sleepywood", 105040300);
		gotomaps.put("florina", 110000000);
		gotomaps.put("orbis", 200000000);
		gotomaps.put("happy", 209000000);
		gotomaps.put("elnath", 211000000);
		gotomaps.put("ludi", 220000000);
		gotomaps.put("aqua", 230000000);
		gotomaps.put("leafre", 240000000);
		gotomaps.put("mulung", 250000000);
		gotomaps.put("herb", 251000000);
		gotomaps.put("omega", 221000000);
		gotomaps.put("korean", 222000000);
		gotomaps.put("nlc", 600000000);
		gotomaps.put("excavation", 990000000);
		gotomaps.put("pianus", 230040420);
		gotomaps.put("horntail", 240060200);
		gotomaps.put("mushmom", 100000005);
		gotomaps.put("griffey", 240020101);
		gotomaps.put("manon", 240020401);
		gotomaps.put("horseman", 682000001);
		gotomaps.put("balrog", 105090900);
		gotomaps.put("zakum", 211042300);
		gotomaps.put("papu", 220080001);
		gotomaps.put("showa", 801000000);
		gotomaps.put("guild", 200000301);
		gotomaps.put("shrine", 800000000);
		gotomaps.put("skelegon", 240040511);
		gotomaps.put("hpq", 100000200);
		gotomaps.put("ht", 240050400);
		gotomaps.put("fm", 910000000);
                gotomaps.put("home", 50);
	}
          static String playertagger = "notagger";
          static boolean tagger = false; 
          static boolean tempo = false;
    
    
     public static boolean executePlayerCommand(MapleClient c, String[] sub, char heading) throws SQLException {
		MapleCharacter player = c.getPlayer();
		if (heading == '!' && player.gmLevel() == 0) {
			player.yellowMessage("You may not use !" + sub[0] + ", please try /" + sub[0]);
			return false;
		}
		switch (sub[0]) {
		case "commands":
		case "help":
				
		     String[] messagesToDrop = {
		     "@aio: Opens up multipurpose NPC.",
                     "@dispose: Fixes your character if it is stuck.", 
                     "@online: Displays a list of all online players." ,
                     "@job: Opens job NPC." ,
                     "@shop/@a: Opens up All-in-one shop NPC." ,
                     "@go <townmap>: Warps you to that town" ,
                     "@spinel: Open warper NPC" ,
                     "@home: Warps you back home." ,
                     "@fm: Warps you to fm." ,
                     "@style/@styler: Opens up style NPC." ,
                     "@dailyreward: Opens up daily reward NPC." ,
                     "@callgm/@gm <message>: Sends a message to all online GMs in the case of an emergency." ,
                     "@staff: Lists the staff of Memory." ,
                     "@rbe/@rbc/@rba: Rebirths your character into explorer,cygnus or aran accordingly" ,
                     "@rank: Opens rank NPC." ,
                     "@rbrank: Shows rb ranks." ,
                     "@maxskills: Max up your skills." ,
                     "@str/@dex/@int/@luk <num>: Inserts ap to str,dex,int or luk accordingly" ,
                     "@apreset: Resets your ap back to 4" ,
                     "@expfix: Fix for your exp" ,
                     "@time: Shows server time." ,
                     "@uptime: Shows server uptime." ,
                     "@joinevent/@join/@j: Lets you join event if the gates are open." ,
                     "@showwinners: Shows current event winners." ,
                     "@giveep <player> <amount>: Gives ep to the player of your choosing." ,
                     "@eventlog: Shows you the last 5 events hosted & how long ago." ,
                     "@emo: Try it urself." ,
                     "@vote <option>: Lets you vote on gm poll.",
                     "@buy: Exchanges 2B meso for 1 piece of currency." ,
                     "@sell: Exchanges 1 piece of currency for 2B meso." ,
                     "@whereami: Gives you maps name + id." ,
                     "@whatdropsfrom <monster name>: Displays a list of drops and chances for a specified monster." ,
                     "@whodrops <item name>: Displays monsters that drop an item given an item name." ,
                     "@smega/@s <msg>: Smegas your message & uses up your smega." ,
                     "@checkme: Your stats showup." ,
                     "@spy <player>: The stats of the player you're spying showup." ,
                     "@whosalive: Shows you whos alive in the map you're currently in." ,
                     "@whosdead: Shows you whos alive in the map you're currently in." ,
                     "@deathlog: Shows you players who died in the last 60 seconds." ,
                     "@changejob: For players with 100rbs or more, lets you change your job regardless." ,
                     "@autorebirth: For players with 1000rbs or more, lets you autorebirth when activated." ,
                     "@chalkboard/@chalktalk/@chalk <msg>: Opens chalk with your msg." ,
                     "@tagger <player>: Sets player as tagger, @taggeroff to turn off." ,
                     "@tag: Tags close players, only when activated." ,
                     "@bomb: Spawns a bomb, only when activated.",
                     "@presinfo: Displays the presidential commands."};
        

				double commandsPerPage = 6.0;
				double pages = Math.ceil(((double) messagesToDrop.length) / commandsPerPage);

				if (sub.length < 2) {
					player.dropMessage(5, "=============================================");
					player.dropMessage(5, "Hello! We currently have " + ((int) pages) + " pages of commands.");
					player.dropMessage(5, "Please, use one of the following valid command syntax:   ");
					player.dropMessage(5, "@commands/command/help <page number>                     ");
					player.dropMessage(5, "For example, @help 2                                     ");
					player.dropMessage(5, "=============================================");
					return true;
				}

				if (Integer.parseInt(sub[1]) == 0) {
					player.dropMessage(5, "The index starts at 1, are you a programmer or what?");
					return true;
				}

				player.dropMessage(5, "Here are the commands for page" + Integer.parseInt(sub[1]));
				int sp = (int) (commandsPerPage * (Integer.parseInt(sub[1]) - 1));
				int tp = (int) (int) (commandsPerPage * (Integer.parseInt(sub[1])));
				for (int i = sp; i < tp; i++) {
					player.dropMessage(5, messagesToDrop[i]);
				}
				break;
               
                    case "staff":
                        
		case "team":
        	String staffInfo[][] = {                    
                    {">.<", "EST", "Owner"},
                    {"rabbit", "EST", "Admin"},
                    {"Kanwar", "PST", "Admin"},
                    {"????", "PST", "Dev"},
                    {"???", "GMT+2", "Dev"},
                    {"???", "EST", "GFX"},
                };
                player.message("The staff team of MapleInfinity is as follows:");
                player.message("IGN | Timezone | Position | On/Offline");
                for (String[] staffMember : staffInfo) {
                    String staffMemberString = staffMember[0]; // so we don't have a ' |' at the end of every line
                    for (int i = 1; i < staffMember.length; i++) {
                        staffMemberString += " | " + staffMember[i];
                    }
                    boolean foundOnline = false;
                    MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(staffMember[0]); // staffMember[0] = IGN
                    if (victim != null) {
                    	foundOnline = true;
                    }
                    String status = foundOnline ? "Online" : "Offline";
                    player.message(staffMemberString + " | " + status);
                }
			
			break;
                    
                    
                 case "rbc":
                     if (c.getPlayer().getLevel() >= 200)
                    {
                        c.getPlayer().doRebirth(2);
                        c.getPlayer().dropMessage("You now have " + c.getPlayer().getRebirths() + " rebirths!");
                    }
                     break;
                case "rba":
                    if (c.getPlayer().getLevel() >= 200)
                    {
                        c.getPlayer().doRebirth(3);
                        c.getPlayer().dropMessage("You now have " + c.getPlayer().getRebirths() + " rebirths!");
                    }
                    break;
                case "rbe":
                case "rb": 
                     if (c.getPlayer().getLevel() >= 200)
                    {
                        c.getPlayer().doRebirth(1);
                        c.getPlayer().dropMessage("You now have " + c.getPlayer().getRebirths() + " rebirths!");
                    }
                    break;
                case "rebirth":
                    player.dropMessage(5,"@rb/@rebirth/@rbe for explorer, @rba for aran, @rbc for cygnus");
                     if (c.getPlayer().getLevel() >= 200)
                    {
                        c.getPlayer().doRebirth(1);
                        c.getPlayer().dropMessage("You now have " + c.getPlayer().getRebirths() + " rebirths!");
                    }
                    break;
                case "changejob":
                    if(player.getRebirths() > 99)
                    {
                        player.changeJob(MapleJob.BEGINNER);
                        player.updateSingleStat(MapleStat.JOB, 0);
                        player.dropMessage(5,"done");
                    }
                    else
                        player.dropMessage(6,"Only players with 100rbs and above are allowed to use this command");
                    break;
                case "autorebirth":
                    if (c.getPlayer().getRebirths() >= 1000)
                    {
                   player.autoRebirth = !player.autoRebirth;
                   player.dropMessage(player.autoRebirth ? "AutoRebirth is now on." : "AutoRebirth is now off.");
                    }
                    else
                    {
                        player.dropMessage("You need 1000 rebirths to use this command.");
                        
                    }
                   break; 
                case "rbrank":
                    ResultSet rsa = Ranking.getRbrank();
                    player.dropMessage(6,"Top 10 most RBed players:");
                    int counter = 1;
                     while(rsa.next()){
                     player.dropMessage(6,counter+". " + rsa.getString("name") + " || " + rsa.getInt("rebirths"));
                     counter++;
                    }
                    break; 
                case "rank":
                      NPCScriptManager.getInstance().start(c, 2040019, null, null);  
                    break;
                    
		case "time":
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
			player.yellowMessage("Memory Server Time (EST): " + dateFormat.format(new Date()));
			break;
             
               case "showwinners":
                    String winnertext = "";
                    StringBuilder sb2 = new StringBuilder();
                    if (event.isRunning())
                    {
                    for (String key : event.winners.keySet())
                    {
                        sb2.append(key).append("[").append(event.winners.get(key)).append("]").append(", ");
                    }
                    if (sb2.length() > 0)
                    {
                       sb2.setLength(sb2.length() - 2);
                       player.dropMessage(sb2.toString());

                    }
                     else
                    {
                        player.dropMessage("No current winners.");
                    }
                    }
                    else
                    {
                        player.dropMessage("There is currently no events running right now.");
                    }
                    
                   
                    break;
                case "msg":
                    boolean isOn = false;
                    String waifu = StringUtil.joinStringFrom(sub, 1);
                    if (c.getPlayer().isMarried()) {
                MapleCharacter wife = c.getChannelServer().getPlayerStorage().getCharacterById(c.getPlayer().getPartnerId());
                if (wife != null) {
                    wife.getClient().announce(MaplePacketCreator.sendSpouseChat(c.getPlayer(), waifu));
                    c.announce(MaplePacketCreator.sendSpouseChat(c.getPlayer(), waifu));
                } else
                    try {
                        for (Channel ch : c.getWorldServer().getChannels()) {
                    if (ch.isConnected(wife.getName())) {
                        isOn = true;
                        break;
                    }
                    else
                    {
                        c.getPlayer().dropMessage("Your spouse is currently not on, or you are not married!");
                        isOn = false;
                        }
                        }
                        if (isOn)
                        {
                                //c.getChannelServer().getWorldInterface().sendSpouseChat(c.getPlayer().getName(), wife.getName(), msg);
                            c.announce(MaplePacketCreator.sendSpouseChat(c.getPlayer(), waifu
                                    
                                    
                                    
                                    
                                    
                                    ));
                        }
                        
                        
                    } catch (Exception e) {
                        c.getPlayer().message("You are either not married or your spouse is currently offline.");
                    }
                }
                    break;
                case "buy":    
                case "buymemory":
                    player.buyCurrency();
                    break;
                case "sell":    
                case "sellmemory":
                    player.sellCurrency();
                    break;
                case "sandbox":
                    player.changeMap(180000001);
                case "clearslots":
                    if (sub.length == 2)
                    {
                        if (sub[1].equalsIgnoreCase("all"))
                        {
                         player.clearSlots(c, 1);
                         player.clearSlots(c, 2);
                         player.clearSlots(c, 3);
                         player.clearSlots(c, 4);
                         player.clearSlots(c, 5);
                        }
                        if (sub[1].equalsIgnoreCase("equip"))
                        {
                            player.clearSlots(c, 1);
                        }
                        if (sub[1].equalsIgnoreCase("use"))
                        {
                            player.clearSlots(c, 2);
                        }
                        if (sub[1].equalsIgnoreCase("etc"))
                        {
                            player.clearSlots(c, 3);
                        }
                        if (sub[1].equalsIgnoreCase("setup"))
                        {
                            player.clearSlots(c, 4);
                        }
                        if (sub[1].equalsIgnoreCase("cash"))
                        {
                            player.clearSlots(c, 5);
                        }
                        player.dropMessage("Done!");
                    }
                    break;
                case "emo":
                    player.setHp(0);
                    player.updateSingleStat(MapleStat.HP, 0);
                    break;
                case "dropinv":
                    for(int i=0; i< player.getInventory(MapleInventoryType.EQUIP).getSlotLimit()+1;i++)
                        MapleInventoryManipulator.drop(c, MapleInventoryType.EQUIP, (short)i, (short)1);
                    break;
                case "rewarp":
                case "r":
                    if(player.JQmap() != 0){
                        if(player.getMapId() == player.JQmap())
                            player.startJQ(player.JQmap());
                        else
                            player.dropMessage(5,"Error. You haven't started a JQ yet.");
                        
                    }
                    else
                        player.dropMessage(5,"Error. You haven't started a JQ yet.");                    
                    break; 
                case "lastjq":
                    if(player.JQmap() != 0)                        
                            player.startJQ(player.JQmap());
                     else
                        player.dropMessage(5,"Error. You haven't played any JQs yet.");                    
                    break;     
                case "checkafk":
                    if(sub.length > 1){
                        MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                        if(victim != null){
                            if(victim.isAfk()){
                                String msg= victim.getName() + " has been AFK for ";
                                Calendar current = Calendar.getInstance();
                                int yourhours = current.get(Calendar.HOUR);
                                int yourminutes = current.get(Calendar.MINUTE);
                                Calendar afk = victim.getAfktime();
                                int afkhours = afk.get(Calendar.HOUR);
                                int afkminutes = afk.get(Calendar.MINUTE);
                                int disthours;
                                int distminutes;
                                
                 if(afkhours > yourhours)
                     yourhours += 12;
                 if(afkminutes > yourminutes){
                     yourminutes += 60;
                     yourhours -= 1;
                 }
                 
                 disthours = yourhours - afkhours;
                 distminutes = yourminutes - afkminutes; 
          
                if(disthours == 0){  
                    if(distminutes == 1)
                         msg+= distminutes + " minute.";
                    else
                        msg+= distminutes + " minutes.";
               
                }
                else if(disthours == 1){
                    if(distminutes == 0)
                     msg+= disthours + " hour.";   
                    else if(distminutes == 1)
                        msg+= disthours + " hour and " + distminutes + " minute."; 
                    else
                        msg+= disthours + " hour and " + distminutes + " minutes.";  
                }
                else{
                    if(distminutes == 0)
                     msg+= disthours + " hours."; 
                    else if(distminutes == 1)
                         msg+= disthours + " hours and " + distminutes + " minute."; 
                    else
                          msg+= disthours + " hours and " + distminutes + " minutes.";  
                }
                player.dropMessage(6,msg);
                            }                                
                            else
                                player.dropMessage(6,victim.getName() + " is not afk!");
                        }
                        else
                            player.dropMessage(5,"Error. Player doesn't exist!");
                    }
                    else
                        player.dropMessage(5,"Error. Please type the command as follows @checkafk <player>");
                    break;
                case "whereami":
                    player.dropMessage(5,"You're currently in " + player.getMap().getMapName() + " , ID: " + player.getMap().getId());
                    break;
                case "deathlog":
                    player.getMap().deathLog(player);                                           
                    break; 
                case "s":    
                case "smega":
                    if(sub.length >1){
                        if(player.getMapId() != 300020100 && !player.isMuted()){ // Temporary jail map
                     String text = StringUtil.joinStringFrom(sub, 1);
                     if(player.getInventory(MapleInventoryType.CASH).findById(5072000) != null){
                      Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(3, c.getChannel(),player.getMedalText() + "" + player.getName() + " : " + text, (text != "")));
                      MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5072000, (short)1, false, true);
                     } else
                         player.dropMessage(5,"You don't have any smega");
                        }
                        else
                            player.dropMessage(5,"You're not allowed to smega");
                   }
                    else
                        player.dropMessage(5,"Error. Please type out a message");
                    break;
                case "spy":                                          
            MapleCharacter p = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
            if (p != null) { // player = you, victim or p or w/e = others it wasvictim here too
               player.dropMessage("Players stats are:");
                player.dropMessage("Level: " + p.getLevel() + "");
                player.dropMessage("Fame: " + p.getFame());
                player.dropMessage("Str: " + p.getStr() + "  ||  Dex: " + p.getDex() + "  ||  Int: " + p.getInt() + "  ||  Luk: " + p.getLuk());
                player.dropMessage("Player has " + p.getMeso() + " mesos.");
                player.dropMessage("Event points: " + p.getEventpoints() + " || Participation points: " + p.getParticipationPoints());
                player.dropMessage("Hp: " + p.getHp() + "/" + p.getCurrentMaxHp() + "  ||  Mp: " + p.getMp() + "/" + p.getCurrentMaxMp());
                player.dropMessage("NX Cash: " +  + p.getCashShop().getCash(1) + " || JQ Points: " + p.getJqpoints());
                player.dropMessage("Currency: " +  + p.getCurrency() + " || Event Wins: " + p.getErp());
                player.dropMessage("Rebirths: " + p.getRebirths() + " || Fishing Level: " + fish.fishLevel(p) + " || Fishing Exp: " + p.getFishexp() + "/" + fish.fishLevelCap(p));
                player.dropMessage("VotePoints: " + p.getClient().getVotePoints() + " || Fishing Points: " + p.getFishpoints());
            } else {
                player.dropMessage("Player not found."); // ok relaunch
            }
                    break;
                case "checkme":
                player.dropMessage("Players stats are:");
                player.dropMessage("Level: " + player.getLevel() + "");
                player.dropMessage("Fame: " + player.getFame());
                player.dropMessage("Str: " + player.getStr() + "  ||  Dex: " + player.getDex() + "  ||  Int: " + player.getInt() + "  ||  Luk: " + player.getLuk());
                player.dropMessage("Player has " + player.getMeso() + " mesos.");
                player.dropMessage("Event points: " + player.getEventpoints() + " || Participation points: " + player.getParticipationPoints());
                player.dropMessage("Hp: " + player.getHp() + "/" + player.getCurrentMaxHp() + "  ||  Mp: " + player.getMp() + "/" + player.getCurrentMaxMp());
                player.dropMessage("NX Cash: " + player.getCashShop().getCash(1) + " || JQ Points: " + player.getJqpoints());
                player.dropMessage("Currency: " +  + player.getCurrency() + " || Event Wins: " + player.getErp());
                player.dropMessage("Rebirths: " + player.getRebirths() + " || Fishing Level: " + fish.fishLevel(player) + " || Fishing Exp: " + player.getFishexp() + "/" + fish.fishLevelCap(player));
                player.dropMessage("VotePoints: " + player.getClient().getVotePoints() + " || Fishing Points: " + player.getFishpoints());
                    break;
                case "giveep":
                    if(sub.length > 2){
                     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null){
                        if(Integer.parseInt(sub[2])<= player.getEventpoints())
                        {
                           victim.addEventpoints(Integer.parseInt(sub[2]));
                           player.addEventpoints(-Integer.parseInt(sub[2]));
                           victim.dropMessage(6,player.getName() + " has given you " + Integer.parseInt(sub[2]) + " ep!");
                           player.dropMessage(6,"You've given "+ victim.getName() + " " + Integer.parseInt(sub[2]) + " ep!");
                        }
                        else
                            player.dropMessage(5,"Error. You don't have enough ep");
                      }
                      else
                          player.dropMessage(5,"Error. Player is nonexistent");
                    }
                    else
                        player.dropMessage(5,"Error. Please type the command as follows @giveep <name> <amount>");
                    break;
                case "save":
                    player.saveToDB();
                    break;
               case "whosalive":
                    String names="";
                    int number=0;
                    for(MapleCharacter a1 : player.getMap().getCharacters())  
                    {
                        if(a1.isAlive()){
                          names+= a1.getName() + " ,";
                          number++;
                        }
                    }
                    player.dropMessage(6,"There are " + number + " players alive : " + names);
                        break;
                case "whosdead":                    
                    String namesdead="";
                    int numberdead=0;
                    for(MapleCharacter a1 : player.getMap().getCharacters())  
                    {
                        if(!a1.isAlive()){
                          namesdead+= a1.getName() + " ,";
                          numberdead++;
                        }
                    }
                    player.dropMessage(6,"There are " + numberdead + " players dead : " + namesdead);
                        break;
                case "go":
                    if (gotomaps.containsKey(sub[1])) {
				MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(sub[1]));
				MaplePortal targetPortal = target.getPortal(0);
				if (player.getEventInstance() != null) {
					player.getEventInstance().removePlayer(player);
				}
				player.changeMap(target, targetPortal);
			} else {
				player.dropMessage(5, "That map does not exist.");
			}
                    break;
                case "mobsalive":
                   if(player.getMap().mobsAlive(player))
                       player.dropMessage("mobs alive");
                   else
                       player.dropMessage("no mobs");
                  
                    break;
                case "eventlog":
                   event.eventLog(player);
                    break;                    
                case "apreset":
                    int ap = 0;                    
                    ap += player.getStr() - 4;
                    ap += player.getDex() - 4;
                    ap += player.getInt() - 4;
                    ap += player.getLuk() - 4;
                    player.setStr(4);
                    player.setDex(4);
                    player.setInt(4);
                    player.setLuk(4);
                    player.setRemainingAp(player.getRemainingAp() + ap);
                    
                    player.updateSingleStat(MapleStat.STR, 4);
                     player.updateSingleStat(MapleStat.DEX, 4);
                      player.updateSingleStat(MapleStat.INT, 4);
                       player.updateSingleStat(MapleStat.LUK, 4);
                       
                   player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                    break;
                case "dailyreward":
                  NPCScriptManager.getInstance().start(c, 2012024, null, null);  
                    break;
                case "maxskills":
                    for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img").getChildren()) {
				try {
					Skill skill = SkillFactory.getSkill(Integer.parseInt(skill_.getName()));
					if (GameConstants.isInJobTree(skill.getId(), player.getJob().getId())) {
                                            if(skill.getId() == 21001001)
                                               player.changeSkillLevel(skill, (byte) 0, 0, -1);
                                            else if(skill.getId() ==  2311002)
                                                player.changeSkillLevel(skill, (byte) 0, 0, -1);
                                            
                                            else    
						player.changeSkillLevel(skill, (byte) skill.getMaxLevel(), skill.getMaxLevel(), -1);
					}
				} catch (NumberFormatException nfe) {
					break;
				} catch (NullPointerException npe) {
					continue;
				}
			}
                    break;
                case "revamp":
                    for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img").getChildren()) {
				try {
					Skill skill = SkillFactory.getSkill(Integer.parseInt(skill_.getName()));
					if (GameConstants.isInJobTree(skill.getId(), player.getJob().getId())) {                                               
						player.changeSkillLevel(skill, (byte) 0, 0, -1);
					}
				} catch (NumberFormatException nfe) {
					break;
				} catch (NullPointerException npe) {
					continue;
				}
			}
                    break;    
                case "chalkboard":
                case "chalktalk":    
                case "chalk":
                    if(player.getMap().isChalkAllowed()){
                        if(sub.length > 1){
                            String text = StringUtil.joinStringFrom(sub, 1); 
                            player.setChalkboard(text);
                            player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(player, false));
                            
                        }
                        else{
                            player.dropMessage(5,"Error.. Please enter message");
                        }
                        
                        
                    }
                    else{
                        player.dropMessage(5,"@chalk has been disabled in this map");
                    }
                    break;
                case "str":
                    if(sub.length > 1)
                    {
                       int amount = Integer.parseInt(sub[1]);
                       if(amount <= player.getRemainingAp() && amount > 0)
                       {
                          if(amount + player.getStr() <= 32767)
                          {
                            // player.updateSingleStat(MapleStat.STR, amount + player.getStr());
                             player.setRemainingAp(player.getRemainingAp() - amount);
                             player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                             player.addStat(1, amount);
                          }
                          else
                          {
                             player.dropMessage(5,"Error. You've reached the maximum");
                          }
                       }
                       else
                       {
                          player.dropMessage(5,"Error. You don't have enough ap or you've entered a non-positive number"); 
                       }
                    }
                    else
                    {
                        player.dropMessage(5,"Error. Insert amount to add");
                    }
                    break;
                case "dex":                    
                    if(sub.length > 1)
                    {
                       int amount = Integer.parseInt(sub[1]);
                       if(amount <= player.getRemainingAp() && amount > 0)
                       {
                          if(amount + player.getDex() <= 32767)
                          {
                           //  player.updateSingleStat(MapleStat.DEX, amount + player.getDex()); 
                             player.setRemainingAp(player.getRemainingAp() - amount);
                             player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                             player.addStat(2, amount);
                          }
                          else
                          {
                             player.dropMessage(5,"Error. You've reached the maximum");
                          }
                       }
                       else
                       {
                          player.dropMessage(5,"Error. You don't have enough ap or you've entered a non-positive number"); 
                       }
                    }
                    else
                    {
                        player.dropMessage(5,"Error. Insert amount to add");
                    }
                    break;
                 case "int":                    
                    if(sub.length > 1)
                    {
                       int amount = Integer.parseInt(sub[1]);
                       if(amount <= player.getRemainingAp() && amount > 0)
                       {
                          if(amount + player.getInt() <= 32767)
                          {
                            // player.updateSingleStat(MapleStat.INT, amount + player.getInt()); 
                             player.setRemainingAp(player.getRemainingAp() - amount);
                             player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                             player.addStat(3, amount);
                          }
                          else
                          {
                             player.dropMessage(5,"Error. You've reached the maximum");
                          }
                       }
                       else
                       {
                          player.dropMessage(5,"Error. You don't have enough ap or you've entered a non-positive number"); 
                       }
                    }
                    else
                    {
                        player.dropMessage(5,"Error. Insert amount to add");
                    }
                        break;
                     case "luk":                    
                    if(sub.length > 1)
                    {
                       int amount = Integer.parseInt(sub[1]);
                       if(amount <= player.getRemainingAp() && amount > 0)
                       {
                          if(amount + player.getLuk() <= 32767)
                          {
                           //  player.updateSingleStat(MapleStat.LUK, amount + player.getLuk()); 
                             player.setRemainingAp(player.getRemainingAp() - amount);
                             player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                             player.addStat(4, amount);
                          }
                          else
                          {
                             player.dropMessage(5,"Error. You've reached the maximum");
                          }
                       }
                       else
                       {
                          player.dropMessage(5,"Error. You don't have enough ap or you've entered a non-positive number"); 
                       }
                    }
                    else
                    {
                        player.dropMessage(5,"Error. Insert amount to add");
                    }
                         break;
              /*  case "whosalivess":
                     int amountofplayers = 0;
                     StringBuilder sb = new StringBuilder();
                 for (MapleCharacter person : player.getMap().getCharacters())
                 {
                   
                       if (!player.isGM() && player.getHp() > 0)
                       {
                           sb.append(person.getName()).append((", "));
                            amountofplayers++;
                       }
                 }
                     
                     if (sb.toString().length() > 1)
                     {
                         sb.setLength(sb.length() - 2);
                         player.dropMessage("Players alive: " + amountofplayers);
                         player.dropMessage(sb.toString());
                     }
                     else
                     {
                         player.dropMessage("There are no current players alive on the map.");
                     }
                 
                    break; */
                     case "job":
                         NPCScriptManager.getInstance().start(c,2012022,null,null);
                         break;
                    case "shop":     
                    case "a":
                    NPCScriptManager.getInstance().start(c,1092019,null,null);
                    break;
                    case "memory":    
                    case "aio":
                        NPCScriptManager.getInstance().start(c,2030008,null,null); 
                        break;
                    case "spinel":    
                   NPCScriptManager.getInstance().start(c, 9000020, null, null);
                      break;
                        case "minigames":    
                   NPCScriptManager.getInstance().start(c, 1012008 , null, null);
                      break;
                case "style":   /* 
                      if(player.isMale()) {
            NPCScriptManager.getInstance().start(c, 9900000, null, null);
                      }
                    else  {
            NPCScriptManager.getInstance().start(c, 9900001, null, null);
                      } */
            NPCScriptManager.getInstance().start(c, 1530041, null, null);        
                    break; 
                case "jq":
                    NPCScriptManager.getInstance().start(c, 1095002, null, null);  
                    break;
                case "fm":
                    player.changeMap(910000000);
                    break; // so is it updated? ok no now u save the java file by pressing Ctrl + S, n ow u see at the top the 3 green rectanges/squares? press it yes click it now its updated
                case "fm1":
                  player.changeMap(910000001);
                    break;
                    case "fm2":
                  player.changeMap(910000002);
                    break;
                    case "fm3":
                  player.changeMap(910000003);
                    break;
                    case "fm4":
                  player.changeMap(910000004);
                    break;
                     case "fm5":
                  player.changeMap(910000005);
                    break;
                     case "fm6":
                  player.changeMap(910000006);
                    break;
                     case "fm7":
                  player.changeMap(910000007);
                    break;
                     case "fm8":
                  player.changeMap(910000008);
                    break;
                     case "fm9":
                  player.changeMap(910000009);
                    break;
                    case "fm10":
                  player.changeMap(910000010);
                    break;
                    case "fm11":
                  player.changeMap(910000011);
                    break;
                    case "fm12":
                  player.changeMap(910000012);
                    break;
                    case "fm13":
                  player.changeMap(910000013);
                    break;
                    case "fm14":
                  player.changeMap(910000014);
                    break;
                    case "fm15":
                  player.changeMap(910000015);
                    break;
                    case "fm16":
                  player.changeMap(910000016);
                    break;
                    case "fm17":
                  player.changeMap(910000017);
                    break;
                    case "fm18":
                  player.changeMap(910000018);
                    break;
                     case "fm19":
                  player.changeMap(910000019);
                    break;
                     case "fm20":
                  player.changeMap(910000020);
                    break;
                    case "fm21":
                  player.changeMap(910000021);
                    break;
                    case "fm22":
                  player.changeMap(910000022);
                    break;                    
                case "home":
                    player.changeMap(50);
                    break;
               case "expfix":
                   player.setExp(0);
                   player.updateSingleStat(MapleStat.EXP, 0);                  ;
                   break;
               case "highfive":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've highfived " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " highfived you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;
                case "rape":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've raped " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " raped you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;
                case "kiss":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've kissed " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " kissed you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;
                case "fuck":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've fucked " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " fucked you!");                              
                          }
                          else
                              player.dropMessage(6,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(6,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(6,"Error. Choose a player you'd like to fuck!");
                   break;
                case "slap":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've slapped " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " slapped you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break; 
                case "kick":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've kicked " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " kicked you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break; 
                case "punch":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've punched " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " punched you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;  
                case "yell":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've yelled at " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " yelled at you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;
                case "hug":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You gave " + victim.getName() + " a hug!");
                              victim.dropMessage(6,player.getName() + " has given you a hug!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break; 
                case "poke":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've poked " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " poked you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player you'd like to highfive!");
                   break; 
                case "choke":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've choked " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " choked you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;
                case "stare":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You're staring at " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " is staring at you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;
                case "spank":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've spanked " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " spanked you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break; 
                case "touch":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've touched " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " touched you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;
                case "harass":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've sexually harassed " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " sexually harassed you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break;    
                case "cheer":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've tried cheering " + victim.getName() + " up!");
                              victim.dropMessage(6,player.getName() + " is trying to cheer you up!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break; 
                case "spit":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You've spat at " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " spat at you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break; 
                case "weed":
                   if(sub.length > 1){
                      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                      if(victim != null)
                      {
                          if(player.getMeso() >= 50000000)
                          {
                              player.gainMeso(-50000000, true);
                              victim.gainMeso(50000000, true);
                              player.dropMessage(6,"You're smoking weed with " + victim.getName() + "!");
                              victim.dropMessage(6,player.getName() + " is smoking weed with you!");                              
                          }
                          else
                              player.dropMessage(5,"Error. You don't have enough money!");
                          
                      }
                      else
                          player.dropMessage(5,"Error. The player doesn't exist!");
                   }
                   else
                       player.dropMessage(5,"Error. Choose a player.");
                   break; 
                case "social":
                    player.dropMessage(6,"[Social commands]");
                    player.dropMessage(6,"@highfive | @rape | @kiss | @fuck | @slap");
                    player.dropMessage(6,"@kick | @punch | @yell | @hug | @poke");
                    player.dropMessage(6,"@choke | @stare | @spank | @touch | @harass");
                    player.dropMessage(6,"@cheer | @spit | @weed");
                    player.dropMessage(6,"Choose a victim and do with it as you please!");
                    break;
                case "tagger": 
                    if(sub.length > 1){
                    MapleCharacter tagdude = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if(tagdude.getName() != player.getName())
                     player.Tagger(player, tagdude);
                    else
                        player.dropMessage(5,"Error.. Please enter a name thats different than yours");
                    }
                    else
                        player.dropMessage(5,"Error. Please type the command as follows @tagger <name>");
                /*    if(!player.didChooseTagger()){
                    MapleCharacter tagguy = player.getMap().getCharacterByName(sub[1]);
                    if(tagguy != null){                        
                        BuddyList tagbl = player.getBuddylist(); 
                        int[] buddyids =tagbl.getBuddyIds();
                         BuddylistEntry[] savedbl = new BuddylistEntry[buddyids.length];
                         BuddylistEntry tryone;
                        for(int i =0 ;i < buddyids.length; i++){ 
                            savedbl[i] = tagbl.get(buddyids[i]);                            
                            tagbl.remove(buddyids[i]);
                        }
                        player.setSavedBL(savedbl);
                         BuddylistEntry ble = new BuddylistEntry(tagguy.getName(),"tagger",tagguy.getId(),c.getChannel(),true);    
                            tagbl.put(ble);  
                       player.setChosenTagger(true);
                       player.setTaggerId(tagguy.getId());
                      c.announce(MaplePacketCreator.updateBuddylist(tagbl.getBuddies()));
                      player.dropMessage(6,"You've set " + tagguy.getName() + " as tagger!");
                    }
                    else
                        player.dropMessage(6,"Error. please enter a players name whos currently in the map");
                    }
                    else
                        player.dropMessage(6,"Error. please first use @taggeroff before switching taggers"); */
                    break;  
                case "taggeroff":  
                    if(player.didChooseTagger()){ // This isnt even getting true, why? no idea, hmm let me try this then
                         player.setChosenTagger(false);                        
                          BuddylistEntry[] getsaved = player.getSavedBL();
                         player.getBuddylist().remove(player.getTaggerId());
                         if(getsaved.length > 0){ 
                        for(int j =0; j< getsaved.length;j++) // note to self: u still gotta remove tagger
                          player.getBuddylist().put(getsaved[j]);                       
                       
                         } 
                        c.announce(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies())); 
                        player.setChosenTagger(false);
                        player.dropMessage(6,"Tagger has been set off"); 
                    }
                    else
                        player.dropMessage(5, "Error. you've not chosen a tagger to begin with..");
                    break; 
                case "bomb":
                    if(player.getMap().bombermapOn()){
                       player.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), player.getPosition()); 
                    }
                    else
                        player.dropMessage(5,"Error. This isn't a bombermap! you're not allowed to spawn any bombs!");
                    break;
                case "tag":                   
                  //  if(playertagger.equals(player.getName())){ // This should do it, LETS TRY ITTTT WOO wait 1 sec , let me recheck
                      if((player.getMap().taggerOn() && player.getMap().getTaggerName().equals(player.getName())) || player.getMap().taggermapOn()) {
                    MapleMap map = player.getMap();
             List<MapleMapObject> players = map.getMapObjectsInRange(player.getPosition(), (double) 10000, Arrays.asList(MapleMapObjectType.PLAYER));
            for (MapleMapObject closeplayers : players) {
                MapleCharacter playernear = (MapleCharacter) closeplayers;
            if (playernear.isAlive() && playernear != player){  // Klaus u can play while i research nblue dot
                playernear.setHp(0);
                playernear.updateSingleStat(MapleStat.HP, 0);
                playernear.dropMessage(6, "You were too close to a tagger.");
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, playernear.getName() + " has been tagged. "));
               
                                            
             }
            }
          }
                    else{
                        player.dropMessage(5,"Error. You weren't given privileges to tag.");
                    }
              
        
   
        
                break;
		
               
		case "lastrestart":
		case "uptime":
			long milliseconds = System.currentTimeMillis() - Server.uptime;
			int seconds = (int) (milliseconds / 1000) % 60 ;
			int minutes = (int) ((milliseconds / (1000*60)) % 60);
			int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
			int days	= (int) ((milliseconds / (1000*60*60*24)));
 			player.yellowMessage("MapleInfinity has been online for " + days + " days " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
			break;
		case "gacha":
			if (player.gmLevel() == 0) { // Sigh, need it for now...
				player.yellowMessage("Player Command " + heading + sub[0] + " does not exist, see @help for a list of commands.");
				return false;
			}
			Gachapon gacha = null;
			String search = StringUtil.joinStringFrom(sub, 1);
			String gachaName = "";
			String [] namess = {"Henesys", "Ellinia", "Perion", "Kerning City", "Sleepywood", "Mushroom Shrine", "Showa Spa Male", "Showa Spa Female", "New Leaf City", "Nautilus Harbor"};
			int [] ids = {9100100, 9100101, 9100102, 9100103, 9100104, 9100105, 9100106, 9100107, 9100109, 9100117};
			for (int j = 0; j < namess.length; j++){
				if (search.equalsIgnoreCase(namess[j])){
					gachaName = namess[j];
					gacha = Gachapon.getByNpcId(ids[j]);
				}
			}
			if (gacha == null){
				player.yellowMessage("Please use @gacha <name> where name corresponds to one of the below:");
				for (String namesss : namess){
					player.yellowMessage(namesss);
				}
				break;
			}
			String output = "The #b" + gachaName + "#k Gachapon contains the following items.\r\n\r\n";
			for (int j = 0; j < 2; j++){
				for (int id : gacha.getItems(j)){
					output += "-" + MapleItemInformationProvider.getInstance().getName(id) + "\r\n";
				}
			}
			output += "\r\nPlease keep in mind that there are items that are in all gachapons and are not listed here.";
			c.announce(MaplePacketCreator.getNPCTalk(9010000, (byte) 0, output, "00 00", (byte) 0));
			break;
		case "whatdropsfrom":
			if (sub.length < 2) {
				player.dropMessage(5, "Please do @whatdropsfrom <monster name>");
				break;
			}
			String monsterName = StringUtil.joinStringFrom(sub, 1);
			output = "";
			int limit = 3;
			Iterator<Pair<Integer, String>> listIterator = MapleMonsterInformationProvider.getMobsIDsFromName(monsterName).iterator();
			for (int j = 0; j < limit; j++) {
				if(listIterator.hasNext()) {
					Pair<Integer, String> data = listIterator.next();
					int mobId = data.getLeft();
					String mobName = data.getRight();
					output += mobName + " drops the following items:\r\n\r\n";
					for (MonsterDropEntry drop : MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId)){
						try {
							String name = MapleItemInformationProvider.getInstance().getName(drop.itemId);
							if (name.equals("null") || drop.chance == 0){
								continue;
							}
							float chance = 1000000 / drop.chance / player.getDropRate();
							output += "- " + name + " (1/" + (int) chance + ")\r\n";
						} catch (Exception ex){
							continue;
						}
					}
					output += "\r\n";
				}
			}
			c.announce(MaplePacketCreator.getNPCTalk(9010000, (byte) 0, output, "00 00", (byte) 0));
			break;
		case "whodrops":
			if (sub.length < 2) {
				player.dropMessage(5, "Please do @whodrops <item name>");
				break;
			}
			String searchString = StringUtil.joinStringFrom(sub, 1);
			output = "";
			listIterator = MapleItemInformationProvider.getInstance().getItemDataByName(searchString).iterator();
			if(listIterator.hasNext()) {
				int count = 1;
				while(listIterator.hasNext() && count <= 3) {
					Pair<Integer, String> data = listIterator.next();
					output += "#b" + data.getRight() + "#k is dropped by:\r\n";
					try {
				PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE itemid = ? LIMIT 50");
						ps.setInt(1, data.getLeft());
				ResultSet rs = ps.executeQuery();
						while(rs.next()) {
							String resultName = MapleMonsterInformationProvider.getMobNameFromID(rs.getInt("dropperid"));
							if (resultName != null) {
								output += resultName + ", ";
							}
						}
						rs.close();
						ps.close();
					} catch (Exception e) {
						player.dropMessage("There was a problem retreiving the required data. Please try again.");
						e.printStackTrace();
						return true;
					}
					output += "\r\n\r\n";
					count++;
				}
			} else {
				player.dropMessage(5, "The item you searched for doesn't exist.");
				break;
			}
			c.announce(MaplePacketCreator.getNPCTalk(9010000, (byte) 0, output, "00 00", (byte) 0));
			break;
		case "dispose":
			NPCScriptManager.getInstance().dispose(c);
			c.announce(MaplePacketCreator.enableActions());
			c.removeClickedNPC();
			player.message("You've been disposed.");
			break;
		case "rates":
			c.resetVoteTime(); 
			player.setRates();
			player.yellowMessage("DROP RATE");
			player.message(">>Total DROP Rate: " + player.getDropRate() + "x");

			player.yellowMessage("MESO RATE");
			player.message(">>Base MESO Rate: " + c.getWorldServer().getMesoRate() + "x");
			player.message(">>Guild MESO Rate bonus: " + (player.getGuild() != null ? "1" : "0") + "x");
			player.message(">>Total MESO Rate: " + player.getMesoRate() + "x");

			player.yellowMessage("EXP RATE");
			player.message(">>Base Server EXP Rate: " + ServerConstants.EXP_RATE + "x");
			if(c.getWorldServer().getExpRate() > ServerConstants.EXP_RATE) {
				player.message(">>Event EXP bonus: " + (c.getWorldServer().getExpRate() - ServerConstants.EXP_RATE) + "x");
			}
			player.message(">>Voted EXP bonus: " + (c.hasVotedAlready() ? "1x" : "0x (If you vote now, you will earn an additional 1x EXP!)"));
			player.message(">>Total EXP Rate: " + player.getExpRate() + "x");
			
			if (player.getLevel() < 10) { 
				player.message("Players under level 10 always have 1x exp.");
			}
			break;
		case "online":
                      String playersoncc = "";
			for (Channel ch : Server.getInstance().getChannelsFromWorld(player.getWorld())) {
                                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
					/*if (!chr.isGM()) {
						player.message(" >> " + MapleCharacter.makeMapleReadable(chr.getName()) + " is at " + chr.getMap().getMapName() + ".");
					}*/
                                    playersoncc += chr.getName() + ", ";                                   
				}
                                if(playersoncc != "")
                                    playersoncc = playersoncc.substring(0, playersoncc.length()-2);
                                 player.dropMessage(6,"Players in Channel " + ch.getId() + ": " + playersoncc);
                                 playersoncc = "";
			}
			break;
                case "callgm":
		case "gm":
                    if(!player.isCommandcooldown()){
			if (sub.length < 3) { // #goodbye 'hi'
				player.dropMessage(5, "Your message was too short. Please provide as much detail as possible.");
				
			}
			String message = StringUtil.joinStringFrom(sub, 1);
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.sendYellowTip("[GM MESSAGE]:" + MapleCharacter.makeMapleReadable(player.getName()) + ": " + message));
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(1, message));
			FilePrinter.printError("gm.txt", MapleCharacter.makeMapleReadable(player.getName()) + ": " + message + "\r\n");
			player.dropMessage(5, "Your message '" + message + "' was sent to GMs.");
			player.dropMessage(5, tips[Randomizer.nextInt(tips.length)]);
                        player.setCommandcooldown(true);
                        player.cooldownCommand(300); // 5 minutes
                        
                    }
                    else
                        player.dropMessage(6,"Please wait 5 minutes before using @callgm again!");
			break;
		/* case "bug":
			if (sub.length < 2) {
				player.dropMessage(5, "Message too short and not sent. Please do @bug <bug>");
				break;
			}
			String message = joinStringFrom(sub, 1);
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.sendYellowTip("[BUG]:" + MapleCharacter.makeMapleReadable(player.getName()) + ": " + message));
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(1, message));
			FilePrinter.printError("bug.txt", MapleCharacter.makeMapleReadable(player.getName()) + ": " + message + "\r\n");
			player.dropMessage(5, "Your bug '" + message + "' was submitted successfully to our developers. Thank you!");
			break; */
		case "points":
			player.dropMessage(5, "You have " + c.getVotePoints() + " vote point(s).");
			if (c.hasVotedAlready()) {
				Date currentDate = new Date();
				int time = (int) ((int) 86400 - ((currentDate.getTime() / 1000) - c.getVoteTime())); //ugly as fuck
				hours = time / 3600;
				minutes = time % 3600 / 60;
				seconds = time % 3600 % 60;
				player.yellowMessage("You have already voted. You can vote again in " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds.");
			} else {
				player.yellowMessage("You are free to vote! Make sure to vote to gain a vote point!");
			}
			break;
                case "j":
                case "join":    
		case "joinevent":
                    if (!event.isEventBanned(c.getPlayer().getName()))
                    {
                    if (event.isRunning())
                    {
                        if (player.getClient().getChannel() == event.channelOn)
                        {
                            if (event.isOpen)
                            {
                            player.changeMap(event.eventMap);
                            if (player.gmLevel() < 1)
                            {
                            player.setHpMp(0);
                            }
                        }
                            else
                            {
                                player.dropMessage("The event gates are currently closed.");
                            }
                        }
                        else
                        {
                            player.dropMessage("You are not on the channel that the event is being hosted on!");
                        }
                    }
                    else
                    {
                        player.dropMessage("There is no event currently running right now.");
                    }
                    } else
                    {
                        player.dropMessage("You are banned from using @joinevent. Please message a Game Master.");
                    }
                    break;
		case "bosshp":
			for(MapleMonster monster : player.getMap().getMonsters()) {
				if(monster != null && monster.isBoss() && monster.getHp() > 0) {
					long percent = monster.getHp() * 100L / monster.getMaxHp();
					String bar = "[";
					for (int j = 0; j < 100; j++){
						bar += j < percent ? "|" : ".";
					}
					bar += "]";
					player.yellowMessage(monster.getName() + " has " + percent + "% HP left.");
					player.yellowMessage("HP: " + bar);
				}
			} 
			break;
		case "ranks":
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = DatabaseConnection.getConnection().prepareStatement("SELECT `characters`.`name`, `characters`.`level` FROM `characters` LEFT JOIN accounts ON accounts.id = characters.accountid WHERE `characters`.`gm` = '0' AND `accounts`.`banned` = '0' ORDER BY level DESC, exp DESC LIMIT 50");
				rs = ps.executeQuery();
				
				player.announce(MaplePacketCreator.showPlayerRanks(9010000, rs));
				ps.close();
				rs.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if(ps != null && !ps.isClosed()) {
						ps.close();
					}
					if(rs != null && !rs.isClosed()) {
						rs.close();
					}
				} catch (SQLException e) {
				}
			}
			break;
		default:
			
				player.yellowMessage("Player Command " + heading + sub[0] + " does not exist, see @help for a list of commands.");
			
			return false;
		}
		return true;
	}
        static boolean tempplayer = false; // Makes gms vulnerable to tag      
        static String eventstarter = "notagger";
}
