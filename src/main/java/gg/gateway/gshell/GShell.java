package gg.gateway.gshell;

import gg.gateway.gshell.interfaces.GProcessInfo;
import gg.gateway.gshell.interfaces.GProcessManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class GShell extends JavaPlugin {

    private final GProcessManager processManager = new DefaultProcessManager(this, getServer().getScheduler());

    public static final String PLUGIN_FOLDER = "plugins/GShell";
    public static final String PROCESS_LOGS_FOLDER = PLUGIN_FOLDER + "/logs";

    @Override
    public void onEnable() {

        new File(PLUGIN_FOLDER).mkdirs();
        new File(PROCESS_LOGS_FOLDER).mkdirs();

        getCommand("gshell").setExecutor(new GShellCommand(processManager));

    }

    @Override
    public void onDisable() {

        // Kill all processes started by this plugin
        for (Map.Entry<Long, GProcessInfo> entry : processManager.listRunningProcesses().entrySet()) {
            long id = entry.getKey();

            boolean killed = processManager.stopProcess(id, true);

            if (killed) getLogger().info("Killed ID " + id + ".");
            else getLogger().info("Failed to kill ID " + id + "!");

        }

    }

}
