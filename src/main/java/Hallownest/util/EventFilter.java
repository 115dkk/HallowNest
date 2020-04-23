package Hallownest.util;

import Hallownest.HallownestMod;


import java.util.ArrayList;

public class EventFilter {

    //Not supposed to be instantiated
    private EventFilter() {
        throw new AssertionError();
    }

    public static ArrayList<String> FilterEvents(ArrayList<String> events) {
        ArrayList<String> eventsToRemove = new ArrayList<>();
        /*
        for (String event : events) {
            if (event.equals(DemonBookSeller.ID)) {
                if (!(AbstractDungeon.player.gold >= DemonBookSeller.COST)) {
                    eventsToRemove.add(event);
                }
            } else if (event.equals(GoodsFromTheOutsideWorld.ID)) {
                if (!(AbstractDungeon.player.gold >= GoodsFromTheOutsideWorld.commonCost)) {
                    eventsToRemove.add(event);
                }
            } else if (event.equals(ClashOfLegends.ID)) {
                if (!(AbstractDungeon.player.maxHealth > 1)) {
                    eventsToRemove.add(event);
                }
            } else if (event.equals(GardenOfTheSun.ID)) {
                if (AbstractDungeon.player.hasRelic(HappyFlower.ID) || !GardenOfTheSun.hasNonStrikeAttack()) {
                    eventsToRemove.add(event);
                }
            } else if (event.equals(FieldTripToAnotherWorld.ID)) {
                if (!FieldTripToAnotherWorld.hasNonBasicCard()) {
                    eventsToRemove.add(event);
                }
            } else if (event.equals(JustAVisit.ID)) {
                if (!(AbstractDungeon.player.gold >= JustAVisit.COST)) {
                    eventsToRemove.add(event);
                }
            } else if (event.equals(AHazardousHobby.ID)) {
                if (!(AHazardousHobby.hasRandomNonBasicCard())) {
                    eventsToRemove.add(event);
                }
            } else if (event.equals(BookThief.ID)) {
                if (!(AbstractDungeon.floorNum >= 4)) {
                    eventsToRemove.add(event);
                }
            }
            if (MarisaSpecificFilter(event)) {
                eventsToRemove.add(event);
            }
        }*/
        return eventsToRemove;
    }

    private static boolean MarisaSpecificFilter(String event) {
        //Filter out these events if the player is not Marisa
        /*
        if (HallownestMod.hasMarisa && !(AbstractDungeon.player instanceof Marisa)) {
            if (event.equals(AnOldGhost.ID)) {
                return true;
            } else if (event.equals(JustAVisit.ID)) {
                return true;
            } else if (event.equals(BookThief.ID)) {
                return true;
            } else if (event.equals(AHazardousHobby.ID)) {
                return true;
            } else if (event.equals(Walpurgisnacht.ID)) {
                return true;
            }
            //Filter out these events if the player is Marisa
        } else if (HallownestMod.hasMarisa && AbstractDungeon.player instanceof Marisa) {
            if (event.equals(ScarletDevilMansion.ID)) {
                return true;
            } else if (event.equals(ForestOfMagic.ID)) {
                return true;
            } else if (event.equals(GoodsFromTheOutsideWorld.ID)) {
                return true;
            }
        }*/
        return false;
    }
}
