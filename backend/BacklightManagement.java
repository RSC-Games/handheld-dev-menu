package backend;

import backend.system_mgmt.SMCallbackFunction;

public class BacklightManagement implements SMCallbackFunction {
    private static final BacklightManagement theService;

    static {
        theService = new BacklightManagement();
    }

    BacklightManagement() {

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
    
}
