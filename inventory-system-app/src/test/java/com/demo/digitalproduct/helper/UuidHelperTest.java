package com.demo.digitalproduct.helper;

import com.demo.digitalproduct.exception.IllegalUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UuidHelperTest {

    @Test
    void getStringFromUUID() {
        //Arrange
        String validUUIDStr = "7bc97053-a4b4-4ca0-87f1-f34412d6f72b";
        String tooLongUUIDStr = "7bc97053-a4b4-4ca0-87f1-f34412d6f72b-7ej7d24";
        String noHyphensUUIDStr = "7bc97053a4b44ca087f1f34412d6f72b";

        //Act
        UUID validUUID = UuidHelper.getStringFromUUID(validUUIDStr);

        //Assert
        assertEquals(validUUID.toString(), validUUIDStr);

        assertThrows(IllegalUUID.class, () -> UuidHelper.getStringFromUUID(tooLongUUIDStr));
        assertThrows(IllegalUUID.class, () -> UuidHelper.getStringFromUUID(noHyphensUUIDStr));
    }
}