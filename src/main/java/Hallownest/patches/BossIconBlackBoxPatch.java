package Hallownest.patches;

import Hallownest.monsters.GreenpathEnemies.BossBrokenVessel;
import Hallownest.monsters.GreenpathEnemies.BossFalseKnight;
import Hallownest.monsters.GreenpathEnemies.BossHornet;
import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.CustomBosses;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.DungeonMap;
import javassist.CtBehavior;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = CustomBosses.SetBossIcon.class,
        method = "Prefix"
)
// A patch to make basemod reload my boss icons so they stop showing up as black boxes
public class BossIconBlackBoxPatch {
    @SpireInsertPatch(locator = BossIconBlackBoxPatch.Locator.class)
    public static void StopTheBlackBox(AbstractDungeon _instance, String key) {
        if (key.equals(BossHornet.ID)) {
            DungeonMap.boss = ImageMaster.loadImage("HallownestResources/images/monsters/Greenpath/Hornet/BossIcon.png");
            DungeonMap.bossOutline = ImageMaster.loadImage("HallownestResources/images/monsters/Greenpath/Hornet/BossIconOutline.png");
        }else if (key.equals(BossBrokenVessel.ID)) {
            DungeonMap.boss = ImageMaster.loadImage("HallownestResources/images/monsters/Greenpath/BrokenVessel/BossIcon.png");
            DungeonMap.bossOutline = ImageMaster.loadImage("HallownestResources/images/monsters/Greenpath/BrokenVessel/BossIconOutline.png");
        }else if (key.equals(BossFalseKnight.ID)) {
            DungeonMap.boss = ImageMaster.loadImage("HallownestResources/images/monsters/Greenpath/falseknight/BossIcon.png");
            DungeonMap.bossOutline = ImageMaster.loadImage("HallownestResources/images/monsters/Greenpath/falseknight/BossIconOutline.png");
        }


    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Logger.class, "info");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}