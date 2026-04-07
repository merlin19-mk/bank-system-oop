package com.fortis.bank;

import com.fortis.bank.business.service.BankService;
import com.fortis.bank.data.persistence.FileDataStore;
import com.fortis.bank.presentation.ConsoleMenu;
import java.nio.file.Path;

/**
 * Entry point for the Fortis Bank console application.
 *
 * @author Franck Merlin
 * @version v0.3.0
 */
public final class FortisBank {

    private FortisBank() {
        // Utility class; no instances.
    }

    public static void main(String[] args) {
        FileDataStore dataStore = new FileDataStore(Path.of("data"));
        BankService bankService = new BankService(dataStore);
        ConsoleMenu consoleMenu = new ConsoleMenu(bankService);
        consoleMenu.run();
    }
}
