/*
 * Copyright (C) 2016-Present The MoonLake (mcmoonlake@hotmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmoonlake.api.item

/**
 * * Enumeration containing 1.12.2 and the new version.
 * * That is to say, it is possible to rely on [Material] to make compatible v1.8 ~ v1.13+ versions.
 *
 * @author lgou2w
 * @since 2.0
 */
abstract class Material {

    abstract fun to(): org.bukkit.Material

    private class Materials(
            private val id: Int?,
            private val value: String?
    ) : Material() {

        constructor(id: Int?) : this(id, null)
        constructor(value: String) : this(null, value)

        override fun to(): org.bukkit.Material
                = org.bukkit.Material.AIR
    }

    private class MaterialBlocks(
            private val id: Int?,
            private val value: String?,
            private val valueBlock: String?
    ) : MaterialBlock() {

        override fun to(): org.bukkit.Material
                = org.bukkit.Material.AIR

        override fun toBlock(): org.bukkit.Material
                = org.bukkit.Material.AIR
    }

    companion object {

        // 1.8 ~ 1.12.2 START

        /** A */

        @JvmField val AIR = Materials(0) as Material
        @JvmField val ACACIA_DOOR = Materials(196) as Material
        @JvmField val ACACIA_DOOR_ITEM = Materials(430, "ACACIA_DOOR") as Material
        @JvmField val ACACIA_FENCE = Materials(192) as Material
        @JvmField val ACACIA_FENCE_GATE = Materials(187) as Material
        @JvmField val ACACIA_STAIRS = Materials(163) as Material
        @JvmField val ACTIVATOR_RAIL = Materials(157) as Material
        @JvmField val ANVIL = Materials(145) as Material
        @JvmField val APPLE = Materials(260) as Material
        @JvmField val ARMOR_STAND = Materials(416) as Material
        @JvmField val ARROW = Materials(262) as Material

        /** B */

        BAKED_POTATO(393),
        BANNER(425, "BLACK_BANNER"),
        BARRIER(166),
        BEACON(138),
        @JvmField val BED = Materials(355, "WHITE_BED") as Material
        BEDROCK(7),
        @JvmField val BED_BLOCK = Materials(26, true) as Material
        BEETROOT(434),
        BEETROOT_BLOCK(207, true),
        BEETROOT_SEEDS(435),
        BEETROOT_SOUP(436),
        BIRCH_DOOR(194),
        BIRCH_DOOR_ITEM(428, "BIRCH_DOOR"),
        BIRCH_FENCE(189),
        BIRCH_FENCE_GATE(184),
        BIRCH_WOOD_STAIRS(135, "BIRCH_STAIRS"),
        BLACK_GLAZED_TERRACOTTA(250),
        BLACK_SHULKER_BOX(234),
        BLAZE_POWDER(377),
        BLAZE_ROD(369),
        BLUE_GLAZED_TERRACOTTA(246),
        BLUE_SHULKER_BOX(230),
        BOAT(333, "OAK_BOAT"),
        BOAT_ACACIA(447, "ACACIA_BOAT"),
        BOAT_BIRCH(445, "BIRCH_BOAT"),
        BOAT_DARK_OAK(448, "DARK_OAK_BOAT"),
        BOAT_JUNGLE(446, "JUNGLE_BOAT"),
        BOAT_SPRUCE(444, "SPRUCE_BOAT"),
        BONE(352),
        BONE_BLOCK(216),
        BOOK(340),
        BOOKSHELF(47),
        BOOK_AND_QUILL(386, "WRITABLE_BOOK"),
        BOW(261),
        BOWL(281),
        BREAD(297),
        BREWING_STAND(117),
        BREWING_STAND_ITEM(379, "BREWING_STAND"),
        BRICK(45, "BRICKS"),
        BRICK_STAIRS(108),
        BROWN_GLAZED_TERRACOTTA(247),
        BROWN_MUSHROOM(39),
        BROWN_SHULKER_BOX(231),
        BUCKET(325),
        BURNING_FURNACE(62, "FURNACE"),

        /** C */

        CACTUS(81),
        CAKE(354),
        CAKE_BLOCK(92, true),
        CARPET(171, "WHITE_CARPET"),
        CARROT(141),
        CARROT_ITEM(391, "CARROT"),
        CARROT_STICK(398, "CARROT_ON_A_STICK"),
        CAULDRON(118),
        CAULDRON_ITEM(380, "CAULDRON"),
        CHAINMAIL_BOOTS(305),
        CHAINMAIL_CHESTPLATE(303),
        CHAINMAIL_HELMET(302),
        CHAINMAIL_LEGGINGS(304),
        CHEST(54),
        CHORUS_FLOWER(200),
        CHORUS_FRUIT(432),
        CHORUS_FRUIT_POPPED(433),
        CHORUS_PLANT(199),
        CLAY(82),
        CLAY_BALL(337),
        CLAY_BRICK(336, "BRICK"),
        COAL,
        COAL_BLOCK,
        COAL_ORE,
        COBBLESTONE,
        COBBLESTONE_STAIRS,
        COBBLE_WALL,
        COCOA,
        COMMAND,
        COMMAND_CHAIN,
        COMMAND_MINECART,
        COMMAND_REPEATING,
        COMPASS,
        CONCRETE,
        CONCRETE_POWDER,
        COOKED_BEEF,
        COOKED_CHICKEN,
        COOKED_FISH,
        COOKED_MUTTON,
        COOKED_RABBIT,
        COOKIE,
        CROPS,
        CYAN_GLAZED_TERRACOTTA,
        CYAN_SHULKER_BOX,

        /** D */

        DARK_OAK_DOOR,
        DARK_OAK_DOOR_ITEM,
        DARK_OAK_FENCE,
        DARK_OAK_FENCE_GATE,
        DARK_OAK_STAIRS,
        DAYLIGHT_DETECTOR,
        DAYLIGHT_DETECTOR_INVERTED,
        DEAD_BUSH,
        DETECTOR_RAIL,
        DIAMOND,
        DIAMOND_AXE,
        DIAMOND_BARDING,
        DIAMOND_BLOCK,
        DIAMOND_BOOTS,
        DIAMOND_CHESTPLATE,
        DIAMOND_HELMET,
        DIAMOND_HOE,
        DIAMOND_LEGGINGS,
        DIAMOND_ORE,
        DIAMOND_PICKAXE,
        DIAMOND_SPADE,
        DIAMOND_SWORD,
        DIODE,
        DIODE_BLOCK_OFF,
        DIODE_BLOCK_ON,
        DIRT,
        DISPENSER,
        DOUBLE_PLANT,
        DOUBLE_STEP,
        DOUBLE_STONE_SLAB2,
        DRAGONS_BREATH,
        DRAGON_EGG,
        DROPPER,

        /** E */

        EGG,
        ELYTRA,
        EMERALD,
        EMERALD_BLOCK,
        EMERALD_ORE,
        EMPTY_MAP,
        ENCHANTED_BOOK,
        ENCHANTMENT_TABLE,
        ENDER_CHEST,
        ENDER_PEARL,
        ENDER_PORTAL,
        ENDER_PORTAL_FRAME,
        ENDER_STONE,
        END_BRICKS,
        END_CRYSTAL,
        END_GATEWAY,
        END_ROD,
        EXPLOSIVE_MINECART,
        EXP_BOTTLE,
        EYE_OF_ENDER,

        /** F */

        FEATHER,
        FENCE,
        FENCE_GATE,
        FERMENTED_SPIDER_EYE,
        FIRE,
        FIREBALL,
        FIREWORK,
        FIREWORK_CHARGE,
        FISHING_ROD,
        FLINT,
        FLINT_AND_STEEL,
        FLOWER_POT,
        FLOWER_POT_ITEM,
        FROSTED_ICE,
        FURNACE,

        /** G */

        GHAST_TEAR,
        GLASS,
        GLASS_BOTTLE,
        GLOWING_REDSTONE_ORE,
        GLOWSTONE,
        GLOWSTONE_DUST,
        GOLDEN_APPLE,
        GOLDEN_CARROT,
        GOLD_AXE,
        GOLD_BARDING,
        GOLD_BLOCK,
        GOLD_BOOTS,
        GOLD_CHESTPLATE,
        GOLD_HELMET,
        GOLD_HOE,
        GOLD_INGOT,
        GOLD_LEGGINGS,
        GOLD_NUGGET,
        GOLD_ORE,
        GOLD_PICKAXE,
        GOLD_PLATE,
        GOLD_RECORD,
        GOLD_SPADE,
        GOLD_SWORD,
        GRASS,
        GRASS_PATH,
        GRAVEL,
        GRAY_GLAZED_TERRACOTTA,
        GRAY_SHULKER_BOX,
        GREEN_GLAZED_TERRACOTTA,
        GREEN_RECORD,
        GREEN_SHULKER_BOX,
        GRILLED_PORK,

        /** H */

        HARD_CLAY,
        HAY_BLOCK,
        HOPPER,
        HOPPER_MINECART,
        HUGE_MUSHROOM_1,
        HUGE_MUSHROOM_2,

        /** I */

        ICE,
        INK_SACK,
        IRON_AXE,
        IRON_BARDING,
        IRON_BLOCK,
        IRON_BOOTS,
        IRON_CHESTPLATE,
        IRON_DOOR,
        IRON_DOOR_BLOCK,
        IRON_FENCE,
        IRON_HELMET,
        IRON_HOE,
        IRON_INGOT,
        IRON_LEGGINGS,
        IRON_NUGGET,
        IRON_ORE,
        IRON_PICKAXE,
        IRON_PLATE,
        IRON_SPADE,
        IRON_SWORD,
        IRON_TRAPDOOR,
        ITEM_FRAME,

        /** J */

        JACK_O_LANTERN,
        JUKEBOX,
        JUNGLE_DOOR,
        JUNGLE_DOOR_ITEM,
        JUNGLE_FENCE,
        JUNGLE_FENCE_GATE,
        JUNGLE_WOOD_STAIRS,

        /** K */

        KNOWLEDGE_BOOK,

        /** L */

        LADDER,
        LAPIS_BLOCK,
        LAPIS_ORE,
        LAVA,
        LAVA_BUCKET,
        LEASH,
        LEATHER,
        LEATHER_BOOTS,
        LEATHER_CHESTPLATE,
        LEATHER_HELMET,
        LEATHER_LEGGINGS,
        LEAVES,
        LEAVES_2,
        LEVER,
        LIGHT_BLUE_GLAZED_TERRACOTTA,
        LIGHT_BLUE_SHULKER_BOX,
        LIME_GLAZED_TERRACOTTA,
        LIME_SHULKER_BOX,
        LINGERING_POTION,
        LOG,
        LOG_2,
        LONG_GRASS,

        /** M */

        MAGENTA_GLAZED_TERRACOTTA,
        MAGENTA_SHULKER_BOX,
        MAGMA,
        MAGMA_CREAM,
        MAP,
        MELON,
        MELON_BLOCK,
        MELON_SEEDS,
        MELON_STEM,
        MILK_BUCKET,
        MINECART,
        MOB_SPAWNER,
        MONSTER_EGG,
        MONSTER_EGGS,
        MOSSY_COBBLESTONE,
        MUSHROOM_SOUP,
        MUTTON,
        MYCEL,

        /** N */

        NAME_TAG,
        NETHERRACK,
        NETHER_BRICK,
        NETHER_BRICK_ITEM,
        NETHER_BRICK_STAIRS,
        NETHER_FENCE,
        NETHER_STALK,
        NETHER_STAR,
        NETHER_WARTS,
        NETHER_WART_BLOCK,
        NOTE_BLOCK,

        /** O */

        OBSERVER,
        OBSIDIAN,
        ORANGE_GLAZED_TERRACOTTA,
        ORANGE_SHULKER_BOX,

        /** P */

        PACKED_ICE,
        PAINTING,
        PAPER,
        PINK_GLAZED_TERRACOTTA,
        PINK_SHULKER_BOX,
        PISTON_BASE,
        PISTON_EXTENSION,
        PISTON_MOVING_PIECE,
        PISTON_STICKY_BASE,
        POISONOUS_POTATO,
        PORK,
        PORTAL,
        POTATO,
        POTATO_ITEM,
        POTION,
        POWERED_MINECART,
        POWERED_RAIL,
        PRISMARINE,
        PRISMARINE_CRYSTALS,
        PRISMARINE_SHARD,
        PUMPKIN,
        PUMPKIN_PIE,
        PUMPKIN_SEEDS,
        PUMPKIN_STEM,
        PURPLE_GLAZED_TERRACOTTA,
        PURPLE_SHULKER_BOX,
        PURPUR_BLOCK,
        PURPUR_DOUBLE_SLAB,
        PURPUR_PILLAR,
        PURPUR_SLAB,
        PURPUR_STAIRS,

        /** Q */

        QUARTZ,
        QUARTZ_BLOCK,
        QUARTZ_ORE,
        QUARTZ_STAIRS,

        /** R */

        RABBIT,
        RABBIT_FOOT,
        RABBIT_HIDE,
        RABBIT_STEW,
        RAILS,
        RAW_BEEF,
        RAW_CHICKEN,
        RAW_FISH,
        RECORD_10,
        RECORD_11,
        RECORD_12,
        RECORD_3,
        RECORD_4,
        RECORD_5,
        RECORD_6,
        RECORD_7,
        RECORD_8,
        RECORD_9,
        REDSTONE,
        REDSTONE_BLOCK,
        REDSTONE_COMPARATOR,
        REDSTONE_COMPARATOR_OFF,
        REDSTONE_COMPARATOR_ON,
        REDSTONE_LAMP_OFF,
        REDSTONE_LAMP_ON,
        REDSTONE_ORE,
        REDSTONE_TORCH_OFF,
        REDSTONE_TORCH_ON,
        REDSTONE_WIRE,
        RED_GLAZED_TERRACOTTA,
        RED_MUSHROOM,
        RED_NETHER_BRICK,
        RED_ROSE,
        RED_SANDSTONE,
        RED_SANDSTONE_STAIRS,
        RED_SHULKER_BOX,
        ROTTEN_FLESH,

        /** S */

        SADDLE,
        SAND,
        SANDSTONE,
        SANDSTONE_STAIRS,
        SAPLING,
        SEA_LANTERN,
        SEEDS,
        SHEARS,
        SHIELD,
        SHULKER_SHELL,
        SIGN,
        SIGN_POST,
        SILVER_GLAZED_TERRACOTTA,
        SILVER_SHULKER_BOX,
        SKULL,
        SKULL_ITEM,
        SLIME_BALL,
        SLIME_BLOCK,
        SMOOTH_BRICK,
        SMOOTH_STAIRS,
        SNOW,
        SNOW_BALL,
        SNOW_BLOCK,
        SOIL,
        SOUL_SAND,
        SPECKLED_MELON,
        SPECTRAL_ARROW,
        SPIDER_EYE,
        SPLASH_POTION,
        SPONGE,
        SPRUCE_DOOR,
        SPRUCE_DOOR_ITEM,
        SPRUCE_FENCE,
        SPRUCE_FENCE_GATE,
        SPRUCE_WOOD_STAIRS,
        STAINED_CLAY,
        STAINED_GLASS,
        STAINED_GLASS_PANE,
        STANDING_BANNER,
        STATIONARY_LAVA,
        STATIONARY_WATER,
        STEP,
        STICK,
        STONE,
        STONE_AXE,
        STONE_BUTTON,
        STONE_HOE,
        STONE_PICKAXE,
        STONE_PLATE,
        STONE_SLAB2,
        STONE_SPADE,
        STONE_SWORD,
        STORAGE_MINECART,
        STRING,
        STRUCTURE_BLOCK,
        STRUCTURE_VOID,
        SUGAR,
        SUGAR_CANE,
        SUGAR_CANE_BLOCK,
        SULPHUR,

        /** T */

        THIN_GLASS,
        TIPPED_ARROW,
        TNT,
        TORCH,
        TOTEM,
        TRAPPED_CHEST,
        TRAP_DOOR,
        TRIPWIRE,
        TRIPWIRE_HOOK,

        /** V */

        VINE,

        /** W */

        WALL_BANNER,
        WALL_SIGN,
        WATCH,
        WATER,
        WATER_BUCKET,
        WATER_LILY,
        WEB,
        WHEAT,
        WHITE_GLAZED_TERRACOTTA,
        WHITE_SHULKER_BOX,
        WOOD,
        WOODEN_DOOR,
        WOOD_AXE,
        WOOD_BUTTON,
        WOOD_DOOR,
        WOOD_DOUBLE_STEP,
        WOOD_HOE,
        WOOD_PICKAXE,
        WOOD_PLATE,
        WOOD_SPADE,
        WOOD_STAIRS,
        WOOD_STEP,
        WOOD_SWORD,
        WOOL,
        WORKBENCH,
        WRITTEN_BOOK,

        /** Y */

        YELLOW_FLOWER,
        YELLOW_GLAZED_TERRACOTTA,
        YELLOW_SHULKER_BOX,

        // 1.8 ~ 1.12.2 END

        // 1.13 ~ ? START

        // 相当于在 1.8 ~ 1.12.2 版本, 那么调用 toBukkit 会转换为 Material.BED
        // 调用 toBlock 会转换为 Material.BED_BLOCK
        ///
        // 而在 1.13 ~ ? 版本, 那么调用 toBukkit 还是 toBlock 都会转换为 Material.WHITE_BED
        ///
        @JvmField val WHITE_BED = MaterialBlocks(26, "BED", "BED_BLOCK") as MaterialBlock

        // 1.13 ~ ? END

    }
}

abstract class MaterialBlock : Material() {

    abstract fun toBlock(): org.bukkit.Material
}
