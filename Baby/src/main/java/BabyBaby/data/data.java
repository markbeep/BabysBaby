package BabyBaby.data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Data {
    public static boolean antibamboozle = true;
    //TODO Remove msgid and check on msgToChan
    public static HashSet<String> msgid = new HashSet<>();
    public static HashMap<String, ArrayList<String>> catToMsg = new HashMap<>();
    public static HashMap<String, String> msgToChan = new HashMap<>();
    public static HashMap<String, String> emoteassign = new HashMap<>();
    public static HashSet<String> roles = new HashSet<>();
    public static final String check = ":checkmark:769279808244809798";
    public static final String xmark = ":xmark:769279807916998728";
    public static final String db = "jdbc:sqlite:testone.db";
    public static final String modlog = "774322031688679454";
    public static final String adminlog = "774322847812157450";
    public static final String ethid = "747752542741725244";
    public static final String spam = "768600365602963496";
    public static final String stfuID = "765542118701400134";
    public static final String blindID= "844136589163626526";
    public static OffsetDateTime kick;
    public static OffsetDateTime ban;
    public static final String myselfID = "223932775474921472";
    public static final String dcvd = "306523617188118528";
    public static final String ethstudent = "747786383317532823";
    public static final String ethexternal = "767315361443741717";
    public static final String ETH_NEWCOMERS_CH_ID = "815881148307210260";
    public static boolean elthision = false;
    public static boolean marc = false;
    public static HashSet<String> buttonid = new HashSet<>();
    public static HashSet<String> stats = new HashSet<>();
    public static int mydel = 0;
    public static int otherdel = 0;
    
    

    //Filepaths
    public static final String TOKEN = "token.txt";
    public static final String SUGGESTION = "suggestions.txt";
    public static final String PLACE = "place/";
}