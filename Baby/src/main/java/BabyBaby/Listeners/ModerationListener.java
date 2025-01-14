package BabyBaby.Listeners;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.internal.managers.ChannelManagerImpl;

import BabyBaby.data.Data;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class ModerationListener extends ListenerAdapter{
    
    //Invite
    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid))
            return;
        
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.prepareStatement("INSERT INTO INVITES (URL, AMOUNT) VALUES (?, 0) ;");
            stmt.setString(1, event.getUrl());
            stmt.executeUpdate();
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }
    }

    //Member Join
    @Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if(!event.getGuild().getId().equals(Data.ethid))
            return;

        OffsetDateTime time = event.getUser().getTimeCreated();
		String username = event.getUser().getName().toLowerCase();

		if(username.contains("lengler") || username.contains("welzl")){
			event.getGuild().getTextChannelById("747754931905364000").sendMessage("<@&773908766973624340> Account with Prof name joined. Time of creation of the account:" + time).queue();
		}

        
        List<Invite> inv = event.getGuild().retrieveInvites().complete();
        HashMap<String, Invite> urls = new HashMap<>();
        for (Invite invite : inv) {
            urls.put(invite.getUrl(), invite);
        }
        
        
        
        String url = "";
        int amount = 0;
        boolean found = false;

        
		
        MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);

        if(event.getUser().isBot()){
            memberJoinModLog("Admin (Bot addition)", event.getUser(), log, url, "NaN", 1);
            return;
        }

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM INVITES;");
            while (rs.next()) {

                url = rs.getString("URL");
                amount = rs.getInt("AMOUNT");
                
                try{
                    Invite temp = urls.get(url);
                    if(temp.getUses() > amount){
                        found = true;
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        if(!found){
            try {        
                VanityInvite vanity = event.getGuild().retrieveVanityInvite().complete();
                memberJoinModLog("Admin (Vanity URL)", event.getUser(), log, vanity.getUrl(), "NaN", vanity.getUses());
            } catch (Exception e) {
                event.getGuild().getTextChannelById("747768907992924192").sendMessage("Smth went wrong with the invite link stuff. Couldnt find the invite link... <@!223932775474921472>").queue();
            }
            return;
        }

        DateTimeFormatter linkcreate = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");
        String timecreate = urls.get(url).getTimeCreated().toLocalDateTime().format(linkcreate);
        
        memberJoinModLog(urls.get(url).getInviter().getAsMention(), event.getUser(), log, url, timecreate, urls.get(url).getUses());


        if(urls.get(url) != null) {
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
                pstmt = c.prepareStatement("UPDATE INVITES SET AMOUNT = ? where URL = ? ;");
                pstmt.setInt(1, amount);
                pstmt.setString(2, url);
                pstmt.executeUpdate();
                
                pstmt.close();
                c.close();
            } catch ( Exception e ) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
	}


    private void memberJoinModLog (String inviter, User joined, MessageChannel log, String url, String invCreate, int uses){
        
        DateTimeFormatter createtime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
        OffsetDateTime created = joined.getTimeCreated();
        OffsetDateTime now = OffsetDateTime.now();
        int day = now.getDayOfYear() - created.getDayOfYear();
        day = (day<0)? 365+day:day;
        int year =  now.getYear() - created.getYear() + ((now.getDayOfYear()<created.getDayOfYear())?-1:0);

        String multyear = ((year + Math.round(day/365.0)) == 1) ? " year ago" : " years ago";
        String multday = (day== 1) ? " day ago" : " days ago";
        String actualtime = (year >0) ?  (year + Math.round(day/365.0)) + multyear : day + multday;

        
        String acccrea = "Account created at: **" + joined.getTimeCreated().format(createtime) + "** `(" + actualtime + ")`"; 
		

        String desc = "User that joined " + joined.getAsMention() +  "\n" +
                        "Used Link: " + url + "\n Creator: " + inviter + "\n" +
                        "Uses: " + uses + "\n"+
                        "Invite created at: " + invCreate + "\n" +
                        acccrea;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(joined.getAsTag() + " (" + joined.getId() + ")", joined.getAvatarUrl(), joined.getAvatarUrl());
        eb.setColor(1);
        eb.setThumbnail(joined.getAvatarUrl());
        eb.setDescription(desc);

        log.sendMessage("cache reload").complete().editMessage(inviter + joined.getAsMention()).complete().editMessageEmbeds(eb.build()).complete();

    }


    @Override
    public void onGuildMemberRoleAdd (GuildMemberRoleAddEvent event){
        if(!event.getGuild().getId().equals(Data.ethid))
            return;

        Role student = event.getGuild().getRoleById(Data.ethstudent);

        if(!event.getRoles().contains(student))
            return;

        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            pstmt = c.prepareStatement("INSERT OR IGNORE INTO VERIFIED (ID) VALUES (?);");
            pstmt.setString(1, event.getUser().getId());
            pstmt.execute();
            
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }
        
    }

    //Channel Create
    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid))
            return;
        ChannelManager test = new ChannelManagerImpl(event.getChannel());
        Collection<Permission> deny = new LinkedList<>();
        deny.add(Permission.MESSAGE_WRITE);
		IPermissionHolder permHolder = event.getGuild().getRoleById(Data.stfuID);
        test.putPermissionOverride(permHolder, null, deny).queue();
    }

    //Voice Channel Create
    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid))
            return;
        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.CHANNEL_CREATE)){
                if(entry.getUser().getId().equals(Data.dcvd))
                    return;
                else
                    break;
            }
        }
        
        ChannelManager channelMan = new ChannelManagerImpl(event.getChannel());
        Collection<Permission> deny = new LinkedList<>();
        deny.add(Permission.VOICE_SPEAK );
		IPermissionHolder permHolder = event.getGuild().getRoleById(Data.stfuID);
        channelMan.putPermissionOverride(permHolder, null, deny).queue();
    }

    



    //Member Remove
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.KICK)){
                if(!Data.kick.equals(entry.getTimeCreated())){

                    Data.kick = entry.getTimeCreated();
                    MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setAuthor(entry.getUser().getAsTag() + " (" + entry.getUser().getId() + ")", entry.getUser().getAvatarUrl(), entry.getUser().getAvatarUrl());
                    eb.setColor(0);
                    eb.setThumbnail(entry.getUser().getAvatarUrl());
                    Member warned = event.getMember();
                    log.sendMessage("cache reload").complete().editMessage(warned.getAsMention() + " " + entry.getUser().getAsMention()).complete().delete().complete();
                    

                    eb.setDescription(":warning: **Kicked** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + entry.getReason());

                    log.sendMessageEmbeds(eb.build()).queue();

                }
                break;
            }  
        }
        
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.BAN)){
                if(!Data.ban.equals(entry.getTimeCreated())){

                    Data.ban = entry.getTimeCreated();
                    MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);

                    
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setAuthor(entry.getUser().getAsTag() + " (" + entry.getUser().getId() + ")", entry.getUser().getAvatarUrl(), entry.getUser().getAvatarUrl());
                    eb.setColor(0);
                    eb.setThumbnail(entry.getUser().getAvatarUrl());
                    Member warned = event.getMember();
                    
                    eb.setDescription(":warning: **Banned** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + entry.getReason());
                    log.sendMessage("cache reload").complete().editMessage(warned.getAsMention() + " " + entry.getUser().getAsMention()).complete().delete().complete();

                    
                    log.sendMessageEmbeds(eb.build()).queue();

                }
                break;
            }  
        }
    }



}
