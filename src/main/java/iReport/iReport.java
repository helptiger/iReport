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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class IReport extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("iReport");
    public static MYSQL sql;
    private final File reportsfile;
    private YamlConfiguration newConfig;
    private static final CommandExecutor DREPORT = new Dreport();
    private static final CommandExecutor REPORTS = new Reports();

    public IReport() {
        this.reportsfile = new File(getDataFolder(), "reports.yml");
    }

    public static MYSQL getMYSQL() {
        if (sql == null) {
            try {
                sql = new MYSQL();
                sql.queryUpdate("CREATE TABLE IF NOT EXISTS reports (uuid VARCHAR(36) PRIMARY KEY, currentname VARCHAR(16), Report LONGTEXT, username VARCHAR(16))");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("dreport") && args.length == 1) {
            return DREPORT.onCommand(sender, command, label, args);
        }
        if (label.equalsIgnoreCase("reports")) {
            return REPORTS.onCommand(sender, command, label, args);
        }

        return false;
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

        getMYSQL();
        try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(new File(getDataFolder(), "data.bin")))) {
            Data.instens = (Data) o.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (sql.isenable && sql.hasConnection()) {
            sql.closeConnection();
        }
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(getDataFolder(), "data.bin")));) {
            o.writeObject(Data.init());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getReports() {
        if (newConfig == null) {
            newConfig = YamlConfiguration.loadConfiguration(reportsfile);

            InputStream defConfigStream = getResource("reports.yml");
            if (defConfigStream != null) {
                @SuppressWarnings("deprecation")
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

                newConfig.setDefaults(defConfig);
            }
        }
        return newConfig;
    }

    public void saveReports() {
        try {
            getReports().save(reportsfile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not save config to " + reportsfile, ex);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Stream<String> set = Data.init().playermapo.keySet().parallelStream().map(UUID::toString);
        if (sender.hasPermission("iReport.dreport") && alias.equalsIgnoreCase("dreport")) {
            return set.filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        if (sender.hasPermission("iReport.reports") && alias.equalsIgnoreCase("reports")) {
            if (args.length < 2) {
                List<String> l = new ArrayList<>();
                if ("uuid".startsWith(args[0].toLowerCase())) {
                    l.add("uuid");
                }
                if ("usernameo".startsWith(args[0].toLowerCase())) {
                    l.add("usernameo");
                }
                return l;
            }
            if (args[0].equalsIgnoreCase("uuid")) {
                return set.filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            }
            if (args[0].equalsIgnoreCase("usernameo")) {
                return Data.init().playermapo.values().parallelStream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            }
        }
        return null;
    }
}