package Persistence;

import model.Account;
import model.Asset;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonWriterTest extends JsonTest {
    @Test
    void testWriterInvalidFile() {
        try {
            Account ac = new Account("My account");
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyAccount() {
        try {
            Account ac = new Account("My account");
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyAccount.json");
            writer.open();
            writer.write(ac);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyAccount.json");
            ac = reader.read();
            assertEquals("My account", ac.getName());
            assertEquals(0, ac.numAssets());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralAccount() {
        try {
            Account ac = new Account("My account");
            Asset a1 = new Asset("bond", 200, 4.8, 2000);
            Asset a2 = new Asset("forward", 300, 6.88, 3000);
            Asset a3 = new Asset("future", 500, 7.01, 4000);

            a1.withdrawEarly(1000);
            a3.withdrawEarly(4000);
            ac.addAsset(a1);
            ac.addAsset(a2);
            ac.addAsset(a3);
            ac.removeAsset(a3);

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralAccount.json");
            writer.open();
            writer.write(ac);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralAccount.json");
            ac = reader.read();
            assertEquals("My account", ac.getName());
            List<Asset> assets = ac.getAccount();
            List<Asset> assetsRemoved = ac.getAssetsRemoved();
            assertEquals(2, assets.size());
            assertEquals(1, assetsRemoved.size());
            checkAsset("bond", 200, 0.048, 1000, LocalDate.now(), assets.get(0));
            checkAsset("forward", 300, 0.0688, 3000, LocalDate.now(), assets.get(1));
            checkAsset("future", 500, 0.0701, 0, LocalDate.now(), assetsRemoved.get(0));
            assertEquals(1000, assets.get(0).getWithdrawList().get(0).getAmount());
            assertEquals(LocalDate.now(), assets.get(0).getWithdrawList().get(0).getDate());
            assertEquals(4000, assetsRemoved.get(0).getWithdrawList().get(0).getAmount());
            assertEquals(LocalDate.now(), assetsRemoved.get(0).getWithdrawList().get(0).getDate());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}
