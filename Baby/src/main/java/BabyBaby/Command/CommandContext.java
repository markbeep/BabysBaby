package BabyBaby.Command;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;


public class CommandContext implements ICommandContext {

    private final GuildMessageReceivedEvent event;
    private final List<String> args;
    private final int permissionLevel;

    public CommandContext(GuildMessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
        Member member = this.getMember();

        Role mod = event.getGuild().getRoleById("815932497920917514");

        // owner -> 2
        // admin -> 1
        // public -> 0
        permissionLevel = member.getId().equals("223932775474921472") ? 2 : event.getGuild().getId().equals("747752542741725244") && (member.hasPermission(Permission.ADMINISTRATOR) || member.getRoles().contains(mod)) ? 1 : 0;
    }
    


    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    public List<String> getArgs() {
        return this.args;
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }
}