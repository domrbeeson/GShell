package gg.gateway.gshell;

import gg.gateway.gshell.interfaces.GProcessInfo;
import gg.gateway.gshell.interfaces.GProcessManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class GShell extends JavaPlugin {

    private final GProcessManager processManager = new DefaultProcessManager(this, getServer().getScheduler());

    @Override
    public void onEnable() {

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
