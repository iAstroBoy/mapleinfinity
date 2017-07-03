/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import net.server.Server;
import net.server.world.World;
import provider.MapleDataProviderFactory;
import server.MapleInventoryManipulator;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.StringUtil;

/**
 *
 * @author Administrator
 */
public class AdminCommands {
    
    
    public static boolean executeAdminCommand(MapleClient c, String[] sub, char heading) {
		MapleCharacter player = c.getPlayer();
		switch (sub[0]) {
		case "sp":  //Changed to support giving sp /a
			if (sub.length == 2) {
				player.setRemainingSp(Integer.parseInt(sub[1]));
				player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
			} else {
				MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				victim.setRemainingSp(Integer.parseInt(sub[2]));
				victim.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
			}
			break;
                    
                    
		case "horntail":
			player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8810026), player.getPosition());
			break;
		case "packet":
			player.getMap().broadcastMessage(MaplePacketCreator.customPacket(StringUtil.joinStringFrom(sub, 1)));
			break;
		case "timerdebug":
			TimerManager tMan = TimerManager.getInstance();
			player.dropMessage(6, "Total Task: " + tMan.getTaskCount() + " Current Task: " + tMan.getQueuedTasks() + " Active Task: " + tMan.getActiveCount() + " Completed Task: " + tMan.getCompletedTaskCount());
			break;
		case "warpworld":
			Server server = Server.getInstance();
			byte worldb = Byte.parseByte(sub[1]);
			if (worldb <= (server.getWorlds().size() - 1)) {
				try {
					String[] socket = server.getIP(worldb, c.getChannel()).split(":");
					c.getWorldServer().removePlayer(player);
					player.getMap().removePlayer(player);//LOL FORGOT THIS ><                    
					c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
					player.setWorld(worldb);
					player.saveToDB();//To set the new world :O (true because else 2 player instances are created, one in both worlds)
					c.announce(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
				} catch (UnknownHostException | NumberFormatException ex) {
					player.message("Error when trying to change worlds, are you sure the world you are trying to warp to has the same amount of channels?");
				}

			} else {
				player.message("Invalid world; highest number available: " + (server.getWorlds().size() - 1));
			}
                        
			break;
		case "saveall"://fyi this is a stupid command
			for (World world : Server.getInstance().getWorlds()) {
				for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
					chr.saveToDB();
				}
			}
			String message = player.getName() + " used !saveall.";
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
			player.message("All players saved successfully.");
			break;
		case "dcall":
			for (World world : Server.getInstance().getWorlds()) {
				for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
					if (!chr.isGM()) {
						chr.getClient().disconnect(false, false);
					}
				}
			}
			player.message("All players successfully disconnected.");
			break;
		case "mapplayers"://fyi this one is even stupider
			//Adding HP to it, making it less useless.
			String names = "";
			int map = player.getMapId();
			for (World world : Server.getInstance().getWorlds()) {
				for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
					int curMap = chr.getMapId();
					String hp = Integer.toString(chr.getHp());
					String maxhp = Integer.toString(chr.getMaxHp());
					String name = chr.getName() + ": " + hp + "/" + maxhp;
					if (map == curMap) {
						names = names.equals("") ? name : (names + ", " + name);
					}
				}
			}
			player.message("These b lurkin: " + names);
			break;
		case "getacc":
			if (sub.length < 1) {
				player.message("Please provide an IGN.");
				break;
			}
			MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
			player.message(victim.getName() + "'s account name is " + victim.getClient().getAccountName() + ".");
			break;
		case "npc":
			if (sub.length < 1) {
				break;
			}
                        Integer npcid = Integer.parseInt(sub[1]);
			MapleNPC npc = MapleLifeFactory.getNPC(npcid);
			if (npc != null && MapleDataProviderFactory.fileInWZPath("Npc.wz/" + npcid + ".img.xml").exists()) {
				npc.setPosition(player.getPosition());
				npc.setCy(player.getPosition().y);
				npc.setRx0(player.getPosition().x + 50);
				npc.setRx1(player.getPosition().x - 50);
				npc.setFh(player.getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
				player.getMap().addMapObject(npc);
				player.getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc));
			}
                        else
                        {
                            player.dropMessage(6,"The npc isn't existent"); 
                        }
			break;
	/*	case "job": { //Honestly, we should merge this with @job and job yourself if array is 1 long only. I'll do it but gotta run at this point lel
			//Alright, doing that. /a
			if (sub.length == 2) {
				player.changeJob(MapleJob.getById(Integer.parseInt(sub[1])));
				player.equipChanged();
			} else if (sub.length == 3) {
				victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				victim.changeJob(MapleJob.getById(Integer.parseInt(sub[2])));
				player.equipChanged();
			} else {
				player.message("!job <job id> <opt: IGN of another person>");
			}
			break;
		} */
		case "playernpc":
			player.playerNPC(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]), Integer.parseInt(sub[2]));
			break; 
		case "shutdown":
		case "shutdownnow":
			int time = 60000;
			if (sub[0].equals("shutdownnow")) {
				time = 1;
			} else if (sub.length > 1) {
				time *= Integer.parseInt(sub[1]);
			}
			TimerManager.getInstance().schedule(Server.getInstance().shutdown(false), time);
			break;
		case "face":
			if (sub.length == 2) {
				player.setFace(Integer.parseInt(sub[1]));
				player.equipChanged();
			} else {
				victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				player.setFace(Integer.parseInt(sub[2]));
				player.equipChanged();
			}
			break;
		case "hair":
			if (sub.length == 2) {
				player.setHair(Integer.parseInt(sub[1]));
				player.equipChanged();
			} else {
				victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				player.setHair(Integer.parseInt(sub[2]));
				player.equipChanged();
			}
			break;
                    
                    
                     case "strip":
        	victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
        	victim.unequipEverything();
        	break;
                case "strip2":
        	victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
        	if (!victim.isGM())
        		victim.unequipAndDropEverything();
        	else
        		player.message("nopenope0");
        	break;
                    
		case "itemvac":
			List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));
			for (MapleMapObject item : items) {
				MapleMapItem mapitem = (MapleMapItem) item;
				if (!MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true)) {
					continue;
				}
				mapitem.setPickedUp(true);
				player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, player.getId()), mapitem.getPosition());
				player.getMap().removeMapObject(item);
			}
			break;
		case "zakum":
			player.getMap().spawnFakeMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), player.getPosition());
			for (int x = 8800003; x < 8800011; x++) {
				player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(x), player.getPosition());
			}
			break;
		case "clearquestcache":
			MapleQuest.clearCache();
			player.dropMessage(5, "Quest Cache Cleared.");
			break;
                    
                  case "fakesmega":
        	MapleCharacter victim1 = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
        	for (MapleCharacter chrs : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
    			if (chrs.getSmegaView())
    				chrs.announce(MaplePacketCreator.serverNotice(3, c.getChannel(), victim1.getMedalText() + " " + victim1.getName() + " : " + StringUtil.joinStringFrom(sub, 2), true));
    		}
        	break;  
                           
		case "clearquest":
			if(sub.length < 1) {
				player.dropMessage(5, "Plese include a quest ID.");
			}
                        else
                        {
			MapleQuest.clearCache(Integer.parseInt(sub[1]));
			player.dropMessage(5, "Quest Cache for quest " + sub[1] + " cleared.");
                        }
			break;
		default:
			player.yellowMessage("Command " + heading + sub[0] + " does not exist.");
			return false;
		}
                return true;
	}
}

