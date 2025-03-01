package BabyBaby.Command.commands.Owner;

import java.io.IOException;

import BabyBaby.CmdHandler;
import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;


public class AdminHelpCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "adminhelp";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        
        CmdHandler handler;
        try {
            handler = new CmdHandler(null);
            AdminCMD cmd = handler.searchAdminCommand("help");
            cmd.handleAdmin(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName()+"`");
        embed.setDescription("Get Help page of an admin.");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addOrange(prefix + getName())
                .build(), false);

        return embed.build();
    }
    
}
