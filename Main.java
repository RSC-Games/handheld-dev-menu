// Handheld System Menu for the RSC Games Pi-based handheld 
// Copyright (C) 2025-2026  sRGB
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

import backend.NetworkBackend;
import backend.TitleLaunchService;
import menu.crash_handler.CrashPanel;
import menu.main.MainMenu;
import system.InputManager;
import system.MainWindow;
import system.PanelManager;
import ui.UIPanel;
import util.Log;
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

        // Simple render loop. Everything is managed in here.
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
            Log.logFatal("unhandled exception caught in main event loop");
            Log.logException(ie);

            menuExceptionHandler(window, ie);
            // TODO: close logfile and clean up logger.
            window.cleanUp();
            System.exit(-1);
        }

        System.exit(0);
    }

    private static void init() {
        // TODO: Do init stuff in here
        // TODO: Volume settings are not automatically restored yet.
        // TODO: Disable x11 screen blanking (xset s noblank)
        // (Backlight is automatically restored by systemd)

        // Run dhcp if applicable
        NetworkBackend.runWaitForNetwork();
    }

    /**
     * Show the exception details on screen to potentially make debugging easier.
     * 
     * @param window The active window for rendering (not any overlays)
     * @param ie The exception to print.
     */
    private static void menuExceptionHandler(MainWindow window, Exception ie) {
        System.out.print("\033[31m");
        System.out.print("Exception in thread " + Thread.currentThread().getName() + " ");
        ie.printStackTrace();
        System.out.print("\033[0m");

        // Format the stack trace for displaying on screen.
        StackTraceElement[] elements = ie.getStackTrace();
        String[] exceptionLines = new String[elements.length + 1];
        exceptionLines[0] = "Exception in thread " + Thread.currentThread().getName() 
                            + " " + ie.getClass().getName() + " " + ie.getMessage();

        for (int i = 0; i < elements.length; i++)
            exceptionLines[i + 1] = "      at " + elements[i].toString();

        // Show the exception on screen.
        PanelManager mgr = PanelManager.getPanelManager();
        mgr.reset();
        mgr.pushPanel(new CrashPanel(exceptionLines));

        while (mgr.hasPanels()) {
            window.tickWindow();
            InputManager.getInputManager().poll();
            Utils.sleepms(16);
            mgr.updateTopPanel();
        }
    }
}