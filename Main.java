// Handheld System Menu for the RSC Games Pi-based handheld 
// Copyright (C) 2025  sRGB
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License 2.0 as published by
// the Free Software Foundation; no other version of the license is permitted.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
// Unless otherwise specified, all source code within this repository is
// covered by the above license terms.

import backend.TitleLaunchService;
import menu.main.MainMenu;
import system.InputManager;
import system.MainWindow;
import system.PanelManager;
import ui.UIPanel;
import util.Utils;

public class Main {
    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        InputManager inputManager = window.getInputManager();
        PanelManager panelManager = PanelManager.getPanelManager();

        // Initial hardware housekeeping stuff like network/volume/power management.
        init();

        // This uses the panel menu system like the System UI
        UIPanel mainMenu = new MainMenu();
        panelManager.pushPanel(mainMenu);

        // Simple render loop. Everythign is managed in here.
        try {
            window.display();

            while (true) {
                // Wait for an interrupt if a game is actively executing.
                TitleLaunchService.poll();

                inputManager.poll();
                Utils.sleepms(16);

                // UI is simulated when the window is active and on screen.
                if (window.isActive()) {
                    panelManager.updateTopPanel();
                }

                // Otherwise we run the background logic (like re-showing the window)
                else {

                }

                window.tickWindow();
            }
        }
        catch (Exception ie) {
            window.cleanUp();
            System.out.print("Exception in thread " + Thread.currentThread().getName() + " ");
            ie.printStackTrace();
            System.exit(-1);
        }

        System.exit(0);
    }

    private static void init() {
        // TODO: Do init stuff in here
        // TODO: Backlight and volume settings are not automatically restored yet.
    }
}