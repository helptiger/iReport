package iReport;

import iReport.commands.Dreport;
import iReport.commands.HReport;
import iReport.commands.Reports;
import iReport.commands.greport;
import iReport.commands.ireportc;
import iReport.commands.sreport;
import iReport.mysql.MYSQL;
import iReport.util.Data;
import iReport.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class iReport extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("iReport");
    public MYSQL sql;
    private File reportsfile;
    private YamlConfiguration newConfig;

    public iReport() {
        this.reportsfile = new File(getDataFolder(), "reports.yml");
    }

    public MYSQL getMYSQL() {
        if (this.sql == null) {
            try {
                this.sql = new MYSQL();
                if (MYSQL.isenable) {
                    this.sql.queryUpdate("CREATE TABLE IF NOT EXISTS Reports (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(16), Reason VARCHAR (100))");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("dreport")) {
            return new Dreport(this).onCommand(sender, command, label, args);
        }
        if (label.equalsIgnoreCase("reporta")) {
            return new Reports(this).onCommand(sender, command, label, args);
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void onEnable() {
        try {
            File f = new File("plugins/iReport/", "config.yml");
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                if (sc.nextLine().contains("reports:")) {
                    sc.close();
                    if (f.renameTo(new File("plugins/iReport/", "reports.yml"))) {
                        break;
                    } else {
                        try {
                            throw new IOException("fail to rename file config.yml, iReport will not load");
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
        }
        getCommand("greport").setExecutor(new greport(this));
        getCommand("hreport").setExecutor(new HReport(this));
        getCommand("sreport").setExecutor(new sreport(this));
        getCommand("ireport").setExecutor(new ireportc());
        getServer().getPluginManager().registerEvents(new Utils(), this);

        getConfig().addDefault("reports.reportnewusername", false);
        saveConfig();
        getMYSQL();
        try {
            ObjectInputStream o = new ObjectInputStream(new FileInputStream(new File(getDataFolder(), "data.bin")));
            Data.instens = (Data) o.readObject();
            o.close();
        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (MYSQL.isenable && sql.hasConnection()) {
            sql.closeConnection();
        }
        try {
            ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(getDataFolder(), "data.bin")));
            o.writeObject(Data.init());
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration grtReports() {
        if (newConfig == null) {
            newConfig = YamlConfiguration.loadConfiguration(reportsfile);

            InputStream defConfigStream = getResource("reports.yml");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

                newConfig.setDefaults(defConfig);
            }
        }
        return newConfig;
    }

    public void saveReports() {
        try {
            grtReports().save(reportsfile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not save config to " + reportsfile, ex);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("iReport.dreport") && alias.equalsIgnoreCase("dreport")) {
            List<String> list = new ArrayList<String>();
            for (String string : Data.init().playerlistu) {
                if (string.contains(args[0])) {
                    list.add(string);
                }
                if (args.length != 1) {
                    list.add(string);
                }
            }
            return list;
        }
        if (sender.hasPermission("iReport.reports") && alias.equalsIgnoreCase("reports") && args.length == 1) {
            List<String> list = new ArrayList<String>();
            if (args[0].toLowerCase().startsWith("uuid:")) {
                for (String string : Data.init().playerlistu) {
                    String[] s = args[0].split(":");
                    try {
                        if (string.contains(s[1]) && args[0].length() < 41) { 
                            list.add(args[0]+string.substring(s[1].length()));
                            continue;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // TODO: handle exception
                    }
                    if (args.length == 1 && args[0].length() < 41 && args[0].equalsIgnoreCase("uuid:")) {
                        list.add(args[0]+string);
                    }
                }
            }
            if (args[0].toLowerCase().startsWith("usernameo:")) {
                for (String string : Data.init().playerlistn) {
                    if (string.contains(args[0])) {
                        list.add(args[0]+string);
                    }
                    if (args.length == 1 && args[0].length() < 41) {
                        list.add(args[0]+string);
                    }
                }
            }
            return list;
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}