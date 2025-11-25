package menu.settings.network.network_list;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuOptionList;

class GenerateNetworksList {
    public GenerateNetworksList(MenuOptionList menu) {
        SavedNetwork[] networks = NetworkBackend.listNetworks();

        for (SavedNetwork network : networks) {
            if (network == null)
                continue;

            new NetworkMenuEntry(menu, network);
        }
    }
}
