package gg.gateway.gshell;

import gg.gateway.gshell.interfaces.GProcessInfo;
import gg.gateway.gshell.interfaces.GProcessManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Map;

public class GShellCommand implements CommandExecutor {

    private final GProcessManager processManager;

    public GShellCommand(GProcessManager processManager) {
        this.processManager = processManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!label.equalsIgnoreCase("gshell") && !label.equalsIgnoreCase("shell") && !label.equalsIgnoreCase("sh")) return false;

        if (args.length == 0) args = new String[] { "help" };

        // remove first index of array
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (args[0].toLowerCase()) {
            case "run":
                if (!sender.hasPermission(GShellPerms.RUN.toString())) {
                    sender.sendMessage(GShellMessages.NO_PERMISSION_MESSAGE);
                    break;
                }
                if (commandArgs.length == 0) {
                    sender.sendMessage(GShellMessages.COMMAND_MISSING_ARGS_MESSAGE(label, args[0], "<args...>"));
                    break;
                }
                long runId = processManager.startProcess(sender, true, commandArgs);
                if (runId > -1) {
                    processManager.getProcessInfo(runId).registerOutputListener(sender);
                    sender.sendMessage(GShellMessages.PROCESS_STARTED_MESSAGE(runId));
                }
                else sender.sendMessage(GShellMessages.PROGRAM_NOT_FOUND_MESSAGE(commandArgs[0]));

                break;
            case "quiet":
                if (!sender.hasPermission(GShellPerms.QUIET.toString())) {
                    sender.sendMessage(GShellMessages.NO_PERMISSION_MESSAGE);
                    break;
                }
                if (commandArgs.length == 0) {
                    sender.sendMessage(GShellMessages.COMMAND_MISSING_ARGS_MESSAGE(label, args[0], "<args...>"));
                    break;
                }
                long quietId = processManager.startProcess(sender, false, commandArgs);
                if (quietId > -1) sender.sendMessage(GShellMessages.PROCESS_STARTED_QUIET_MESSAGE(quietId));
                else sender.sendMessage(GShellMessages.PROGRAM_NOT_FOUND_MESSAGE(commandArgs[0]));

                break;
            case "list":
                if (!sender.hasPermission(GShellPerms.LIST.toString())) {
                    sender.sendMessage(GShellMessages.NO_PERMISSION_MESSAGE);
                    break;
                }
                Map<Long, GProcessInfo> processes = processManager.listRunningProcesses();
                sender.sendMessage(GShellMessages.RUNNING_PROCESSES_HEADER_MESSAGE(processes.size()));
                for (Map.Entry<Long, GProcessInfo> entry : processes.entrySet()) {
                    GProcessInfo info = entry.getValue();

                    sender.sendMessage(GShellMessages.RUNNING_PROCESSES_BODY_MESSAGE(entry.getKey(), GShell.convertCommandSenderToName(info.getExecutor()), info.getArgs()));
                }

                break;
            case "stop":
                if (!sender.hasPermission(GShellPerms.STOP.toString())) {
                    sender.sendMessage(GShellMessages.NO_PERMISSION_MESSAGE);
                    break;
                }
                if (commandArgs.length == 0) {
                    sender.sendMessage(GShellMessages.COMMAND_MISSING_ARGS_MESSAGE(label, args[0], "<id>"));
                    break;
                }
                long stopId;
                try {
                    stopId = Long.parseLong(commandArgs[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(GShellMessages.INVALID_ID_MESSAGE);
                    break;
                }
                if (!processManager.isIdRunning(stopId)) {
                    sender.sendMessage(GShellMessages.PROCESS_NOT_RUNNING_MESSAGE(stopId));
                    break;
                }
                sender.sendMessage(GShellMessages.PROCESS_STOP_ATTEMPT_MESSAGE(stopId));
                boolean stopped = processManager.stopProcess(stopId, false);
                if (stopped) sender.sendMessage(GShellMessages.PROCESS_STOP_SUCCESS_MESSAGE(stopId));
                else sender.sendMessage(GShellMessages.PROCESS_STOP_FAIL_MESSAGE(stopId));

                break;
            case "kill":
                if (!sender.hasPermission(GShellPerms.KILL.toString())) {
                    sender.sendMessage(GShellMessages.NO_PERMISSION_MESSAGE);
                    break;
                }
                if (commandArgs.length == 0) {
                    sender.sendMessage(GShellMessages.COMMAND_MISSING_ARGS_MESSAGE(label, args[0], "<id>"));
                    break;
                }
                long killId;
                try {
                    killId = Long.parseLong(commandArgs[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(GShellMessages.INVALID_ID_MESSAGE);
                    break;
                }
                if (!processManager.isIdRunning(killId)) {
                    sender.sendMessage(GShellMessages.PROCESS_NOT_RUNNING_MESSAGE(killId));
                    break;
                }
                sender.sendMessage(GShellMessages.PROCESS_STOP_ATTEMPT_MESSAGE(killId));
                boolean killed = processManager.stopProcess(killId, false);
                if (killed) sender.sendMessage(GShellMessages.PROCESS_STOP_SUCCESS_MESSAGE(killId));
                else sender.sendMessage(GShellMessages.PROCESS_STOP_FAIL_MESSAGE(killId));

                break;
            case "listen":
                if (!sender.hasPermission(GShellPerms.LISTEN.toString())) {
                    sender.sendMessage(GShellMessages.NO_PERMISSION_MESSAGE);
                    break;
                }
                if (commandArgs.length == 0) {
                    sender.sendMessage(GShellMessages.COMMAND_MISSING_ARGS_MESSAGE(label, args[0], "<id>"));
                    break;
                }
                long listenId;
                try {
                    listenId = Long.parseLong(commandArgs[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(GShellMessages.INVALID_ID_MESSAGE);
                    break;
                }
                if (!processManager.isIdRunning(listenId)) {
                    sender.sendMessage(GShellMessages.PROCESS_NOT_RUNNING_MESSAGE(listenId));
                    break;
                }
                GProcessInfo info = processManager.getProcessInfo(listenId);
                if (info.registerOutputListener(sender)) {
                    sender.sendMessage(GShellMessages.PROCESS_LISTEN_MESSAGE(listenId));
                } else {
                    info.unregisterOutputListener(sender);
                    sender.sendMessage(GShellMessages.PROCESS_UNLISTEN_MESSAGE(listenId));
                }

                break;
            case "info":
                if (!sender.hasPermission(GShellPerms.INFO.toString())) {
                    sender.sendMessage(GShellMessages.NO_PERMISSION_MESSAGE);
                    break;
                }

                if (commandArgs.length == 0) {
                    sender.sendMessage(GShellMessages.COMMAND_MISSING_ARGS_MESSAGE(label, args[0], "<id>"));
                    break;
                }
                long infoId;
                try {
                    infoId = Long.parseLong(commandArgs[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(GShellMessages.INVALID_ID_MESSAGE);
                    break;
                }
                if (!processManager.isIdRunning(infoId)) {
                    sender.sendMessage(GShellMessages.PROCESS_NOT_RUNNING_MESSAGE(infoId));
                    break;
                }

                sender.sendMessage(GShellMessages.PROCESS_INFO_MESSAGES(infoId, processManager.getProcessInfo(infoId)));

                break;
            default: // help menu
                for (String message : GShellMessages.HELP_MESSAGES(label, sender)) {
                    sender.sendMessage(message);
                }

                break;

        }

        return true;
    }


}
