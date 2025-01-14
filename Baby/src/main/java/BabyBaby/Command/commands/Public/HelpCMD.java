package BabyBaby.Command.commands.Public;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.CmdHandler;
import BabyBaby.Command.*;
import BabyBaby.data.Data;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;

public class HelpCMD implements PublicCMD {
    String sigchange = "";

    Comparator<PublicCMD> compPub = new Comparator<>(){
        @Override
        public int compare(PublicCMD o1, PublicCMD o2) {
            return o1.getName().charAt(0) - o2.getName().charAt(0);
        }
    };
    Comparator<AdminCMD> compAdm = new Comparator<>(){
        @Override
        public int compare(AdminCMD o1, AdminCMD o2) {
            return o1.getName().charAt(0) - o2.getName().charAt(0);
        }
    };
    Comparator<OwnerCMD> compOwn = new Comparator<>(){
        @Override
        public int compare(OwnerCMD o1, OwnerCMD o2) {
            return o1.getName().charAt(0) - o2.getName().charAt(0);
        }
    };

    private final CmdHandler manager;

    public HelpCMD(CmdHandler manager) {
        this.manager = manager;
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        String prefix = getPrefix(ctx.getGuild().getId());

        // no search text given -> general help
        if (args == null || args.isEmpty()) {
            generalHelp(manager, prefix, 0, channel);
            return;
        }

        // specific help
        String search = args.get(0);
        PublicCMD cmd = manager.searchPublicCommand(search);

        // command does not exist -> error message
        if (cmd == null) {
            commandNotFound(channel, search, prefix);
            return;
        }
        // command does exits -> command specific help
        MessageEmbed help = cmd.getPublicHelp(prefix);
        if (help != null) {
            channel.sendMessageEmbeds(help).queue();
        }
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();
        String prefix = getPrefix(ctx.getGuild().getId());

        // no search text given -> general help
        if (args == null || args.isEmpty()) {
            generalHelp(manager, prefix, 1, channel);
            return;
        }

        // specific help
        String search = args.get(0);
        AdminCMD cmd = manager.searchAdminCommand(search);

        // command does not exist -> error message
        if (cmd == null) {
            commandNotFound(channel, search, prefix);
            return;
        }
        // command does exits -> command specific help
        channel.sendMessageEmbeds(cmd.getAdminHelp(prefix)).queue();
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();
        String prefix = getPrefix(ctx.getGuild().getId());

        // no search text given -> general help
        if (args == null || args.isEmpty()) {
            generalHelp(manager, prefix, 2, channel);
            return;
        }

        // specific help
        String search = args.get(0);
        OwnerCMD cmd = manager.searchOwnerCommand(search);

        // command does not exist -> error message
        if (cmd == null) {
            commandNotFound(channel, search, prefix);
            return;
        }
        // command does exits -> command specific help
        channel.sendMessageEmbeds(cmd.getOwnerHelp(prefix)).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName() + "`");
        embed.setDescription("A command that shows you what my bot can or can't do.");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use:")
                .addOrange(prefix + "help")
                .build(), false);

        // specific command
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("specific command:")
                .addNormal("with this subcommand you can see what another command does.")
                .addOrange(prefix + "help <command>")
                .build(), false);

        return embed.build();
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist");
    }

    void commandNotFound(MessageChannel channel, String search, String prefix) {
        channel.sendMessageEmbeds(
                EmbedUtils.getDefaultEmbed()
                        .setTitle("Error: Command `" + search + "` not found")
                        .setDescription(new ColouredStringAsciiDoc()
                                .addBlueAboveDash("try " + prefix + "help to see all commands")
                                .build())
                        .build()
        ).queue();
    }

    private String getPrefix(String id){
        Connection c = null;
        Statement stmt = null;
		ResultSet rs;
        String prefix = "+";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            c.setAutoCommit(false);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT PREFIX FROM GUILD WHERE ID = "  + id + ";");
            
            String tmp = rs.getString("PREFIX");
            if(tmp!= null)
                prefix = tmp;
            

            rs.close();
            stmt.close();
            c.close();
            } catch ( Exception e ) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        return prefix;
    }

    private String publicCMDs (CmdHandler manager, String prefix){
        String publicstr = ""; 
        List<PublicCMD> publiclist = manager.getPublicCommands();
        publiclist.sort(compPub);
        for (PublicCMD publicCMD : publiclist) {
            publicstr += prefix + publicCMD.getName() +  "\n";
        }
        return publicstr;
    }
    private String adminCMDs (CmdHandler manager, String prefix){
        String admin = ""; 
        List<AdminCMD> adminlist = manager.getAdminCommands();
        adminlist.sort(compAdm);
        for (AdminCMD adminCMD : adminlist) {
            admin += prefix + adminCMD.getName() +  "\n";
        }
        return admin;
    }

    private String ownerCMDs (CmdHandler manager, String prefix){
        String ownerstr = ""; 
        List<OwnerCMD> ownerlist = manager.getOwnerCommands();
        ownerlist.sort(compOwn);
        for (OwnerCMD ownerCMD : ownerlist) {
            ownerstr += prefix + ownerCMD.getName() +  "\n";
        }
        return ownerstr;
    }

    private void generalHelp(CmdHandler manager, String prefix, int rank, TextChannel channel){
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("BabysBaby");
        embed.setDescription("Significant changes: " + sigchange);

        embed.addField("", new ColouredStringAsciiDoc().addBlueAboveEq("Public CMD").addDiff(publicCMDs(manager, prefix)).build(), true);
        
        if(rank>0)
            embed.addField("", new ColouredStringAsciiDoc().addBlueAboveEq("Admin CMD").addDiff(adminCMDs(manager, prefix)).build(), true);
        
        if(rank>1)
            embed.addField("", new ColouredStringAsciiDoc().addBlueAboveEq("Owner CMD").addDiff(ownerCMDs(manager, prefix)).build(), true);

        embed.setFooter("With most CMDS you can get help how to use them by writing " + prefix + "help <cmdname>. For example " + prefix + "help ping");
        channel.sendMessageEmbeds(embed.build()).queue();
    }

}
