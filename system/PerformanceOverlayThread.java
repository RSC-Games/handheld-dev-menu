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

package system;

import util.Log;
import util.Utils;

public class PerformanceOverlayThread implements Runnable {
    /**
     * Performance overlay thread is a singleton (no reason for more than one instance
     * to ever exist).
     */
    private static final PerformanceOverlayThread theThread;

    /**
     * Over the lifetime of the overlay many different threads can be spawned and deleted.
     */
    Thread innerThread;

    /**
     * Facilitate easy killing of the inner thread.
     */
    volatile boolean running;

    static {
        theThread = new PerformanceOverlayThread();
    }

    // Only allow this to create the overlay thread.
    PerformanceOverlayThread() {}

    public static PerformanceOverlayThread getOverlayThread() {
        return theThread;
    }
    
    public void run() {
        Log.logInfo("perfmon: started performance overlay thread");
        PerfOverlayWindow perfOverlay = new PerfOverlayWindow();

        // Simple render loop. Everything is managed in here.
        try {
            perfOverlay.show();

            while (this.running) {
                Utils.sleepms(16);
                perfOverlay.tick();
                perfOverlay.update();
            }

            Log.logInfo("perfmon: thread exit requested");
        }
        catch (Exception ie) {
            Log.logFatal("unhandled exception caught in perf overlay loop");
            Log.logException(ie);
        }

        perfOverlay.destroy();
    }

    public boolean getIsRunning() {
        return this.running;
    }

    public void start() {
        if (!this.running) {
            innerThread = new Thread(this, "perfmon_window");
            innerThread.setDaemon(true);
            this.running = true;
            innerThread.start();
        }
    }

    public void stop() {
        this.running = false;
        this.innerThread = null;
    }
}