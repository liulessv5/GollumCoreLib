package com.gollum.core.common.command;

import java.util.List;

import com.gollum.core.common.building.Builder;
import com.gollum.core.common.building.Building.SubBuilding;
import com.gollum.core.common.building.BuildingParser;
import com.gollum.core.inits.ModItems;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class CommandBuilding extends CommandBase {

	@Override
	public String getCommandName()
	{
		return "building";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "building [reload, rebuild]";
	}
	
	public String getCommandUsageRebuild(ICommandSender sender)
	{
		return "building rebuild <num last building>";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] arguments) throws CommandException {
		
		if(arguments.length <= 0) {
			throw new WrongUsageException(this.getCommandUsage(sender));
		
		} else if(arguments[0].matches("reload")) {

			sender.addChatMessage(new ChatComponentText("Reload all buildings"));
			
			BuildingParser.reloadAll ();
			
		}  else if(arguments[0].matches("rebuild")) {
			try {
				int last = 0;
				
				if(arguments.length >= 2) {
					last = Integer.parseInt(arguments[1]);
				}
				
				SubBuilding subBuilding = ModItems.itemBuilding.getLastBuild (last);
				sender.addChatMessage(new ChatComponentText("Rebuild last building "+subBuilding.building.modId+":"+subBuilding.building.name));
				
				subBuilding.building = new BuildingParser().parse(subBuilding.building.name, subBuilding.building.modId);
				new Builder().build(sender.getEntityWorld(), subBuilding, true);
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new WrongUsageException(this.getCommandUsageRebuild(sender));
			}
			
		} else {
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}
	
	public List addTabCompletionOptions(ICommandSender sender, String[] arguments) {
		
		List rtn = null;

		rtn = (arguments.length == 1) ? getListOfStringsMatchingLastWord(arguments, new String[] {"reload", "rebuild"}) : rtn;
		
		return rtn;
	}
}
