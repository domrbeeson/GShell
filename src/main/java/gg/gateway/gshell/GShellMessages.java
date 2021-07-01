package gg.gateway.gshell;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;

public class GShellMessages {

    private static final String COMMAND_PREFIX = ChatColor.translateAlternateColorCodes('&',"&0&l❰❰&f&lGShell&0&l❱❱&r ");
    public static final String NO_PERMISSION_MESSAGE = ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&cNo permission.");

    static String COMMAND_MISSING_ARGS_MESSAGE(String label, String subcommand, String missingArgs) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&7/" + label + " &7" + subcommand + " &f" + missingArgs); }

    static String INVALID_ID_MESSAGE = ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&cID must be a whole number.");
    static String PROCESS_NOT_RUNNING_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&cProcess not running. [ID: " + id + "]"); }
    static String PROGRAM_NOT_FOUND_MESSAGE(String program) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&cProgram not found: &f\"" + program + "\""); }
    static String PROCESS_EXIT_MESSAGE(long id, int exitCode) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&3Process exited with code " + exitCode + ". &b[ID: " + id + "]"); }
    static String PROCESS_STARTED_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&2Started process. &a[ID: " + id + "]"); }
    static String PROCESS_STARTED_QUIET_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&2Started process quietly. &a[ID: " + id + "]"); }
    static String RUNNING_PROCESSES_HEADER_MESSAGE(int size) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&2Running processes: &a[" + size + "]"); }
    static String RUNNING_PROCESSES_BODY_MESSAGE(long id, String startedBy, String[] args) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + " &7[ID: " + id + "] &fStarted by: " + startedBy + ", Args: \"" + String.join(" ", args) + "\""); }
    static String PROCESS_STOP_ATTEMPT_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&7Attempting to stop process... [ID: " + id + "]"); }
    static String PROCESS_STOP_SUCCESS_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&2Process stopped successfully. &a[ID: " + id + "]"); }
    static String PROCESS_STOP_FAIL_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&cProcess stop failed. [ID: " + id + "]"); }
    static String PROCESS_LISTEN_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', "&2Listening to process output. &a[ID: " + id + "]"); }
    static String PROCESS_UNLISTEN_MESSAGE(long id) { return ChatColor.translateAlternateColorCodes('&', "&2Stopped listening to process output. &a[ID: " + id + "]"); }

    static String[] HELP_MESSAGES(String label, Permissible user) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("");
        lines.add(ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "&2Arguments for &a/" + label));

        if (user.hasPermission(GShellPerms.RUN.toString())) lines.add(ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "run <args...> &7runs a process"));
        if (user.hasPermission(GShellPerms.QUIET.toString())) lines.add(ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "quiet <args...> &7runs a process with no output"));
        if (user.hasPermission(GShellPerms.LIST.toString())) lines.add(ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "list &7shows running processes"));
        if (user.hasPermission(GShellPerms.STOP.toString())) lines.add(ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "stop <id> &7stops a process"));
        if (user.hasPermission(GShellPerms.KILL.toString())) lines.add(ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "kill <id> &7forcefully stops a process"));
        if (user.hasPermission(GShellPerms.LISTEN.toString())) lines.add(ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + "listen <id> &7toggles process output to your chat"));

        return lines.toArray(new String[0]);
    }

}
