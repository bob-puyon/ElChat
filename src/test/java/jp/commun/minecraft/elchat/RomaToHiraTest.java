package jp.commun.minecraft.elchat;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RomaToHiraTest {
    @Test
    public void hasHiraganaTest()
    {
        assertTrue(RomaToHira.hasHiragana("にゃんにゃん"));
        assertTrue(RomaToHira.hasHiragana("defモフモフabc"));
        assertFalse(RomaToHira.hasHiragana("ab|-32.<!?mofu"));
    }
}
