/*
 * Copyright (C) 2016 The MoonLake Authors
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
 
 
package com.minecraft.moonlake.nms.anvil;

import com.minecraft.moonlake.api.event.MoonLakeListener;
import com.minecraft.moonlake.event.EventHelper;
import com.minecraft.moonlake.nms.exception.NMSException;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * <h1>AnvilWindow</h1>
 * 铁砧窗口（详细doc待补充...）
 *
 * @version 1.0
 * @author Month_Light
 */
public final class AnvilWindow {

    private final Plugin plugin;
    private MoonLakeListener listenerClick;
    private MoonLakeListener listenerClose;
    private MoonLakeListener listenerMove;
    private boolean allowMove;
    private boolean isInitialized;
    private AnvilWindowEventHandler<AnvilWindowClickEvent> clickEventHandle;
    private AnvilWindowEventHandler<AnvilWindowCloseEvent> closeEventHandle;
    protected Inventory anvilInventory;
    protected Object anvilInventoryHandle;

    /**
     * 铁砧窗口类构造函数
     *
     * @param plugin JavaPlugin
     */
    public AnvilWindow(Plugin plugin) {

        this.plugin = plugin;
    }

    public Plugin getPlugin() {

        return plugin;
    }

    private void initialize() {

        if(!isInitialized) {

            isInitialized = true;
        }
    }

    public boolean isAllowMove() {

        return allowMove;
    }

    public void setAllowMove(boolean allowMove) {

        this.allowMove = allowMove;
        this.setAllowMove0(allowMove);
    }

    private void setAllowMove0(boolean flag) {

        if(flag && listenerMove == null) {

            listenerMove = new MoonLakeListener() {

                @EventHandler
                public void onAnvilMove(InventoryClickEvent event) {

                    if(event.getClickedInventory() == null) return;
                    if(event.getClickedInventory().getType() != InventoryType.PLAYER) return;
                    if(!(event.getWhoClicked() instanceof Player)) return;

                    Player player = (Player) event.getWhoClicked();

                    if(player.getOpenInventory() == null) return;
                    if(player.getOpenInventory().getTopInventory() == null) return;
                    if(!player.getOpenInventory().getTopInventory().equals(anvilInventory)) return;

                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);

                    player.updateInventory();
                }
            };
            registerListener(listenerMove);
        }
        else if(!flag && listenerMove != null) {

            disposeMove();
        }
    }

    public void onClick(AnvilWindowEventHandler<AnvilWindowClickEvent> clickEvent) {

        if(clickEvent == null) {

            disposeClick();
        }
        else {

            clickEventHandle = clickEvent;
            listenerClick = new MoonLakeListener() {

                @EventHandler
                public void onAnvilClick(InventoryClickEvent event) {

                    if(event.getClickedInventory() == null) return;
                    if(!event.getClickedInventory().equals(anvilInventory)) return;
                    if(!(event.getWhoClicked() instanceof Player)) return;

                    Player player = (Player) event.getWhoClicked();
                    ItemStack clickItemStack = event.getCurrentItem();
                    AnvilWindowSlot clickSlot = AnvilWindowSlot.fromRawSlot(event.getRawSlot());

                    if(clickSlot == null) return;
                    AnvilWindowClickEvent awce = new AnvilWindowClickEvent(player, AnvilWindow.this, clickSlot, clickItemStack);
                    clickEventHandle.onExecute(awce);
                }
            };
            registerListener(listenerClick);
        }
    }

    public void onClose(AnvilWindowEventHandler<AnvilWindowCloseEvent> closeEvent) {

        if(closeEvent == null) {

            disposeClose();
        }
        else {

            closeEventHandle = closeEvent;
            listenerClose = new MoonLakeListener() {

                @EventHandler
                public void onAnvilClose(InventoryCloseEvent event) {

                    if(event.getInventory() == null) return;
                    if(!event.getInventory().equals(anvilInventory)) return;
                    if(!(event.getPlayer() instanceof Player)) return;

                    Player player = (Player) event.getPlayer();

                    AnvilWindowCloseEvent awce = new AnvilWindowCloseEvent(player, AnvilWindow.this);
                    closeEventHandle.onExecute(awce);
                }
            };
            registerListener(listenerClose);
        }
    }

    public void openAnvil(Player player) throws NMSException {

        AnvilWindowReflect.get().openAnvil(player, this);
        initialize();
    }

    public void setItem(AnvilWindowSlot slot, ItemStack itemStack) {

        if(!isInitialized) {

        }
        anvilInventory.setItem(slot.getSlot(), itemStack);
    }

    public ItemStack getItem(AnvilWindowSlot slot) {

        if(!isInitialized) {

        }
        return anvilInventory.getItem(slot.getSlot());
    }

    private void registerListener(MoonLakeListener listener) {

        if(listener != null) {

            EventHelper.registerEvent(listener, plugin);
        }
    }

    public void dispose() {

        disposeMove();
        disposeClick();
        disposeClose();
    }

    private void disposeMove() {

        disposeListener(listenerMove);
        listenerMove = null;
    }

    private void disposeClick() {

        disposeListener(listenerClick);
        listenerClick = null;
        clickEventHandle = null;
    }

    private void disposeClose() {

        disposeListener(listenerClose);
        listenerClose = null;
        closeEventHandle = null;
    }

    private void disposeListener(MoonLakeListener listener) {

        if(listener != null) {
            EventHelper.unregisterAll(listener);
        }
    }
}
