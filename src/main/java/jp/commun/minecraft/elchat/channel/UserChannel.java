package jp.commun.minecraft.elchat.channel;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/12
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class UserChannel extends GameChannel
{
    protected String owner;
    protected List<String> moderators;
    
    public UserChannel(String name) {
        super(name);
    }
    
    
}
