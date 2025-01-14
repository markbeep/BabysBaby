package BabyBaby.Command.commands.Public;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class SieveCMD implements PublicCMD {

    @Override
    public String getName() {
        return "sieve";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        
        String[] cmd = new String [cmds.size()];

        int dooku = 0;
        for (String arg : cmds) {
            cmd[dooku] = arg;
            dooku++;
        }

        HashSet<Member> counter = new HashSet<>();

        List<Member> tmp = ctx.getGuild().getMembers();
        if(cmd[0].equals(Data.ethid)){
            for (Member member : tmp) {
                counter.add(member);
            }
        } else {
            Role role1 = ctx.getGuild().getRoleById(cmd[0]);
            for (Member member : tmp) {
                if(member.getRoles().contains(role1))
                    counter.add(member);
            }
        }   

        for(int i = 1; i < cmd.length-1; i +=2){
            Role role = ctx.getGuild().getRoleById(cmd[i+1]);
            switch (cmd[i]){
                case "!":
                    List<Member> removerMembers = ctx.getGuild().getMembersWithRoles(role);
                    for (Member member : removerMembers) {
                        if(counter.contains(member)){
                            counter.remove(member);
                        }
                    }
                    break;
                case "&":
                    LinkedList<Member> save = new LinkedList<>();
                    for (Member member : counter) {
                        if(!member.getRoles().contains(role))
                            save.add(member);
                    }
                    counter.removeAll(save);
                    break;
                case "|":
                    List<Member> adderMem = ctx.getGuild().getMembersWithRoles(role);
                    for (Member member : adderMem) 
                        counter.add(member);
                    break;
            }
        }
        LinkedList<Member> sorted = new LinkedList<>(counter);
        Comparator<Member> idcomp = new Comparator<>(){
            @Override
            public int compare(Member o1, Member o2) {
                return (o1.getIdLong() - o2.getIdLong()>0) ? 1 : -1;
            }
        };
        sorted.sort(idcomp);

        String mention = "";
        for (Member member : sorted) {
            mention += member.getAsMention() + "\n";
        }
        String cmdrole = "";
        for(int i = 0; i < cmd.length; i ++){
            if(i%2==0)
                cmdrole += "<@&" + cmd[i] + "> ";
            else
                cmdrole += cmd[i] + " ";
        }


        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();

        LinkedList <String> cacherefresh = new LinkedList<>();

        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("People with ");
        eb.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
        eb.setColor(1);
        if(mention.length() <= 6000 - ((cmdrole.length()>1024) ? 1024 : cmdrole.length())){
            dooku = 0;
            while(mention.length() > 1024){
                mention = Helper.addFieldSieve(eb, cacherefresh, dooku++, mention);
            }
            cacherefresh.add(mention);
            eb.addField(""+dooku, mention, true);
        } else {
            ctx.getChannel().sendMessage("This is over 6000 chars (or empty), can't send this big messages. Sry!").queue();
            return;
        }
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
        


        Message editor = ctx.getChannel().sendMessage("wait a sec").complete();
        for (String pings : cacherefresh) {
            if(pings == null || pings.length()==0)
                continue;
            editor.editMessage(pings + " ").complete();
        }
        editor.delete().queue();

        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
        ctx.getMessage().addReaction(":checkmark:769279808244809798").queue();

    }   

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<roleID> {<!/&/|> <roleID>}", "Command to find out who has the Role AmongUs but also the Anime role for example.");
    }
    
}
