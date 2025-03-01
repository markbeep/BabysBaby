package BabyBaby.Command.commands.Owner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;

/*
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
*/
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class TestCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        
        LinkedList<Member> allmem = new LinkedList<>(); 
        
        Role student = ctx.getGuild().getRoleById(Data.ethstudent);
        for (Member mem : ctx.getGuild().getMembers()) {   
            if(mem.getRoles().contains(student))
                allmem.add(mem);
        }

        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            for (Member mem : allmem) {
                pstmt = c.prepareStatement("INSERT OR IGNORE INTO VERIFIED (ID) VALUES (?);");
                pstmt.setString(1, mem.getId());
                pstmt.execute();    
                pstmt.close();
            }
            
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }
        ctx.getMessage().reply("Done!").complete();
    }   

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "(whatever it is atm)", "A cmd to test things out.");
    }
    /*
    private static String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(),                 c.getGreen(), c.getBlue());
    } 
    */
    
}
