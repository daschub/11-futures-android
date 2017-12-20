package de.fhro.inf.prg3.a11;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.fhro.inf.prg3.a11.openmensa.OpenMensaAPI;
import de.fhro.inf.prg3.a11.openmensa.OpenMensaAPIService;
import de.fhro.inf.prg3.a11.adapter.MealsRecyclerAdapter;
import de.fhro.inf.prg3.a11.openmensa.model.Canteen;
import de.fhro.inf.prg3.a11.openmensa.model.Meal;
import de.fhro.inf.prg3.a11.openmensa.model.PageInfo;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private static final String LOGGER_TAG = MainActivity.class.getName();
    private static final String OPEN_MENSA_DATE_FORMAT = "yyyy-MM-dd";

    private final SimpleDateFormat dateFormat;
    private final Calendar currentDate;
    private final OpenMensaAPI openMensaAPI;

    private TextView dateDisplayView;
    private RecyclerView mealsListView;
    private ArrayAdapter<Canteen> canteenAdapter;
    private MealsRecyclerAdapter mealsListAdapter;

    public MainActivity() {
        dateFormat = new SimpleDateFormat(OPEN_MENSA_DATE_FORMAT, Locale.getDefault());
        currentDate = Calendar.getInstance();
        openMensaAPI = OpenMensaAPIService.getInstance().getOpenMensaAPI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateDisplayView = findViewById(R.id.date_display_view);
        updateDateDisplay();

        /* setup canteen selection spinner */
        canteenAdapter = new ArrayAdapter<>(this, R.layout.list_item);
        Spinner canteensSelection = findViewById(R.id.canteen_selection);
        canteensSelection.setAdapter(canteenAdapter);
        canteensSelection.setOnItemSelectedListener(this);

        /* setup meals recycler view */
        mealsListAdapter = new MealsRecyclerAdapter(this);
        mealsListView = findViewById(R.id.meals_list_view);
        mealsListView.setLayoutManager(new LinearLayoutManager(this));
        mealsListView.setAdapter(mealsListAdapter);

        initCanteensSpinner();
    }

    /**
     * Initializes the top menu
     * furthermore it sets the icon of the date selection button
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /* set a font awesome icon to the date selection button */
        menu.findItem(R.id.dateSelection).setIcon(
                new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_calendar_o)
                        .color(Color.BLACK)
                        .actionBar()
        );
        return true;
    }

    /**
     * Handles selection events on menu items in the top menu
     * @param item item which was selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.dateSelection:
                new DatePickerDialog(MainActivity.this, this, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handler for the DatePicker
     * @param year selected year
     * @param month selected month
     * @param dayOfMonth selected day of month
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        currentDate.set(year, month, dayOfMonth);
        updateDateDisplay();
    }

    /**
     * Handler for the canteen spinner
     * @param adapterView adapter containing the elements in the spinner
     * @param position selected position
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        /* TODO Trigger updating of displayed meals as another canteen is now selected */
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(
                this,
                R.string.no_item_selected_toast,
                Toast.LENGTH_LONG
        ).show();
    }

    private void initCanteensSpinner() {
        /* TODO load all canteens and pass them to the canteenAdapter instance
         * hint: the first page is loaded without an index
         * afterwards you have to load the remaining pages with an index
         * use the given utility class PageInfo to get the total number of pages
         * you can create a PageInfo object by passing the return value `Response<T>`
         * you get when you fetch the first page to the static method `extractFromResponse(...)`
         * of the PageInfo class */
        openMensaAPI.getCanteens().thenApply(listResponse -> {
            List<Canteen> canteens =  listResponse.body();
            PageInfo pageInfo = PageInfo.extractFromResponse(listResponse);
            for(int i = 2; i < pageInfo.getItemCountPerPage(); i++) {

                try {
                    openMensaAPI.getCanteens(2).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Helper method to update the currently selected date
     */
    private void updateDateDisplay() {
        dateDisplayView.setText(String.format("Currently selected %s", dateFormat.format(currentDate.getTime())));
        updateMeals();
    }

    private void updateMeals() {
        final String dateString = dateFormat.format(currentDate.getTime());

        /* TODO load meals and pass them to the helper method `updateMealsListView(...)` to update the view */
    }
}
