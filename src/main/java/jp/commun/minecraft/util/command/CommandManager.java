/*
 * Copyright 2012 ayunyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.commun.minecraft.util.command;

import jp.commun.minecraft.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandManager
{
    private Map<Method, CommandHandler> instances;
    //private Map<Method, List<String>> commands;
    private List<CommandEntry> commands;
    private boolean handleHelp = false;

    public CommandManager()
    {
        instances = new HashMap<Method, CommandHandler>();
        commands = new LinkedList<CommandEntry>();
        //commands = new HashMap<Method, List<String>>();
    }

    public void register(CommandHandler handler)
    {
        Method[] methods = handler.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            if (!method.isAnnotationPresent(Command.class)) continue;
            Command property = method.getAnnotation(Command.class);

            if (property.names() == null || property.names().length == 0) continue;
            for (int j = 0; j < property.names().length; j++) {
                String name = property.names()[j].toLowerCase();
                if (name.substring(0, 1).equals("/")) name = name.substring(1);
                int len = name.split(" ").length;
                commands.add(new CommandEntry(name, property, method, len));
            }

            instances.put(method, handler);
        }

        sortCommands();
    }

    protected void sortCommands()
    {
        Collections.sort(commands, new Comparator<CommandEntry>() {
            @Override
            public int compare(CommandEntry o1, CommandEntry o2) {
                int ret = (o2.getArgsLength() - o1.getArgsLength());
                if (ret == 0) {
                    ret = o2.getName().length() - o1.getName().length();
                }
                return ret;
            }
        });
    }

    private void usage(CommandSender sender, String[] args)
    {
        String commandName = StringUtils.join(args, " ").toLowerCase();
        
        Map<CommandEntry, String> usages = new HashMap<CommandEntry, String>();
        for (CommandEntry entry : commands) {
            if (entry.getName().startsWith(commandName)) {
                Command command = entry.getCommand();

                String name;
                if (sender instanceof ConsoleCommandSender) {
                    if (!command.allowConsole()) continue;
                    name = entry.getName();
                } else {
                    if (!command.allowPlayer()) continue;
                    name = "/" + entry.getName();
                }

                usages.put(entry, ChatColor.AQUA + name + " " + ChatColor.GREEN + command.usage());
            }
        }
        
        if (usages.size() == 0 && args.length > 1) {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 0, newArgs, 0, args.length - 1);
            usage(sender, newArgs);
        } else if (usages.size() == 1) {
            help(sender, usages.keySet().iterator().next());
        } else {
            sender.sendMessage(ChatColor.AQUA + "==== Command Help ====");
            for (String usage: usages.values()) {
                sender.sendMessage(usage);
            }
        }
    }
    
    private void help(CommandSender sender, CommandEntry entry)
    {
        Command command = entry.getCommand();

        sender.sendMessage(ChatColor.AQUA + "==== Command Help ====");

        String name;
        if (sender instanceof ConsoleCommandSender) {
             name = entry.getName();
        } else {
             name = "/" + entry.getName();
        }

        if (command.desc().length() > 0) {
            sender.sendMessage(command.desc());
        }

        sender.sendMessage(ChatColor.AQUA + name + " " + ChatColor.GREEN + command.usage());
        if (command.names().length > 1) {
            List<String> aliases = new ArrayList<String>();
            for (String alias : command.names()) {
                if (alias.equals(entry.getName())) continue;
                if (!(sender instanceof ConsoleCommandSender)) alias = "/" + alias;
                aliases.add(alias);
            }
            sender.sendMessage("Aliases: " + StringUtils.join(aliases, ", "));
        }
        if (sender instanceof ConsoleCommandSender && command.permissions().length > 0) {
            sender.sendMessage("Permissions: " + StringUtils.join(command.permissions(), ", "));
        }
    }

    public void execute(CommandSender sender, String[] args)
    {
        String commandName = StringUtils.join(args, " ").toLowerCase();
        for (CommandEntry entry : commands) {
            if (commandName.startsWith(entry.getName())) {
                Command command = entry.getCommand();

                if (sender instanceof ConsoleCommandSender) {
                    if (!command.allowConsole()) continue;
                } else {
                    if (!command.allowPlayer()) continue;
                    String[] permissions = command.permissions();
                    if (permissions != null && permissions.length > 0) {
                        for (String permission : permissions) {
                            if (!sender.hasPermission(permission)) throw new CommandPermissionException();
                        }
                    }
                }

                int argsLength = args.length - entry.getArgsLength();
                if (command.min() > argsLength) {
                    if (command.usage().length() > 0) {
                        help(sender, entry);
                        return;
                    } else {
                        throw new CommandException("too few arguments");
                    }
                } else if (command.max() != -1 && command.max() < argsLength) {
                    if (command.usage().length() > 0) {
                        help(sender, entry);
                        return;
                    } else {
                        throw new CommandException("too many arguments");
                    }
                }

                String[] newArgs = new String[argsLength];
                System.arraycopy(args, entry.getArgsLength(), newArgs, 0, argsLength);


                Method method = entry.getMethod();
                try {
                    method.invoke(instances.get(method), sender, entry.getName(), newArgs);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                return;
            }
        }

        usage(sender, args);
    }

    public void execute(CommandSender sender, org.bukkit.command.Command command, String[] args)
    {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = command.getName();
        System.arraycopy(args, 0, newArgs, 1, args.length);
        execute(sender, newArgs);
    }
}