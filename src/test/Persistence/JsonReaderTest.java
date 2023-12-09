package Persistence;

import model.Account;
import model.Asset;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonReaderTest extends JsonTest {
    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Account ac = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyAccount() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyAccount.json");
        try {
            Account ac = reader.read();
            assertEquals("My account", ac.getName());
            assertEquals(0, ac.numAssets());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralAccount() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralAccount.json");
        try {
            Account ac = reader.read();
            assertEquals("My account", ac.getName());
            List<Asset> assets = ac.getAccount();
            List<Asset> assetsRemoved = ac.getAssetsRemoved();
            assertEquals(2, assets.size());
            assertEquals(1, assetsRemoved.size());
            LocalDate expDate = LocalDate.of(2023, 10, 22);
            checkAsset("bond", 200, 0.048, 1000, expDate, assets.get(0));
            checkAsset("forward", 300, 0.0688, 3000, expDate, assets.get(1));
            checkAsset("future", 500, 0.0701, 0, expDate, assetsRemoved.get(0));
            assertEquals(1000, assets.get(0).getWithdrawList().get(0).getAmount());
            assertEquals(expDate, assets.get(0).getWithdrawList().get(0).getDate());
            assertEquals(4000, assetsRemoved.get(0).getWithdrawList().get(0).getAmount());
            assertEquals(expDate, assetsRemoved.get(0).getWithdrawList().get(0).getDate());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
