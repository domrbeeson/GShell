package gg.gateway.gshell;

import gg.gateway.gshell.interfaces.GProcessInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.Date;

public class GShellMessages {

    private static final String COMMAND_PREFIX = ChatColor.translateAlternateColorCodes('&',"&0&l❰❰&f&lGShell&0&l❱❱&r ");
    public static final String NO_PERMISSION_MESSAGE = convertChatColours("&cNo permission.");

    private static String convertChatColours(String message) {
        return ChatColor.translateAlternateColorCodes('&', COMMAND_PREFIX + message);
    }

    static String COMMAND_MISSING_ARGS_MESSAGE(String label, String subcommand, String missingArgs) { return convertChatColours("&7/" + label + " &7" + subcommand + " &f" + missingArgs); }

    static String INVALID_ID_MESSAGE = convertChatColours("&cID must be a whole number.");
    static String PROCESS_NOT_RUNNING_MESSAGE(long id) { return convertChatColours("&cProcess not running. [ID: " + id + "]"); }
    static String PROGRAM_NOT_FOUND_MESSAGE(String program) { return convertChatColours("&cProgram not found: &f\"" + program + "\""); }
    static String PROCESS_EXIT_MESSAGE(long id, int exitCode) { return convertChatColours("&3Process exited with code " + exitCode + ". &b[ID: " + id + "]"); }
    static String PROCESS_STARTED_MESSAGE(long id) { return convertChatColours("&2Started process. &a[ID: " + id + "]"); }
    static String PROCESS_STARTED_QUIET_MESSAGE(long id) { return convertChatColours("&2Started process quietly. &a[ID: " + id + "]"); }
    static String RUNNING_PROCESSES_HEADER_MESSAGE(int size) { return convertChatColours("&2Running processes: &a[" + size + "]"); }
    static String RUNNING_PROCESSES_BODY_MESSAGE(long id, String startedBy, String[] args) { return convertChatColours(" &7[ID: " + id + "] &fStarted by: " + startedBy + ", Args: \"" + String.join(" ", args) + "\""); }
    static String PROCESS_STOP_ATTEMPT_MESSAGE(long id) { return convertChatColours("&7Attempting to stop process... [ID: " + id + "]"); }
    static String PROCESS_STOP_SUCCESS_MESSAGE(long id) { return convertChatColours("&2Process stopped successfully. &a[ID: " + id + "]"); }
    static String PROCESS_STOP_FAIL_MESSAGE(long id) { return convertChatColours("&cProcess stop failed. [ID: " + id + "]"); }
    static String PROCESS_LISTEN_MESSAGE(long id) { return convertChatColours("&2Listening to process output. &a[ID: " + id + "]"); }
    static String PROCESS_UNLISTEN_MESSAGE(long id) { return convertChatColours("&2Stopped listening to process output. &a[ID: " + id + "]"); }

    static String[] HELP_MESSAGES(String label, Permissible user) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("");
        lines.add(convertChatColours("&2Arguments for &a/" + label));

        if (user.hasPermission(GShellPerms.RUN.toString())) lines.add(convertChatColours("run <args...> &7runs a process"));
        if (user.hasPermission(GShellPerms.QUIET.toString())) lines.add(convertChatColours("quiet <args...> &7runs a process with no output"));
        if (user.hasPermission(GShellPerms.LIST.toString())) lines.add(convertChatColours("list &7shows running processes"));
        if (user.hasPermission(GShellPerms.STOP.toString())) lines.add(convertChatColours("stop <id> &7stops a process"));
        if (user.hasPermission(GShellPerms.KILL.toString())) lines.add(convertChatColours("kill <id> &7forcefully stops a process"));
        if (user.hasPermission(GShellPerms.LISTEN.toString())) lines.add(convertChatColours("listen <id> &7toggles process output to your chat"));
        if (user.hasPermission(GShellPerms.INFO.toString())) lines.add(convertChatColours("info <id> &7shows detailed process information"));

        return lines.toArray(new String[0]);
    }

    static String[] PROCESS_INFO_MESSAGES(long id, GProcessInfo info) {
        String[] lines = new String[6];

        lines[0] = convertChatColours("&2Process info: &a[ID: " + id + "]");
        lines[1] = convertChatColours(" Executor: &7" + GShell.convertCommandSenderToName(info.getExecutor()));
        lines[2] = convertChatColours(" Start time: &7" + info.getStartTime().toString());
        int runningTime = (int) Math.floor((new Date().getTime() - info.getStartTime().getTime()) / 1000.0);
        lines[3] = convertChatColours(" Running time: &7" + runningTime + (runningTime == 1 ? " second" : " seconds"));
        lines[4] = convertChatColours(" Arguments: &7\"" + String.join(" ", info.getArgs()) + "\"");

        String[] listenerUsernames = new String[info.getOutputListeners().size()];
        int i = 0;
        for (CommandSender listener : info.getOutputListeners()) {
            listenerUsernames[i] = GShell.convertCommandSenderToName(listener);
            i++;
        }
        lines[5] = convertChatColours(" Output listeners: &7" + String.join(", ", listenerUsernames));

        return lines;
    }

}
