package de.fhro.inf.prg3.a11;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.fhro.inf.prg3.a11.openmensa.OpenMensaAPI;
import de.fhro.inf.prg3.a11.openmensa.OpenMensaAPIService;
import de.fhro.inf.prg3.a11.openmensa.model.Canteen;
import de.fhro.inf.prg3.a11.openmensa.model.PageInfo;
import de.fhro.inf.prg3.a11.openmensa.model.State;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Peter Kurfer
 */

public class OpenMensaApiTests {

    private static final int FHRO_MENSA_ID = 229;
    private static final String OPEN_MENSA_DATE_FORMAT = "yyyy-MM-dd";
    private final SimpleDateFormat dateFormat;
    private final OpenMensaAPI openMensaAPI;
    private final Calendar calendar;

    OpenMensaApiTests() {
        dateFormat = new SimpleDateFormat(OPEN_MENSA_DATE_FORMAT, Locale.getDefault());
        openMensaAPI = OpenMensaAPIService.getInstance().getOpenMensaAPI();
        calendar = Calendar.getInstance();
    }

    @Test
    void testGetFirstMensaPage() throws ExecutionException, InterruptedException {
        Response<List<Canteen>> canteensResponse = openMensaAPI.getCanteens().get();

        assertNotNull(canteensResponse);
        assertNotNull(canteensResponse.body());
        assertNotEquals(0, canteensResponse.body().size());

        for (Canteen c : canteensResponse.body()) {
            System.out.println(c.getName());
        }
    }

    @Test
    void testExtractPageInfo() throws ExecutionException, InterruptedException {
        Response<List<Canteen>> canteensResponse = openMensaAPI.getCanteens().get();

        PageInfo pageInfo = PageInfo.extractFromResponse(canteensResponse);

        assertNotNull(pageInfo);
        assertEquals(canteensResponse.body().size(), pageInfo.getItemCountPerPage());
        assertTrue(pageInfo.getTotalCountOfItems() > 0);
        assertTrue(pageInfo.getTotalCountOfPages() > 0);
        assertTrue(pageInfo.getCurrentPageIndex() > 0);
    }

    @Test
    void testGetMensaState() throws ExecutionException, InterruptedException {
        State mensaState = openMensaAPI.getMensaState(FHRO_MENSA_ID, dateFormat.format(calendar.getTime())).get();

        assertNotNull(mensaState);
    }

    @Test
    void testGetMultiplePages() throws ExecutionException, InterruptedException {
        Response<List<Canteen>> firstPage = openMensaAPI.getCanteens().get();

        assertNotNull(firstPage);
        assertNotNull(firstPage.body());

        PageInfo pageInfo = PageInfo.extractFromResponse(firstPage);
        for(int i = 2; i <= pageInfo.getTotalCountOfPages(); i++) {
            List<Canteen> canteensPage = openMensaAPI.getCanteens(i).get();
            assertNotNull(canteensPage);
            assertNotEquals(0, canteensPage.size());
        }
    }
}
