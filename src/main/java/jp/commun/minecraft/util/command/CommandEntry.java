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

import java.lang.reflect.Method;

public class CommandEntry {
    private final String name;
    private final Command command;
    private final Method method;
    private final int argsLength;
    
    public CommandEntry(String name, Command command, Method method, int argsLen)
    {
        this.command = command;
        this.name = name;
        this.method = method;
        this.argsLength = argsLen;
    }
    
    public String getName() {
        return name;
    }
    
    public Command getCommand() {
        return command;
    }

    public int getArgsLength() {
        return argsLength;
    }

    public Method getMethod() {
        return method;
    }
}
